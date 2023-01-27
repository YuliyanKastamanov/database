package bg.softuni.L1_Intro.service.impl;

import bg.softuni.L1_Intro.model.dto.CreateOwnerDto;
import bg.softuni.L1_Intro.model.entity.CatEntity;
import bg.softuni.L1_Intro.model.entity.OwnerEntity;
import bg.softuni.L1_Intro.repository.OwnerRepository;
import bg.softuni.L1_Intro.service.OwnerService;
import org.springframework.stereotype.Service;

@Service
public class OwnerServiceImpl implements OwnerService {

    private OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository){

        this.ownerRepository = ownerRepository;
    }
    @Override
    public void createOwner(CreateOwnerDto createOwnerDto) {

        OwnerEntity owner = new OwnerEntity().setOwnerName(createOwnerDto.getOwnerName());

        createOwnerDto.getCatNames().forEach(name -> {
            CatEntity cat = new CatEntity()
                    .setCatName(name)
                    .setOwner(owner);

            owner.addCat(cat);

        });

        ownerRepository.save(owner);

    }
}
