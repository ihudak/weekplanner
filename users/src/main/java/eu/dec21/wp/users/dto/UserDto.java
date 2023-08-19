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

    @Schema(name = "authSystem", example = "facebook", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Auth system used by the user")
    @Size(max = 12)
    private String authSystem;

    @Schema(name = "authID", example = "fb534635423534524", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "User ID in the Auth system used by the user")
    @Size(max = 255)
    private String authID;

    public void setEmail(String email) {
        if (!email.matches(Constraints.emailRegExp)) {
            throw new eu.dec21.wp.exceptions.BadRequestException("Invalid email address: " + email);
        }
        this.email = email;
    }
}
