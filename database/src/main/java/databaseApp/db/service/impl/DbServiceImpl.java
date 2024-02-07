package databaseApp.db.service.impl;

import databaseApp.db.model.dto.DbAddTaskDTO;
import databaseApp.db.model.entity.DbEntity;
import databaseApp.db.model.entity.TypeEntity;
import databaseApp.db.model.entity.enums.TypeEnum;
import databaseApp.db.repository.DbRepository;
import databaseApp.db.service.DbService;
import databaseApp.db.service.DbTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class DbServiceImpl implements DbService {

    private final ModelMapper modelMapper;

    private final DbRepository dbRepository;

    private final DbTypeService dbTypeService;


    public DbServiceImpl(ModelMapper modelMapper, DbRepository dbRepository, DbTypeService dbTypeService) {
        this.modelMapper = modelMapper;
        this.dbRepository = dbRepository;
        this.dbTypeService = dbTypeService;
    }


    @Override
    public void addTask(DbAddTaskDTO dbAddTaskDTO) {
        DbEntity task = modelMapper.map(dbAddTaskDTO, DbEntity.class);


        TypeEntity typeEntity = dbTypeService.findByType(dbAddTaskDTO.getType());
        task.setTypeEntity(typeEntity);
        task.setCri(typeEntity.getType().name() + "-" + dbAddTaskDTO.getTaskNumber());

        dbRepository.save(task);

    }


}
