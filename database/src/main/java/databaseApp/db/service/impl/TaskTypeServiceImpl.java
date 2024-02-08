package databaseApp.db.service.impl;

import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import databaseApp.db.repository.TaskTypeRepository;
import databaseApp.db.service.TaskTypeService;
import org.springframework.stereotype.Service;
import java.util.Arrays;


@Service
public class TaskTypeServiceImpl implements TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;

    public TaskTypeServiceImpl(TaskTypeRepository taskTypeRepository) {
        this.taskTypeRepository = taskTypeRepository;
    }

    @Override
    public void initDbTypes() {

        if (taskTypeRepository.count() == 0){
            Arrays.stream(TaskTypeEnum.values())
                    .forEach(dbTypeEnum -> {
                        TaskTypeEntity taskTypeEntity = new TaskTypeEntity();
                        taskTypeEntity.setType(dbTypeEnum);
                        switch (dbTypeEnum){
                            case MPD320 -> taskTypeEntity.setCriType("MPD-");
                            case MPD330 -> taskTypeEntity.setCriType("A3-");
                            case MPD340 -> taskTypeEntity.setCriType("A4-");
                            case MPD350 -> taskTypeEntity.setCriType("A5-");
                            case MS320 -> taskTypeEntity.setCriType("MS-");
                            case MS330 -> taskTypeEntity.setCriType("A3-MS");
                            case MS340_300 -> taskTypeEntity.setCriType("A4-MS");
                            case MS340_600 -> taskTypeEntity.setCriType("A4-MS");
                            case MS350 -> taskTypeEntity.setCriType("A5-MS");
                            case MS380 -> taskTypeEntity.setCriType("A8-MS");
                            case BAW320 -> taskTypeEntity.setCriType("BAW-");
                            case JBU320 -> taskTypeEntity.setCriType("JBU-");
                            case NKS320 -> taskTypeEntity.setCriType("NKS-");
                            case WZZ320 -> taskTypeEntity.setCriType("WZZ-");
                        }

                        taskTypeRepository.save(taskTypeEntity);
                    });
        }
    }

    @Override
    public TaskTypeEntity findByType(TaskTypeEnum name) {
        return taskTypeRepository.findByType(name).orElse(null);
    }


}
