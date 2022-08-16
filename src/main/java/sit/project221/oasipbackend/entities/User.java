package sit.project221.oasipbackend.entities;

import lombok.Getter;
import lombok.Setter;

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

    @Lob
//    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "createdOn", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updateOn", nullable = false)
    private LocalDateTime updateOn;

    public enum role {
        admin, lecturer, student;
    }

}