package sit.project221.oasipbackend.entities;

import lombok.Getter;
import lombok.Setter;
import sit.project221.oasipbackend.utils.Roles;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

//    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private String role;
//    private Roles role;

    @Column(name = "password" , nullable = false , length = 14)
    private String password;

    @Column(name = "createdOn", nullable = false , insertable = false)
    private LocalDateTime createdOn;

    @Column(name = "updatedOn", nullable = false , insertable = false , updatable = false)
    private LocalDateTime updatedOn;

}