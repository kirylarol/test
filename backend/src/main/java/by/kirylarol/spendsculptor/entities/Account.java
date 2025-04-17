package by.kirylarol.spendsculptor.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table (name = "Account")
public class Account {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "account_id")
    int id;


    @Column(name = "name")
    private String name;

    @Column (name = "created_at")
    private LocalDate dateCreated;

    public Account(String name, LocalDate date) {
        this.name = name;
        this.dateCreated = date;
    }

    public Account() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return String.valueOf(dateCreated);
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<AccountUser> getAccountUsers() {
        return accountUsers;
    }

    public void setAccountUsers(List<AccountUser> accountUsers) {
        this.accountUsers = accountUsers;
    }

    public List<Goal> getGoalList() {
        return goalList;
    }

    public void setGoalList(List<Goal> goalList) {
        this.goalList = goalList;
    }




    @JsonIgnore
    @OneToMany (orphanRemoval = true, mappedBy = "account")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<AccountUser> accountUsers;

    @JsonIgnore
    @OneToMany (mappedBy = "account")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Goal> goalList;



}
