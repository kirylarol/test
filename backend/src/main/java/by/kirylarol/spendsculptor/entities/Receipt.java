package by.kirylarol.spendsculptor.entities;


import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Receipts")
public class Receipt {

    @Id
    @Column (name = "receipt_id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int receiptId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate date;

    @ManyToOne
    @JoinColumn (name = "account_user_id")
    private AccountUser account;
    @ManyToOne
    @JoinColumn (name = "shop_id")
    private Shop shop;

    @Column (name = "total_amount")
    private BigDecimal total;


    @OneToMany(mappedBy = "receipt" , fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Position> positionList;

    public int getReceiptId() {
        return receiptId;
    }



    public AccountUser getAccount() {
        return account;
    }

    public void setAccount(AccountUser account) {
        this.account = account;
    }

    public Shop getShop() {
        return shop;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<Position> getPositionList() {
        return positionList;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public LocalDate getDate() {
        return date;
    }


    public void setShop(Shop shop) {
        this.shop = shop;
    }


    public void setTotal(BigDecimal total) {
        this.total = total;
    }


    public void setPositionList(@NotNull List<Position> positionList) {
        this.positionList = positionList;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
