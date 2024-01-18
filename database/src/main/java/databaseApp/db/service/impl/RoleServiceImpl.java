package databaseApp.db.service.impl;

import databaseApp.db.model.entity.Role;
import databaseApp.db.model.entity.enums.RoleEnum;
import databaseApp.db.repository.RoleRepository;
import databaseApp.db.service.RoleService;
import jdk.jfr.Category;
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
                        Role role = new Role();
                        role.setRole(roleEnum);
                        roleRepository.save(role);
                    });
        }

    }
}
