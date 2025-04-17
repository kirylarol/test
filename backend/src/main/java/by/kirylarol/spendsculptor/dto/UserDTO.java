package by.kirylarol.spendsculptor.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class  UserDTO {


    @NotEmpty(message = "Логин не может быть пустой")
    @Size(min = 2, max = 100, message = "Имя пользователя может содержать от 2 до 100 символов")
    private String login;

    @NotEmpty (message = "Пароль не может быть пустой")
    @Size (min = 6, max = 100, message = "Пароль может содержать от 6 до 100 символов")
    private String password;

    public String login() {
        return login;
    }

    public UserDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserDTO(){

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
