package eu.dec21.wp.tasks.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {
    @Id
    private String taskId;
    @NonNull
    @Schema(name="categoryId", example = "45", requiredMode = Schema.RequiredMode.REQUIRED, description = "ID of the task category (or project)")
    @Min(1)
    private Long categoryId;
    @NonNull
    @Schema(name="title", example = "Implement Auth for the task registry", requiredMode = Schema.RequiredMode.REQUIRED, description = "The title of the task")
    @Size(min = 2, max = 255)
    private String title;
    @Null
    @Schema(name="description", example = "Implement FaceBook and Google SSO", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The description of the task")
    @Size(min = 2, max = 255)
    private String description;
    @NonNull
    @Schema(name="state", example = "DONE", requiredMode = Schema.RequiredMode.REQUIRED, description = "The state of the task")
    private TaskStates state;
    @NonNull
    @Schema(name="cronSchedule", example = "0 0 * * 1", requiredMode = Schema.RequiredMode.REQUIRED, description = "The schedule of the task")
    @Size(min = 4, max = 12)
    private String cronSchedule;
    @NonNull
    @Schema(name="addedPriority", example = "-4", requiredMode = Schema.RequiredMode.REQUIRED, description = "Points added/subtracted to the category's priority")
    @Min(-30)
    @Max(30)
    private Integer addedPriority;
    @Null
    @Schema(name="taskLinks", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The links necessary to accomplish the task")
    private List<TaskLink> taskLinks;
}
