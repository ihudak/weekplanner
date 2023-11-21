package eu.dec21.wp.tasks.collection;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskLink {
    @NonNull
    @Schema(name="name", example = "Jira Link", requiredMode = Schema.RequiredMode.REQUIRED, description = "User-friendly name of the link")
    @Size(min = 2, max = 25)
    private String name;
    @NonNull
    @Schema(name="url", example = "https://dec21.jira.atlassian.com/TASK-2341", requiredMode = Schema.RequiredMode.REQUIRED, description = "URL to the resource")
    @Size(min = 2, max = 255)
    private String url;
}
