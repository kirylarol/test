package by.kirylarol.spendsculptor.dto;

import by.kirylarol.spendsculptor.entities.Position;

import java.math.BigDecimal;

public class PostitionWithDateDTO {
    private String name;
    private BigDecimal price;
    private long date;

    public PostitionWithDateDTO(Position position) {
        this.name = position.getName();
        this.price = position.getPrice();
        date = position.getReceipt().getDate().toEpochDay();
    }



    public long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
