package eu.dec21.wp.tasks.collection;

import java.util.ArrayList;

public class TaskBuilder {
    private Task task;

    public TaskBuilder() {
        this.task = new Task();
    }

    public TaskBuilder reset() {
        this.task = new Task();
        return this;
    }

    public TaskBuilder withId(String id) {
        this.task.setTaskId(id);
        return this;
    }

    public TaskBuilder withTitle(String title) {
        this.task.setTitle(title);
        return this;
    }

    public TaskBuilder withDescription(String description) {
        this.task.setDescription(description);
        return this;
    }

    public TaskBuilder withPriority(int priority) {
        this.task.setAddedPriority(priority);
        return this;
    }

    public TaskBuilder withCategoryId(long categoryId) {
        this.task.setCategoryId(categoryId);
        return this;
    }

    public TaskBuilder withCronExpression(String cronExpression) {
        this.task.setCronExpression(cronExpression);
        return this;
    }

    public TaskBuilder withTaskState(TaskStates taskState) {
        this.task.setState(taskState);
        return this;
    }

    public TaskBuilder withTaskLink(String name, String url) {
        TaskLink taskLink = new TaskLink(name, url);
        ArrayList<TaskLink> taskLinks = new ArrayList<>();
        taskLinks.add(taskLink);
        this.task.setTaskLinks(taskLinks);
        return this;
    }

    public Task build() {
        if (this.task == null) {
            this.reset();
        }
        return this.task;
    }
}
