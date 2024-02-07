package databaseApp.db.service.impl;

import databaseApp.db.model.entity.TypeEntity;
import databaseApp.db.model.entity.enums.TypeEnum;
import databaseApp.db.repository.DbTypeRepository;
import databaseApp.db.service.DbTypeService;
import org.springframework.stereotype.Service;
import java.util.Arrays;


@Service
public class DbTypeServiceImpl implements DbTypeService {

    private final DbTypeRepository dbTypeRepository;

    public DbTypeServiceImpl(DbTypeRepository dbTypeRepository) {
        this.dbTypeRepository = dbTypeRepository;
    }

    @Override
    public void initDbTypes() {

        if (dbTypeRepository.count() == 0){
            Arrays.stream(TypeEnum.values())
                    .forEach(dbTypeEnum -> {
                        TypeEntity typeEntity = new TypeEntity();
                        typeEntity.setType(dbTypeEnum);
                        dbTypeRepository.save(typeEntity);
                    });
        }
    }

    @Override
    public TypeEntity findByType(TypeEnum name) {
        return dbTypeRepository.findByType(name).orElse(null);
    }


}
