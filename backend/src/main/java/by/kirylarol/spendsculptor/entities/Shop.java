package by.kirylarol.spendsculptor.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table (name = "shop")
public class Shop {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "shop_id")
    private int id;

    @Column
    private String name;

    @OneToMany (mappedBy = "shop")
    private List<Receipt> receiptList;

    public void setId(int id) {
        this.id = id;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public List<Receipt> getReceiptList() {
        return receiptList;
    }



    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }
}
