package eu.dec21.wp.tasks.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Transient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class TaskLink {
    @Transient
    @JsonIgnore
    final int minNameLength = 2;
    @Transient
    @JsonIgnore
    final int maxNameLength = 25;
    @Transient
    @JsonIgnore
    final int minUrlLength = 12;
    @Transient
    @JsonIgnore
    final int maxUrlLength = 255;

    @NonNull
    @Schema(name="name", example = "Jira Link", requiredMode = Schema.RequiredMode.REQUIRED, description = "User-friendly name of the link")
    @Size(min = minNameLength, max = maxNameLength)
    private String name;
    @NonNull
    @Schema(name="url", example = "https://dec21.jira.atlassian.com/TASK-2341", requiredMode = Schema.RequiredMode.REQUIRED, description = "URL to the resource")
    @Size(min = minUrlLength, max = maxUrlLength)
    private String url;

    public TaskLink(@NonNull String name, @NonNull String url) {
        this.setName(name);
        this.setUrl(url);
    }

    public void setName(@NonNull String name) {
        if (name.length() < minNameLength || name.length() > maxNameLength) {
            throw new IllegalArgumentException("Name length must be between " + minNameLength + " and " + maxNameLength);
        }
        this.name = name;
    }

    public void setUrl(@NonNull String url) {
        if (url.length() < minUrlLength || url.length() > maxUrlLength) {
            throw new IllegalArgumentException("Url length must be between " + minUrlLength + " and " + maxUrlLength);
        }
        try {
            URL ignore = new URI(url).toURL(); // checking if the url is correct. If not, exception will be raised
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        this.url = url;
    }
}
