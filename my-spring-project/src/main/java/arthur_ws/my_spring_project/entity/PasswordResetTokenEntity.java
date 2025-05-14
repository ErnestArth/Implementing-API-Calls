package arthur_ws.my_spring_project.entity;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "password_reset_tokens")
public class PasswordResetTokenEntity  implements Serializable {

    @Serial
    private static final long serialVersionUID = 2650213631845619862L;

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "users_id")
    private UserEntity userDetails;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
