package databaseApp.db.service.impl;

import databaseApp.db.model.dto.AddTaskDTO;
import databaseApp.db.model.entity.TaskEntity;
import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.model.entity.UserEntity;
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

@Service
public class TaskServiceImpl implements TaskService {

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

        TaskTypeEntity taskTypeEntity = taskTypeService.findByType(taskTypeEnum);

        String cri = createCRI(taskNumber, taskTypeEntity).toString();
        TaskEntity task = taskRepository.findByCri(cri).orElse(null);
        return task != null;

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
