package by.kirylarol.spendsculptor.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
@Table (name = "account_to_user")
public class AccountUser {

    @ManyToOne
    @JoinColumn (name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;



    @Column
    private double weight;
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private ACCOUNT_ENUM permission;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ACCOUNT_ENUM getPermission() {
        return permission;
    }

    public void setPermission(ACCOUNT_ENUM permission) {
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    @OneToMany (mappedBy = "account")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<Receipt> receiptList;

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }
}
