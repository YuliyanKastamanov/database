package databaseApp.db.service.impl;

import databaseApp.db.model.dto.*;
import databaseApp.db.model.entity.*;
import databaseApp.db.model.entity.enums.ResponseEnum;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import databaseApp.db.repository.OldTaskRepository;
import databaseApp.db.repository.TaskRepository;
import databaseApp.db.service.TaskService;
import databaseApp.db.service.TaskTypeService;
import databaseApp.db.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String COVERSHEET = "coversheet";
    private static final String AGENCY = "agency";
    private static final String DELETED = "Deleted";
    private static final String HAS_COMMENT = "Has Comment";
    private static final String NOT_OK = "Not OK";
    private static final String NOT_EXISTING = "Not Existing";
    private static final String SOC_STATUS_D = "D";
    private static final String TASK_STATUS_OK = "OK";
    private static final String TASK_STATUS_UPDATED = "Updated";
    private static final String A340_300 = "/300";
    private static final String A340_600 = "/600";
    private static final String SVA330 = "/330";
    private static final int MAX_OLD_REVISIONS = 3;

    private final ModelMapper modelMapper;

    private final TaskRepository taskRepository;

    private final TaskTypeService taskTypeService;

    private final UserService userService;
    private final OldTaskRepository oldTaskRepository;


    public TaskServiceImpl(ModelMapper modelMapper, TaskRepository taskRepository, TaskTypeService taskTypeService, UserService userService, OldTaskRepository oldTaskRepository) {
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
        this.taskTypeService = taskTypeService;
        this.userService = userService;
        this.oldTaskRepository = oldTaskRepository;
    }


    @Override
    public void addTask(AddTaskDTO addTaskDTO, TaskTypeEnum type, String revision) {
        addTaskDTO.setRevision(revision);
        TaskEntity task = modelMapper.map(addTaskDTO, TaskEntity.class);
        TaskTypeEntity taskTypeEntity = getTaskTypeEntity(type);
        taskTypeEntity.setDbRevision(revision);
        task.setTaskTypeEntity(taskTypeEntity);
        task.setCri(createCRI(addTaskDTO.getTaskNumber(), taskTypeEntity).toString());
        String name = getUserName();
        task.setJceName(name);
        task.setLastUpdate(LocalDateTime.now());

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
    public void addNewRevision(AddTaskDTOs addTaskDTOs) {

        for (AddTaskDTO currentTask : addTaskDTOs.getTaskNumbers()) {
            if(existByCri(currentTask.getTaskNumber(), addTaskDTOs.getType())){
                taskRevision(currentTask, addTaskDTOs.getType(),addTaskDTOs.getRevision());
            }else {
                addTask(currentTask, addTaskDTOs.getType(), addTaskDTOs.getRevision());
            }
        }
        taskTypeService.updateRevision(addTaskDTOs.getType(), addTaskDTOs.getRevision());
    }

    @Override
    public void taskRevision(AddTaskDTO currentTask, TaskTypeEnum type, String revision) {
        TaskEntity task = taskRepository
                .findByCri(extractCriType(type)+ currentTask.getTaskNumber())
                .orElse(null);
        addToOldRevision(task, task.getTaskTypeEntity().getDbRevision());

        modelMapper.map(currentTask, task);
        task.setLastUpdate(LocalDateTime.now());
        task.setRevision(revision);
        String name = getUserName();
        task.setJceName(name);
        if(task.getStatusMJob().isEmpty() || task.getStatusMJob()==null){
            task.setStatusMJob("-");
        }
        if(task.getCoversheetStatus().isEmpty() || task.getCoversheetStatus()==null){
            task.setCoversheetStatus("-");
        }
        taskRepository.save(task);

    }

    private void addToOldRevision(TaskEntity task, String revision) {
        OldTasksEntity oldRevisionTask = new OldTasksEntity();
        modelMapper.map(task, oldRevisionTask);
        oldRevisionTask.setLastUpdate(LocalDateTime.now());
        //Revision in the task old revision due to no changes,
        //Set current DB revision allows to see on which revision the task is revised
        oldRevisionTask.setRevision(revision);
        String name = getUserName();
        task.setJceName(name);
        oldTaskRepository.save(oldRevisionTask);

        // Find all tasks with the same CRI in the different revisions ordered by LastUpdateAsc.
        List<OldTasksEntity> oldRevisions = oldTaskRepository.findByCriOrderByLastUpdateAsc(task.getCri());

        // If more than 3 tasks found, remove the oldest one on index 0
        if (oldRevisions.size() > MAX_OLD_REVISIONS) {
            oldTaskRepository.delete(oldRevisions.get(0));
        }

    }

    @Override
    public List<AddTaskResponseDTO> addAllTasks(AddTaskDTOs addTaskDTOs) {
        List<AddTaskResponseDTO> results = new ArrayList<>();
        for (AddTaskDTO dto : addTaskDTOs.getTaskNumbers()) {
            AddTaskResponseDTO responseItem = new AddTaskResponseDTO();
            responseItem.setAddTaskDTO(dto);

            if (existByCri(dto.getTaskNumber(), addTaskDTOs.getType())) {
                responseItem.setStatus(ResponseEnum.ALREADY_EXIST);
            } else {
                addTask(dto, addTaskDTOs.getType(), addTaskDTOs.getRevision());
                responseItem.setStatus(ResponseEnum.ADDED);
            }

            results.add(responseItem);
        }
        taskTypeService.updateRevision(addTaskDTOs.getType(), addTaskDTOs.getRevision());
        return results;
    }

    @Override
    public List<ReturnTaskDTO> getAllTasksWithoutOk(CheckTaskStatusDTO checkTaskStatusDTO) {
        List<ReturnTaskDTO> result = new ArrayList<>();
        TaskTypeEntity taskTypeEntity = taskTypeService.findByType(checkTaskStatusDTO.getTaskType());
        String currentDbRev = taskTypeEntity.getDbRevision();

        checkTaskStatusDTO.getTaskNumbers().forEach(taskNumber -> {
            String cri = extractCriType(checkTaskStatusDTO.getTaskType()) + taskNumber;
            TaskEntity currentTask = null;
            OldTasksEntity oldTask = null;

            // find old task based on the provided revision
            if (!currentDbRev.equals(checkTaskStatusDTO.getRevision())) {
                oldTask = oldTaskRepository.findByCriAndRevision(cri, checkTaskStatusDTO.getRevision()).orElse(null);
            }

            // if no old task found or new revision is required find the task in the current DB
            if (oldTask == null) {
                currentTask = taskRepository.findByCri(cri).orElse(null);
            }

            BaseTask taskToCheck = oldTask != null ? oldTask : currentTask;

            if (taskToCheck != null) {
                List<String> statusList = new ArrayList<>();
                if (taskDeleted(taskToCheck.getSocStatus())) {
                    statusList.add(DELETED);
                }
                if (hasComment(taskToCheck.getComment())) {
                    statusList.add(HAS_COMMENT);
                }
                if (taskNotOk(taskToCheck, checkTaskStatusDTO.getProjectType())) {
                    statusList.add(NOT_OK);
                }

                //providing only tasks which are not ok
                if (!statusList.isEmpty()) {
                    ReturnTaskDTO dto = modelMapper.map(taskToCheck, ReturnTaskDTO.class);
                    dto.setExists(true);
                    dto.setStatusInfo(String.join(", ", statusList));
                    result.add(dto);
                }

                //providing all tasks
                /*ReturnTaskDTO dto = modelMapper.map(taskToCheck, ReturnTaskDTO.class);
                if (statusList.isEmpty()) {
                    statusList.add("Task Ok!");
                }
                dto.setStatusInfo(String.join(", ", statusList));
                dto.setExists(true);
                result.add(dto);*/

            } else {
                ReturnTaskDTO dto = new ReturnTaskDTO();
                dto.setTaskNumber(taskNumber);
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
            if(updatedTask == null){
                taskToReturn.setStatusInfo(NOT_EXISTING);
                taskToReturn.setExists(false);
            }else {
                taskToReturn.setStatusInfo(TASK_STATUS_UPDATED);
            }
            allTasksToReturn.add(taskToReturn);
        }

        return allTasksToReturn;
    }

    private TaskEntity updateTask(UpdateTaskDTO task) {
        TaskEntity taskToUpdate = taskRepository.findByCri(extractCriType(task.getType()) + task.getTaskNumber()).orElse(null);
        if (taskToUpdate != null) {
            modelMapper.map(task, taskToUpdate);
            taskToUpdate.setLastUpdate(LocalDateTime.now());
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
        if(socStatus == null){
            return false;
        }
            return socStatus.equalsIgnoreCase(SOC_STATUS_D);
    }

    private boolean taskNotOk(BaseTask task, String projectType) {

        return switch (projectType.toLowerCase()) {
            case COVERSHEET -> !TASK_STATUS_OK.equalsIgnoreCase(
                    task.getCoversheetStatus() != null ? task.getCoversheetStatus() : ""
            );
            case AGENCY -> !TASK_STATUS_OK.equalsIgnoreCase(
                    task.getStatusMJob() != null ? task.getStatusMJob() : ""
            );
            default -> false;
        };
    }

    private String extractCriType(TaskTypeEnum type) {
        return taskTypeService.findByType(type).getCriType();
    }


    private TaskTypeEntity getTaskTypeEntity(TaskTypeEnum type) {
        return taskTypeService
                .findByType(type);
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
