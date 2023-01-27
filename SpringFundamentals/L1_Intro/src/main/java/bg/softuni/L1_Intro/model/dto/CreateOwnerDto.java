package bg.softuni.L1_Intro.model.dto;

import java.util.List;

public class CreateOwnerDto {

    private String ownerName;


    private List<String> catNames;

    public String getOwnerName() {
        return ownerName;
    }

    public CreateOwnerDto setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public List<String> getCatNames() {
        return catNames;
    }

    public CreateOwnerDto setCatNames(List<String> catNames) {
        this.catNames = catNames;
        return this;
    }
}
