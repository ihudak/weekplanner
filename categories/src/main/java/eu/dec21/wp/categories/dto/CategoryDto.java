package eu.dec21.wp.categories.dto;

import eu.dec21.wp.helper.Constraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;

    @NonNull
    @Schema(name="name", example = "My Project", requiredMode = Schema.RequiredMode.REQUIRED, description = "Category or Project to group ToDo items")
    @Size(min = 2, max = 25)
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
    @Min(1)
    private Long userId;

    @Builder.Default
    @Schema(name="deleted", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Indicates whether Category is deleted")
    private boolean deleted = false;

    public boolean equals(CategoryDto c) {
        // different names
        if (!this.name.equals(c.name)) {
            return false;
        }
        // different priorities
        if (!this.priority.equals(c.priority)) {
            return false;
        }
        // different users
        if (!this.userId.equals(c.userId)) {
            return false;
        }
        // different colors
        if (this.color == null && c.color != null || this.color != null && c.color == null) {
            return false;
        }
        return this.color == null || this.color.equals(c.color);
    }
}
