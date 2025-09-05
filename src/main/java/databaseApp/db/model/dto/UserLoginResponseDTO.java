package databaseApp.db.model.dto;

import java.util.List;

public class UserLoginResponseDTO {

    private String uNumber;
    private String email;
    private String name;
    private List<String> roles;
    private String msg;

    public UserLoginResponseDTO(String uNumber, String email, String name, List<String> roles, String msg) {
        this.uNumber = uNumber;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.msg = msg;
    }

    public String getuNumber() {
        return uNumber;
    }

    public void setuNumber(String uNumber) {
        this.uNumber = uNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
