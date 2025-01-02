package eu.dec21.wp.tasks.service;

import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskResponse;
import eu.dec21.wp.tasks.collection.TaskStates;

import java.util.List;

public interface TaskService {
    Task save(Task task);
    List<Task> saveAll(List<Task> tasks);
    Task getTaskById(String id);
    TaskResponse getAllTasksByCategoryId(Long categoryId, int pageNo, int pageSize);
    TaskResponse getAllTasksByCategoryIdAndState(Long categoryId, TaskStates state, int pageNo, int pageSize);

    TaskResponse findAll(int pageNo, int pageSize);
    TaskResponse searchTasks(String searchString, int pageNo, int pageSize);

    void delete(String id);
    long count();
}
