package eu.dec21.wp.tasks.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {
    @Transient
    @JsonIgnore
    final int minTitleLength = 2;
    @Transient
    @JsonIgnore
    final int maxTitleLength = 127;
    @Transient
    @JsonIgnore
    final int maxDescriptionLength = 255;
    @Transient
    @JsonIgnore
    final int minAddedPriority = -30;
    @Transient
    @JsonIgnore
    final int maxAddedPriority = 30;

    @Id
    @NonNull
    private String taskId;
    @NonNull
    @Schema(name="categoryId", example = "45", requiredMode = Schema.RequiredMode.REQUIRED, description = "ID of the task category (or project)")
    @Min(1)
    @Indexed
    private Long categoryId;
    @NonNull
    @Schema(name="title", example = "Implement Auth for the task registry", requiredMode = Schema.RequiredMode.REQUIRED, description = "The title of the task")
    @Size(min = minTitleLength, max = maxTitleLength)
    @Indexed
    private String title;
    @Null
    @Schema(name="description", example = "Implement FaceBook and Google SSO", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The description of the task")
    @Size(max = maxDescriptionLength)
    @Indexed
    private String description;
    @NonNull
    @Schema(name="state", example = "READY", requiredMode = Schema.RequiredMode.REQUIRED, description = "The state of the task")
    @Indexed
    private TaskStates state = TaskStates.PREP;
    @NonNull
    @Schema(name="cronExpression", example = "0 0 0 ? * MON#1", requiredMode = Schema.RequiredMode.REQUIRED, description = "The schedule of the task")
    @Size(min = 4, max = 12)
    private String cronExpression;
    @NonNull
    @Schema(name="addedPriority", example = "-4", requiredMode = Schema.RequiredMode.REQUIRED, description = "Points added/subtracted to the category's priority")
    @Min(minAddedPriority)
    @Max(maxAddedPriority)
    private Integer addedPriority;
    @Null
    @Schema(name="taskLinks", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The links necessary to accomplish the task")
    private List<TaskLink> taskLinks;
    @NonNull
    @Schema(name="isBlocked", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Indicates whether the task is blocked")
    @Indexed
    private Boolean isBlocked = Boolean.FALSE;
    @Null
    @Schema(name="blockReason", example = "Blocked by team Mars", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Verbal reason why the task is blocked")
    @Size(max = maxDescriptionLength)
    private String blockReason;
    @Null
    @Schema(name="blockingIssues", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "The links to the blocking issues")
    private List<TaskLink> blockingIssues;
    @NonNull
    @Schema(name="isActive", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Indicates whether the task is active to create recurring copies")
    @Indexed
    private Boolean isActive = Boolean.TRUE;
    @NonNull
    @Schema(name="taskDateTime", requiredMode = Schema.RequiredMode.REQUIRED, description = "Timestamp when the task is scheduled for implementation")
    @Indexed
    private LocalDateTime taskDateTime = LocalDateTime.now();
    @NonNull
    @Schema(name="archived", example = "false", requiredMode = Schema.RequiredMode.REQUIRED, description = "Indicates whether the task is archived")
    @Indexed
    private Boolean archived = Boolean.FALSE;

    public void setCategoryId(@NonNull Long categoryId) {
        if (categoryId < 1) {
            throw new IllegalArgumentException("Category id cannot be less than 1");
        }
        this.categoryId = categoryId;
    }

    public void setTitle(@NonNull String title) {
        if (title.length() < minTitleLength || title.length() > maxTitleLength) {
            throw new IllegalArgumentException("Title length must be between " + minTitleLength + " and " + maxTitleLength);
        }
        this.title = title;
    }

    public void setDescription(@Null String description) {
        if (null != description && description.length() > maxDescriptionLength) {
            throw new IllegalArgumentException("Description length must be less than " + maxDescriptionLength);
        }
        this.description = description;
    }

    public void addTaskLink(@NonNull TaskLink taskLink) {
        if (this.taskLinks == null) {
            this.taskLinks = new ArrayList<>();
        }
        this.taskLinks.add(taskLink);
    }

    public void removeTaskLink(@NonNull TaskLink taskLink) {
        if (this.taskLinks != null) {
            this.taskLinks.remove(taskLink);
        }
    }

    public void clearTaskLinks() {
        this.taskLinks = null;
    }

    public void setBlockReason(@Null String blockReason) {
        if (null != blockReason && blockReason.length() > maxDescriptionLength) {
            throw new IllegalArgumentException("BlockingReason length must be less than " + maxDescriptionLength);
        }
        this.blockReason = blockReason;
        if (blockReason != null && !blockReason.isEmpty()) {
            this.isBlocked = Boolean.TRUE;
        }
    }

    public void setBlockingIssues(@Null List<TaskLink> blockingIssues) {
        this.blockingIssues = blockingIssues;
        if (blockingIssues != null && !blockingIssues.isEmpty()) {
            this.block();
        }
    }

    public void addBlockLink(@NonNull TaskLink blockLink) {
        if (this.blockingIssues == null) {
            this.blockingIssues = new ArrayList<>();
        }
        this.blockingIssues.add(blockLink);
        this.block();
    }

    public void removeBlockLink(@NonNull TaskLink blockLink) {
        if (this.blockingIssues != null) {
            this.blockingIssues.remove(blockLink);
        }
    }

    public void clearBlockLinks() {
        this.blockingIssues = null;
    }

    public void setAddedPriority(@NonNull Integer addedPriority) {
        if (addedPriority < this.minAddedPriority || addedPriority > this.maxAddedPriority) {
            throw new IllegalArgumentException("Added priority must be between " + this.minAddedPriority + " and " + this.maxAddedPriority);
        }
        this.addedPriority = addedPriority;
    }

    public void setCronExpression(@NonNull String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("Cron expression is invalid");
        }
        this.cronExpression = cronExpression;
    }

    public void nextState() {
        if (!this.state.isDone()) {
            this.state = this.state.next();
        }
        if (this.state.isDone()) {
            this.isBlocked = Boolean.FALSE;
        }
    }

    public void prevState() {
        if (this.state != TaskStates.PREP) {
            this.state = this.state.previous();
        }
    }

    public void cancel() {
        this.isBlocked = Boolean.FALSE;
        this.state = TaskStates.CANCEL;
    }

    public void complete() {
        this.isBlocked = Boolean.FALSE;
        this.state = TaskStates.DONE;
    }

    public void reopen() {
        this.state = TaskStates.READY;
    }

    public void start() {
        this.state = TaskStates.IMPL;
    }

    public Boolean isBlocked() {
        return this.isBlocked;
    }

    public void setIsBlocked(@NonNull Boolean isBlocked) {
        if (isBlocked) {
            this.block();
        } else {
            this.unblock();
        }
    }

    public void block() {  // should not block completed or cancelled
        this.isBlocked = !this.state.isDone();
    }

    public void unblock() {
        this.isBlocked = Boolean.FALSE;
    }

    public void activate() {
        this.isActive = Boolean.TRUE;
    }

    public void deactivate() {
        this.isActive = Boolean.FALSE;
    }

    public Boolean isActive() {
        return this.isActive;
    }

    public Boolean isArchived() {
        return this.archived;
    }

    public Boolean isActual() {
        return !this.archived;
    }

    public void archive() {
        this.deactivate();
        this.archived = Boolean.TRUE;
    }

    public Boolean isComplete() {
        return this.state.isDone();
    }

    public Boolean isToDo() {
        return !this.state.isDone() && !this.archived;
    }
}
