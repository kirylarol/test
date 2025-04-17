package by.kirylarol.spendsculptor.dto;

import by.kirylarol.spendsculptor.entities.RolesSystem;

public class UserInfoDTO {

    private final String login;
    private final String name;
    private final String surname;

    private final RolesSystem role;

    public UserInfoDTO(String login, String name, String surname, RolesSystem role) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public RolesSystem getRole() {
        return role;
    }
}
