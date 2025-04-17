package by.kirylarol.spendsculptor.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table (name = "identity")
public class Identity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "identity_id")
    private int id;

    @Column
    private String surname;

    @Column
    private String name;

    @OneToOne (mappedBy = "identity")
    private User user;

    public Identity(Identity identity) {
        this.surname = identity.getSurname();
        this.name = identity.getName();
    }

    public Identity() {
    }

    public Identity(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }



    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }
}
