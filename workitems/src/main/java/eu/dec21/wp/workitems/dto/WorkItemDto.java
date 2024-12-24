package eu.dec21.wp.workitems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkItemDto {

    @Min(0)
    private Long id;

    @NonNull
    @Schema(name="name", example = "Implement New Agent", requiredMode = Schema.RequiredMode.REQUIRED, description = "Work Item Name")
    @Size(max = 25)
    private String name;

    @Schema(name="description", example = "As a User, I want to monitor new technology", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Description")
    @Size(max = 255)
    private String description;

    @NonNull
    @Size(max = 100)
    @Schema(name = "country", example = "Austria", requiredMode = Schema.RequiredMode.REQUIRED, description = "Country")
    private String country;

    @NonNull
    @Size(max = 100)
    @Schema(name = "city", example = "Linz", requiredMode = Schema.RequiredMode.REQUIRED, description = "city")
    private String city;

    @Schema(name = "address", example = "Rudolf Strasse, 1", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Address")
    @Size(max = 255)
    private String address;

    @NonNull
    @Schema(name = "assignee", example = "John Brown", requiredMode = Schema.RequiredMode.REQUIRED, description = "Assignee")
    @Size(max = 255)
    private String assignee;

    @NonNull
    @Schema(name = "points", example = "5", requiredMode = Schema.RequiredMode.REQUIRED, description = "Complexity")
    private Integer points;

    @NonNull
    @Schema(name = "cost", example = "55.28", requiredMode = Schema.RequiredMode.REQUIRED, description = "Cost")
    private Double cost;

    @Builder.Default
    @Schema(name = "blocked", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Indicates whether the WorkItem is blocked")
    private boolean blocked = false;

    public boolean equals(WorkItemDto o) {
        return Objects.equals(this.getId(), o.getId());
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.id.intValue();
    }
}
