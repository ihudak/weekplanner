package eu.dec21.wp.categories.dto;

import eu.dec21.wp.helper.Constraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;

    @NonNull
    @Schema(name="name", example = "My Project", requiredMode = Schema.RequiredMode.REQUIRED, description = "Category or Project to group ToDo items")
    private String name;

    @NonNull
    @Min(Constraints.minPrio)
    @Max(Constraints.maxPrio)
    @Schema(name="priority", example = "50", requiredMode = Schema.RequiredMode.REQUIRED, description = "Default priority for all ToDo items in the Category. Between 0 and 100")
    private Integer priority;

    @Schema(name="color", example = "#A52A2A", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "HTML Color code to differentiate ToDo items of the Category")
    private String color;

    @NonNull
    @Schema(name="userId", example = "45", requiredMode = Schema.RequiredMode.REQUIRED, description = "ID of the owner of the Category")
    private Long userId;
}
