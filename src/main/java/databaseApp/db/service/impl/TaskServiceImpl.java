package databaseApp.db.service.impl;

import databaseApp.db.model.dto.*;
import databaseApp.db.model.entity.TaskEntity;
import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.model.entity.UserEntity;
import databaseApp.db.model.entity.enums.ResponseEnum;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import databaseApp.db.repository.TaskRepository;
import databaseApp.db.service.TaskService;
import databaseApp.db.service.TaskTypeService;
import databaseApp.db.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String COVERSHEET = "Coversheet";
    private static final String AGENCY = "Agency";
    private static final String DELETED = "Deleted";
    private static final String HAS_COMMENT = "Has Comment";
    private static final String NOT_OK = "Not OK";
    private static final String NOT_EXISTING = "Not Existing";
    private static final String SOC_STATUS_D = "D";
    private static final String TASK_STATUS_OK = "OK";
    private static final String A340_300 = "/300";
    private static final String A340_600 = "/600";
    private static final String SVA330 = "/330";

    private final ModelMapper modelMapper;

    private final TaskRepository taskRepository;

    private final TaskTypeService taskTypeService;

    private final UserService userService;


    public TaskServiceImpl(ModelMapper modelMapper, TaskRepository taskRepository, TaskTypeService taskTypeService, UserService userService) {
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
        this.taskTypeService = taskTypeService;
        this.userService = userService;
    }


    @Override
    public void addTask(AddTaskDTO addTaskDTO) {
        TaskEntity task = modelMapper.map(addTaskDTO, TaskEntity.class);
        TaskTypeEntity taskTypeEntity = getTaskTypeEntity(addTaskDTO);
        task.setTypeEntity(taskTypeEntity);
        task.setCri(createCRI(addTaskDTO.getTaskNumber(), taskTypeEntity).toString());

        String name = getUserName();
        task.setJceName(name);
        task.setLastUpdate(LocalDate.now());
        taskRepository.save(task);

    }

    @Override
    public boolean existByCri(String taskNumber, TaskTypeEnum taskTypeEnum) {

        String cri = extractCriType(taskTypeEnum) + taskNumber;
        TaskEntity task = taskRepository.findByCri(cri).orElse(null);
        return task != null;

    }

    @Override
    public List<ReturnTaskDTO> getAllTasksByTaskType(TaskTypeEnum type) {
        TaskTypeEntity taskTypeEntity = taskTypeService.findByType(type);
        List<TaskEntity> tasks = taskRepository.findAllByTaskTypeEntity(taskTypeEntity);
        List<ReturnTaskDTO> tasksToReturn = new ArrayList<>();
        tasks.forEach(t -> {
                    ReturnTaskDTO currentTask = modelMapper.map(t, ReturnTaskDTO.class);
                    tasksToReturn.add(currentTask);
                });

        tasksToReturn.sort(Comparator.comparing(ReturnTaskDTO::getCri));
        return tasksToReturn;
    }



    @Override
    public List<ReturnTaskDTO> getAll() {
        List<TaskEntity> allTasks = taskRepository.findAll();
        List<ReturnTaskDTO> allTasksToReturn = new ArrayList<>();
        allTasks.forEach(task -> {
            ReturnTaskDTO taskToReturn = modelMapper.map(task, ReturnTaskDTO.class);
            allTasksToReturn.add(taskToReturn);
        });
        allTasksToReturn.sort(Comparator.comparing(ReturnTaskDTO::getCri));
        return allTasksToReturn;
    }

    @Override
    public void addNewRevision(List<AddTaskDTO> addTaskDTOs) {
        for (AddTaskDTO currentTask : addTaskDTOs) {
            if(existByCri(currentTask.getTaskNumber(), currentTask.getType())){
                taskRevision(currentTask);
            }else {
                addTask(currentTask);
            }
        }
    }

    @Override
    public void taskRevision(AddTaskDTO currentTask) {
        TaskEntity task = taskRepository
                .findByCri(extractCriType(currentTask.getType())+ currentTask.getTaskNumber())
                .orElse(null);
        taskTypeService.updateRevision(currentTask.getType(), currentTask.getRevision());
        modelMapper.map(currentTask, task);
        task.setLastUpdate(LocalDate.now());
        String name = getUserName();
        task.setJceName(name);
        task.setStatusMJob("-");
        task.setCoversheetStatus("-");
        taskRepository.save(task);

    }

    @Override
    public List<AddTaskResponseDTO> addAllTasks(List<AddTaskDTO> addTaskDTOs) {
        List<AddTaskResponseDTO> results = new ArrayList<>();
        for (AddTaskDTO dto : addTaskDTOs) {
            AddTaskResponseDTO responseItem = new AddTaskResponseDTO();
            responseItem.setAddTaskDTO(dto);

            if (existByCri(dto.getTaskNumber(), dto.getType())) {
                responseItem.setStatus(ResponseEnum.ALREADY_EXIST);
            } else {
                addTask(dto);
                responseItem.setStatus(ResponseEnum.ADDED);
            }

            results.add(responseItem);
        }
        return results;
    }

    @Override
    public List<ReturnTaskDTO> getAllTasksWithoutOk(CheckTaskStatusDTO checkTaskStatusDTO) {
        List<ReturnTaskDTO> result = new ArrayList<>();
        checkTaskStatusDTO.getTaskNumbers().forEach(task -> {
            String cri = extractCriType(checkTaskStatusDTO.getTaskType()) + task;
            Optional<TaskEntity> taskEntityOpt = taskRepository.findByCri(cri);
            ReturnTaskDTO dto = new ReturnTaskDTO();
            dto.setTaskNumber(task);

            if (taskEntityOpt.isPresent()) {
                TaskEntity taskEntity = taskEntityOpt.get();
                List<String> statusList = new ArrayList<>();
                if (taskDeleted(taskEntity.getSocStatus())) {
                    statusList.add(DELETED);
                }
                if (hasComment(taskEntity.getComment())) {
                    statusList.add(HAS_COMMENT);
                }
                if (taskNotOk(taskEntity, checkTaskStatusDTO.getProjectType())) {
                    statusList.add(NOT_OK);
                }

                dto = modelMapper.map(taskEntity, ReturnTaskDTO.class);
                dto.setExists(true);
                if (!statusList.isEmpty()){
                    dto.setStatusInfo(String.join(", ", statusList));
                    result.add(dto);
                }

            } else {
                // Task does not exist
                dto.setExists(false);
                dto.setStatusInfo(NOT_EXISTING);
                result.add(dto);
            }
        });
        return result;
    }

    @Override
    public List<ReturnTaskDTO> updateAllTasks(List<UpdateTaskDTO> updateTaskDTOS) {
        List<ReturnTaskDTO> allTasksToReturn = new ArrayList<>();
        for (UpdateTaskDTO task : updateTaskDTOS){
            TaskEntity updatedTask = updateTask(task);
            ReturnTaskDTO taskToReturn = modelMapper.map(updatedTask, ReturnTaskDTO.class);
            allTasksToReturn.add(taskToReturn);
        }

        return allTasksToReturn;
    }

    private TaskEntity updateTask(UpdateTaskDTO task) {
        TaskEntity taskToUpdate = taskRepository.findByCri(extractCriType(task.getType()) + task.getTaskNumber()).orElse(null);
        if (taskToUpdate != null) {
            modelMapper.map(task, taskToUpdate);
            taskToUpdate.setLastUpdate(LocalDate.now());
            String name = getUserName();
            taskToUpdate.setJceName(name);
            taskRepository.save(taskToUpdate);
        }
        return taskToUpdate;
    }

    private boolean hasComment(String comment) {
        return !(comment == null || comment.isBlank());
    }

    private boolean taskDeleted(String socStatus) {
            return socStatus.equalsIgnoreCase(SOC_STATUS_D);
    }

    private boolean taskNotOk(TaskEntity task, String projectType) {

        return projectType.equals(COVERSHEET) && (!task.getCoversheetStatus().equalsIgnoreCase(TASK_STATUS_OK))
                || projectType.equals(AGENCY) && (!task.getStatusMJob().equalsIgnoreCase(TASK_STATUS_OK));
    }

    private String extractCriType(TaskTypeEnum type) {
        return taskTypeService.findByType(type).getCriType();
    }


    private TaskTypeEntity getTaskTypeEntity(AddTaskDTO addTaskDTO) {
        return taskTypeService
                .findByType(addTaskDTO.getType());
    }

    private String  getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uNumber = authentication.getName();
        UserEntity user = userService.findByUNumber(uNumber);
        return user.getName();
    }

    private static StringBuilder createCRI(String taskNumber, TaskTypeEntity taskType) {
        StringBuilder cri = new StringBuilder();
        if(taskType.getType() == TaskTypeEnum.MS340_300){
            cri.append(taskType.getCriType()).append(taskNumber).append(A340_300);
        }else if (taskType.getType() == TaskTypeEnum.MS340_600){
            cri.append(taskType.getCriType()).append(taskNumber).append(A340_600);
        }else if (taskType.getType() == TaskTypeEnum.SVA330){
            cri.append(taskType.getCriType()).append(taskNumber).append(SVA330);
        }else {
            cri.append(taskType.getCriType()).append(taskNumber);
        }
        return cri;
    }


}
