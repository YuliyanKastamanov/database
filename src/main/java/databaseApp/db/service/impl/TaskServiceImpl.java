package databaseApp.db.service.impl;

import databaseApp.db.model.dto.AddTaskDTO;
import databaseApp.db.model.dto.AddTaskResponseDTO;
import databaseApp.db.model.dto.CheckTaskStatusDTO;
import databaseApp.db.model.dto.ReturnTaskDTO;
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

@Service
public class TaskServiceImpl implements TaskService {

    private static final String COVERSHEET = "Coversheet";
    private static final String AGENCY = "Agency";

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
    public boolean existByCri(String taskNumber, String taskTypeEnum) {

        String cri = extractCriType(taskTypeEnum) + "-" + taskNumber;
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
    public void changeTask(AddTaskDTO currentTask) {
        TaskEntity task = taskRepository
                .findByCri(extractCriType(currentTask.getType().name()) +"-"+ currentTask.getTaskNumber())
                .orElse(null);

        task.setRevision(currentTask.getRevision());
        task.setSocStatus(currentTask.getSocStatus());
        task.setSocDescription(currentTask.getSocDescription());
        task.setLastUpdate(LocalDate.now());
        String name = getUserName();
        task.setJceName(name);
        task.setStatusMJob(null);
        task.setCoversheetStatus(null);
        taskRepository.save(task);

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
            if(existByCri(currentTask.getTaskNumber(), currentTask.getType().name())){
                changeTask(currentTask);
            }else {
                addTask(currentTask);
            }
        }
    }

    @Override
    public List<AddTaskResponseDTO> addAllTasks(List<AddTaskDTO> addTaskDTOs) {
        List<AddTaskResponseDTO> results = new ArrayList<>();
        for (AddTaskDTO dto : addTaskDTOs) {
            AddTaskResponseDTO responseItem = new AddTaskResponseDTO();
            responseItem.setAddTaskDTO(dto);

            if (existByCri(dto.getTaskNumber(), dto.getType().name())) {
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

        List<TaskEntity> allTasks = new ArrayList<>();
        checkTaskStatusDTO.getTaskNumbers().forEach(task -> {
            String cri = extractCriType(checkTaskStatusDTO.getTaskType()) + "-" + task;
            TaskEntity taskEntity = taskRepository.findByCri(cri).orElse(null);
            allTasks.add(taskEntity);
        });
        return getNotOkTasks(allTasks, checkTaskStatusDTO.getProjectType());
    }

    private List<ReturnTaskDTO> getNotOkTasks(List<TaskEntity> allTasks, String projectType) {
        List<ReturnTaskDTO> allTasksToReturn = new ArrayList<>();

        for (TaskEntity task : allTasks){
            if(taskNotOk(task, projectType) || taskDeleted(task.getSocStatus()) || hasComment(task.getComment())){
                allTasksToReturn.add( modelMapper.map(task, ReturnTaskDTO.class));
            }
        }

        return allTasksToReturn;
    }

    private boolean hasComment(String comment) {
        return !(comment == null || comment.isBlank());
    }

    private boolean taskDeleted(String socStatus) {
            return socStatus.equals("D");
    }

    private boolean taskNotOk(TaskEntity task, String projectType) {

        return projectType.equals(COVERSHEET) && (task.getCoversheetStatus()== null || task.getCoversheetStatus().isBlank())
                || projectType.equals(AGENCY) && (task.getStatusMJob()== null || task.getStatusMJob().isBlank());
    }

    private String extractCriType(String type) {

        if(type.length() == 6){
            return type.substring(0, 3);
        }else {
            return type.substring(0, 2);
        }
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
            cri.append(taskType.getCriType()).append(taskNumber).append("/300");
        }else if (taskType.getType() == TaskTypeEnum.MS340_600){
            cri.append(taskType.getCriType()).append(taskNumber).append("/600");
        }else {
            cri.append(taskType.getCriType()).append(taskNumber);
        }
        return cri;
    }

}
