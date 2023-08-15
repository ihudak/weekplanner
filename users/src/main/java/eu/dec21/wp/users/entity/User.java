package eu.dec21.wp.users.entity;

import eu.dec21.wp.helper.Constraints;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = { "auth_system, auth_id" } ) } )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 25)
    private String firstName;

    @Column(name = "last_name", length = 25)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 128)
    @NonNull
    private String email;

    @Column(name = "auth_system", length = 12)
    private String authSystem;

    @Column(name = "auth_id", length = 255)
    private String authID;

    public void setEmail(String email) {
        if (!email.matches(Constraints.emailRegExp)) {
            throw new eu.dec21.wp.exceptions.BadRequestException("Invalid email address");
        }
        this.email = email;
    }
}
