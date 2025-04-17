package by.kirylarol.spendsculptor.dto;

public class AccountInfoDTO {
    private final int id;
    private final String name;
    private final int number;
    private final String username;

    public AccountInfoDTO(int id, String name, int number, String username) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }
}
