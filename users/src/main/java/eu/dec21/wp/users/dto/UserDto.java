package eu.dec21.wp.users.dto;

import eu.dec21.wp.helper.Constraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @Schema(name="firstName", example = "Peter", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "First Name")
    private String firstName;

    @Schema(name="lastName", example = "Brown", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Last Name")
    private String lastName;

    @Pattern(regexp = Constraints.emailRegExp)
    @NonNull
    @Schema(name = "email", example = "pbrown@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED, description = "email address")
    private String email;

    @Schema(name = "authSystem", example = "facebook", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Auth system used by the user")
    private String authSystem;

    @Schema(name = "authID", example = "fb534635423534524", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "User ID in the Auth system used by the user")
    private String authID;
}
