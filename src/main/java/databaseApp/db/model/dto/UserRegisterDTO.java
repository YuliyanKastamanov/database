package databaseApp.db.model.dto;

import databaseApp.db.model.entity.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRegisterDTO {

    @Size(min = 7, max = 7, message = "please provide correct username U + 6 digits")
    @NotBlank
    private String uNumber;

    @Size(min = 5, max = 20, message = "please provide correct password")
    @NotBlank
    private String password;

    @Size(min = 5, max = 20, message = "please provide correct password")
    @NotBlank
    private String confirmPassword;

    @Size(min = 5, max = 30, message = "please provide correct name")
    @NotBlank
    private String name;

    @Email(message = "please provide correct email")
    @NotBlank
    private String email;

    @NotNull(message = "please provide correct role")
    private RoleEnum role;

    public UserRegisterDTO() {
    }

    public String getuNumber() {
        return uNumber;
    }

    public void setuNumber(String uNumber) {
        this.uNumber = uNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
}
