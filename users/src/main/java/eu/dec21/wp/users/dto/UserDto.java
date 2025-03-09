package eu.dec21.wp.users.dto;

import eu.dec21.wp.helper.Constraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @Min(0)
    private Long id;

    @Schema(name="firstName", example = "Peter", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "First Name")
    @Size(max = 25)
    private String firstName;

    @Schema(name="lastName", example = "Brown", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Last Name")
    @Size(max = 25)
    private String lastName;

    @Pattern(regexp = Constraints.emailRegExp)
    @NonNull
    @Size(min = 5)
    @Schema(name = "email", example = "pbrown@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED, description = "email address")
    private String email;

    @Pattern(regexp = Constraints.passwordRegExp)
    @Size(min = 8)
    @Schema(name = "password", example = "p@ssw0rD!", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "password")
    private String password;

    @Schema(name = "authSystem", example = "facebook", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Auth system used by the user")
    @Size(max = 12)
    private String authSystem;

    @Schema(name = "authID", example = "fb534635423534524", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "User ID in the Auth system used by the user")
    @Size(max = 255)
    private String authID;

    @Builder.Default
    @Schema(name = "suspended", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Indicates whether user account is suspended")
    private boolean suspended = false;

    public void setEmail(String email) {
        if (!email.matches(Constraints.emailRegExp)) {
            throw new eu.dec21.wp.exceptions.BadRequestException("Invalid email address: " + email);
        }
        this.email = email;
    }

    public void setPassword(String password) {
        if (!password.matches(Constraints.passwordRegExp)) {
            throw new eu.dec21.wp.exceptions.BadRequestException("Password must contain at least 1 lower case, 1 upper case letter, 1 number, 1 special character, and be minimum of 8 characters long");
        }
        this.password = password;
    }

    public boolean equals(UserDto o) {
        // different emails
        if (!this.email.equals(o.email)) {
            return false;
        }
        // different auth systems
        if (this.authID != null && o.authID == null || this.authID == null && o.authID != null) {
            return false;
        }
        if (this.authID != null && !this.authID.equals(o.authID)) {
            return false;
        }
        if (this.authSystem != null && o.authSystem == null || this.authSystem == null && o.authSystem != null) {
            return false;
        }
        if (this.authSystem != null && !this.authSystem.equals(o.authSystem)) {
            return false;
        }
        // different name
        if (this.firstName != null && o.firstName == null || this.firstName == null && o.firstName != null) {
            return false;
        }
        if (this.firstName != null && !this.firstName.equals(o.firstName)) {
            return false;
        }
        if (this.lastName != null && o.lastName == null || this.lastName == null && o.lastName != null) {
            return false;
        }
        return this.lastName == null || this.lastName.equals(o.lastName);
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public int hashCode() {
        return this.id.intValue();
    }
}
