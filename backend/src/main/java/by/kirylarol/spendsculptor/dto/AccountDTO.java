package by.kirylarol.spendsculptor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.sql.Date;
import java.time.LocalDate;

public class AccountDTO {


    @NotEmpty(message = "Название счета не может быть пустым")
    @Size(min = 2, max = 100, message = "Название счета может содержать от 2 до 100 символов")
    private String name;

    private long date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
