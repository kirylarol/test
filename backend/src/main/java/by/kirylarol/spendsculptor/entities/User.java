package by.kirylarol.spendsculptor.entities;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
@Table(name = "userprofile")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "user_id")
    private int id;

    @OneToOne (cascade = CascadeType.ALL)
    @JoinColumn (name = "identity_id")
    private Identity identity;

    @Column(unique = true)
    private String login;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private RolesSystem role;

    @OneToMany (mappedBy = "user")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<AccountUser> accountUsers;

    public User(User user) {
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.identity = new Identity(user.getIdentity());
    }

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RolesSystem getRole() {
        return role;
    }

    public void setRole(RolesSystem role) {
        this.role = role;
    }


    @JsonIgnore
    public List<AccountUser> getAccountUsers() {
        return accountUsers;
    }

    public void setAccountUsers(List<AccountUser> accountUsers) {
        this.accountUsers = accountUsers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User user = (User) obj;
        return user.getLogin().equals(this.getLogin());
    }
}
