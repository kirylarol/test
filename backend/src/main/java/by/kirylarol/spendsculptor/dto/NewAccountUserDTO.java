package by.kirylarol.spendsculptor.dto;

import by.kirylarol.spendsculptor.entities.ACCOUNT_ENUM;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class NewAccountUserDTO {


    @NotEmpty(message = "Логин не может быть пустой")
    @Size(min = 2, max = 100, message = "Имя пользователя может содержать от 2 до 100 символов")
    private String username;

    @Min(value = 1, message = "Значение должно быть не меньше 1")
    @Max(value = 100, message = "Значение должно быть не больше 100")
    private int weight;
    private ACCOUNT_ENUM ROLE;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ACCOUNT_ENUM getROLE() {
        return ROLE;
    }

    public void setROLE(ACCOUNT_ENUM ROLE) {
        this.ROLE = ROLE;
    }
}
