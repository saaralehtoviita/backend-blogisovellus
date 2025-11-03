package backend25.blogisovellus.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="USERS")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id generoidaan automaattisesti tietokantaan
    @Column(name="user_id")
    private Long userId;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;


    @Column(unique= true, name="user_name")
    private String userName;

    @Column(name="password_hashed")
    private String passwordHashed;

    @Column(name="app_role")
    private String role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "writer")
    private List<Post> userPosts = new ArrayList<>();

    //tyhjä konstruktori
    public AppUser() {
    }

    //konstruktori ilman postauksia
    public AppUser(String firstName, String lastName, String userName, String passwordHashed, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.passwordHashed = passwordHashed;
        this.role = role;
    }

    //konstruktori kaikilla attribuuteilla
    public AppUser(String firstName, String lastName, String userName, String passwordHashed, String role,
            List<Post> userPosts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.passwordHashed = passwordHashed;
        this.role = role;
        this.userPosts = userPosts;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordHashed() {
        return passwordHashed;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Post> getUserPosts() {
        if (userPosts.size() > 0) {
        return userPosts;
        } else {
            return null;
        }
    }

    public void setUserPosts(List<Post> userPosts) {
        this.userPosts = userPosts;
    }



    

    



}
