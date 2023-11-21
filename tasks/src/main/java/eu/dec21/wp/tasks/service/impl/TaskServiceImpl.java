package eu.dec21.wp.tasks.service.impl;

import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskResponse;
import eu.dec21.wp.tasks.collection.TaskStates;
import eu.dec21.wp.tasks.repository.TaskRepository;
import eu.dec21.wp.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Task task) {
        return taskRepository.save(task).getTaskId();
    }

    @Override
    public TaskResponse getAllTasksByCategoryId(Long categoryId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return getTastResponse(taskRepository.getAllByCategoryId(categoryId, pageable));
    }

    @Override
    public List<Task> getAllTasksByCategoryIdAndState(Long categoryId, TaskStates state) {
        return taskRepository.getAllByCategoryIdAndState(categoryId, state);
    }

    @Override
    public TaskResponse findAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return getTastResponse(taskRepository.findAll(pageable));
    }

    @Override
    public TaskResponse searchTasks(String searchString, int pageNo, int pageSize) {
        if (searchString == null || searchString.isEmpty()) {
            return null;
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("title").regex(searchString, "i"));
        criteria.add(Criteria.where("description").regex(searchString, "i"));
        criteria.add(Criteria.where("state").regex(searchString, "i"));
        criteria.add(Criteria.where("taskLinks.name").regex(searchString, "i"));
        criteria.add(Criteria.where("taskLinks.url").regex(searchString, "i"));

        query.addCriteria(new Criteria().orOperator(criteria));

        return getTastResponse(
                PageableExecutionUtils.getPage(
                        mongoTemplate.find(query, Task.class),
                        pageable, () -> mongoTemplate.count(query.skip(0).limit(0), Task.class)
                )
        );
    }

    @Override
    public Task getTaskById(String id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
    }

    @Override
    public void delete(String id) {
        taskRepository.deleteById(id);
    }



    private TaskResponse getTastResponse(Page<Task> tasks) {
        List<Task> taskList = tasks.getContent();

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setContent(taskList);
        taskResponse.setPageNo(tasks.getNumber());
        taskResponse.setPageSize(tasks.getSize());
        taskResponse.setTotalElements(tasks.getTotalElements());
        taskResponse.setTotalPages(taskResponse.getTotalPages());
        taskResponse.setLast(tasks.isLast());

        return taskResponse;
    }
}
