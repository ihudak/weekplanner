package eu.dec21.wp.tasks.collection;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskDirector {
    private final TaskBuilder taskBuilder = new TaskBuilder();
    private final Faker faker = new Faker();

    private String getRandStr(int length) {
        return faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
    }
    private int getRantTitleLength() {
        return faker.random().nextInt(5, 25);
    }

    private TaskBuilder prepareTask() {
        return taskBuilder.reset()
                .withId(getRandStr(10))
                .withCategoryId(faker.random().nextInt(1, 10))
                .withTitle(getRandStr(getRantTitleLength()))
                .withDescription(getRandStr(getRantTitleLength()))
                .withPriority(faker.random().nextInt(-30, 30))
                .withTaskState(TaskStates.READY)
                .withCronExpression(faker.regexify("[0-9]{3}"))
                .withTaskLink("Jira", "https://jira.com");
    }

    public Task constructRandomTask() {
        return prepareTask()
                .build();
    }

    public Task constructRandomTaskWithId(String taskId) {
        return prepareTask()
                .withId(taskId)
                .build();
    }

    public Task constructTaskForCategoryState(long categoryId, TaskStates taskState) {
        return prepareTask()
                .withCategoryId(categoryId)
                .withTaskState(taskState)
                .build();
    }

    public List<Task> constructRandomTasks(int numTasks) {
        if (numTasks < 0) {
            numTasks = 0;
        }
        List<Task> tasks = new ArrayList<>(numTasks);
        for (int i = 0; i < numTasks; i++) {
            tasks.add(constructRandomTaskWithId(String.valueOf(i)));
        }
        return tasks;
    }

    public List<Task> constructTasksForCategoryState(long categoryId, TaskStates taskState, int numTasks) {
        return this.constructRandomTasks(numTasks).stream().peek(task -> task.setCategoryId(categoryId)).peek(task -> task.setState(taskState)).collect(Collectors.toList());
    }
}
