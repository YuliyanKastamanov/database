package databaseApp.db.service.impl;

import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import databaseApp.db.repository.TaskTypeRepository;
import databaseApp.db.service.TaskTypeService;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;


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
                            case MPD320, MPD737NG -> taskTypeEntity.setCriType("MPD-");
                            case MPD330 -> taskTypeEntity.setCriType("A3-");
                            case MPD340 -> taskTypeEntity.setCriType("A4-");
                            case MPD350 -> taskTypeEntity.setCriType("A5-");
                            case MS320 -> taskTypeEntity.setCriType("MS-MS");
                            case MS330 -> taskTypeEntity.setCriType("A3-MS");
                            case MS340_300, MS340_600 -> taskTypeEntity.setCriType("A4-MS");
                            case MS350 -> taskTypeEntity.setCriType("A5-MS");
                            case MS380 -> taskTypeEntity.setCriType("A8-MS");
                            case BAW320 -> taskTypeEntity.setCriType("BAW-");
                            case JBU320 -> taskTypeEntity.setCriType("JBU-");
                            case NKS320 -> taskTypeEntity.setCriType("NKS-");
                            case WZZ320 -> taskTypeEntity.setCriType("WZZ-");
                            case MPD737MAX -> taskTypeEntity.setCriType("37M-");
                            case MPD787 -> taskTypeEntity.setCriType("B87-");
                            case MS787 -> taskTypeEntity.setCriType("B87-MS");
                            case SVA320, SVA330 -> taskTypeEntity.setCriType("SVA-");
                            case OCN320 -> taskTypeEntity.setCriType("OCN-");
                            case NAX737 -> taskTypeEntity.setCriType("NAX-");
                            case EZY320 -> taskTypeEntity.setCriType("EZY-");
                            case EWG320 -> taskTypeEntity.setCriType("EWG-");
                            case PGT320 -> taskTypeEntity.setCriType("PGT-");
                            case FTT320 -> taskTypeEntity.setCriType("FTT-");
                            case VIV320 -> taskTypeEntity.setCriType("VIV-");
                            case VIR350 -> taskTypeEntity.setCriType("VIR-");
                            case IBE350 -> taskTypeEntity.setCriType("IBE-");
                            case JZR320 -> taskTypeEntity.setCriType("JZR-");
                            case LAN320 -> taskTypeEntity.setCriType("LAN-");


                        }

                        taskTypeRepository.save(taskTypeEntity);
                    });
        }
    }

    @Override
    public TaskTypeEntity findByType(TaskTypeEnum name) {
        return taskTypeRepository.findByType(name).orElse(null);
    }

    @Override
    public void updateRevision(TaskTypeEnum taskType, String dbRevision) {
        TaskTypeEntity taskTypeEntity = findByType(taskType);
        taskTypeEntity.setDbRevision(dbRevision);
        taskTypeRepository.save(taskTypeEntity);
    }

    @Override
    public List<TaskTypeEntity> getAll() {
        return taskTypeRepository.findAll();
    }


}
