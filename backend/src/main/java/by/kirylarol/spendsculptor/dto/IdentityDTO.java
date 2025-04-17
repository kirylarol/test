package by.kirylarol.spendsculptor.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class IdentityDTO {


    @NotEmpty (message =  "Имя не может быть пустым")
    @Size (min = 2, max = 100, message = "Фамилия может содержать от 2 до 100 символов")
    private String name;
    @NotEmpty (message = "Фамилия не может быть пустой")
    @Size (min = 2, max = 100, message = "Имя может содержать от 2 до 100 символов")
    private String surname;

    public IdentityDTO(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public IdentityDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
