package databaseApp.db.service.impl;

import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.enums.RoleEnum;
import databaseApp.db.repository.RoleRepository;
import databaseApp.db.service.RoleService;
import org.springframework.stereotype.Service;
import java.util.Arrays;


@Service
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void initRoles() {

        if (roleRepository.count() == 0){
            Arrays.stream(RoleEnum.values())
                    .forEach(roleEnum -> {
                        RoleEntity roleEntity = new RoleEntity();
                        roleEntity.setRole(roleEnum);
                        roleRepository.save(roleEntity);
                    });
        }

    }

    @Override
    public RoleEntity findByName(RoleEnum role) {
        return roleRepository.findByRole(role)
                .orElse(null);
    }


}
