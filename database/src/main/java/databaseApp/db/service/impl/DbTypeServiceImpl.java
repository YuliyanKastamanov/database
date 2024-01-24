package databaseApp.db.service.impl;

import databaseApp.db.model.entity.DbType;
import databaseApp.db.model.entity.enums.DbTypeEnum;
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
            Arrays.stream(DbTypeEnum.values())
                    .forEach(dbTypeEnum -> {
                        DbType dbType = new DbType();
                        dbType.setType(dbTypeEnum);
                        dbTypeRepository.save(dbType);
                    });
        }
    }
}
