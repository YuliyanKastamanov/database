package databaseApp.db.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserLoginDTO {

    @Size(min = 7, max = 7, message = "please provide correct username U + 6 digits")
    @NotBlank
    private String uNumber;

    @Size(min = 5, max = 20, message = "please provide correct password")
    @NotBlank
    private String password;

    public UserLoginDTO() {
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
}
