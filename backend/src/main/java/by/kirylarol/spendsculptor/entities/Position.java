package by.kirylarol.spendsculptor.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table (name = "positions")
public class Position {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "position_id")
    private int positionId;

    @Column
    private String name;

    @Column
    private BigDecimal price;

    @ManyToOne(optional = true)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(optional = true)
    @JoinColumn (name = "receipt_id")
    private Receipt receipt;

    public Position(String name) {
        this.name = name;
    }

    public Position(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Position{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    public Position() {
    }

    public Position(BigDecimal price) {
        this.price = price;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        name = name.replaceAll(" [\"0-9].+","");
        this.name = name;
    }



    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }


    public void setCategory(Category category) {
        this.category = category;
    }


    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getPositionId() {
        return positionId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    @JsonIgnore
    public Receipt getReceipt() {
        return receipt;
    }
}
