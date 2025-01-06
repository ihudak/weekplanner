package eu.dec21.wp.tasks.service.impl;

import eu.dec21.wp.exceptions.BadRequestException;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TaskServiceImpl(MongoTemplate mongoTemplate, TaskRepository taskRepository) {
        this.mongoTemplate = mongoTemplate;
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> saveAll(List<Task> tasks) {
        return taskRepository.saveAll(tasks);
    }

    @Override
    public TaskResponse getAllTasksByCategoryId(Long categoryId, int pageNo, int pageSize) {
        return getTaskResponse(taskRepository.getAllByCategoryIdAndArchived(categoryId, Boolean.TRUE, PageRequest.of(pageNo, pageSize)));
    }

    @Override
    public TaskResponse getAllTasksByCategoryIdAndState(Long categoryId, TaskStates state, int pageNo, int pageSize) {
        return getTaskResponse(taskRepository.getAllByCategoryIdAndStateAndArchived(categoryId, state, Boolean.TRUE, PageRequest.of(pageNo, pageSize)));
    }

    @Override
    public TaskResponse getActualTasks(int pageNo, int pageSize) {
        return getTaskResponse(taskRepository.getAllByArchived(Boolean.FALSE, PageRequest.of(pageNo, pageSize)));
    }

    @Override
    public TaskResponse getTasksByStateActual(TaskStates state, int pageNo, int pageSize) {
        return getTaskResponse(taskRepository.getAllByStateAndArchived(state, Boolean.FALSE, PageRequest.of(pageNo, pageSize)));
    }

    @Override
    public TaskResponse findAll(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return getTaskResponse(taskRepository.findAll(pageable));
    }

    @Override
    public TaskResponse searchTasks(String searchString, boolean inclArchived, int pageNo, int pageSize) {
        if (searchString == null || searchString.length() < 3) {
            throw new BadRequestException("Search string must be at least 3 characters");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("title").regex(searchString, "i"));
        criteria.add(Criteria.where("description").regex(searchString, "i"));
        criteria.add(Criteria.where("state").regex(searchString, "i"));
        criteria.add(Criteria.where("blockReason").regex(searchString, "i"));

        criteria.add(Criteria.where("taskLinks").elemMatch(Criteria.where("name").regex(searchString, "i")));
        criteria.add(Criteria.where("taskLinks").elemMatch(Criteria.where("url").regex(searchString, "i")));
        criteria.add(Criteria.where("blockingIssues").elemMatch(Criteria.where("name").regex(searchString, "i")));
        criteria.add(Criteria.where("blockingIssues").elemMatch(Criteria.where("url").regex(searchString, "i")));

        if (!inclArchived) {
            Criteria finalCriteria = new Criteria().andOperator(Criteria.where("archived").is(Boolean.FALSE), new Criteria().orOperator(criteria));
            query.addCriteria(new Criteria().andOperator(finalCriteria));
        } else {
            query.addCriteria(new Criteria().orOperator(criteria));
        }

        query.with(Sort.by(Sort.Direction.DESC, "taskDateTime"));
        query.skip((long) pageNo * pageSize).limit(pageSize);

        return getTaskResponse(
                PageableExecutionUtils.getPage(
                        mongoTemplate.find(query, Task.class),
                        pageable, () -> mongoTemplate.count(query, Task.class)
                )
        );
    }

    @Override
    public List<Task> allTasksOfWeek(int weekNo, int year) {
        return mongoTemplate.find(this.weekTasksQuery(this.prepareWeekCriteria(weekNo, year)), Task.class);
    }

    @Override
    public List<Task> activeTasksOfWeek(int weekNo, int year) {
        List<Criteria> criteria = this.prepareWeekCriteria(weekNo, year);
        criteria.add(Criteria.where("state").in(TaskStates.activeStates()));

        return mongoTemplate.find(this.weekTasksQuery(criteria), Task.class);
    }

    @Override
    public List<Task> completeTasksOfWeek(int weekNo, int year) {
        List<Criteria> criteria = this.prepareWeekCriteria(weekNo, year);
        criteria.add(Criteria.where("state").in(TaskStates.inactiveStates()));

        return mongoTemplate.find(this.weekTasksQuery(criteria), Task.class);
    }

    @Override
    public Task getTaskById(String id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
    }

    @Override
    public void delete(String id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task does not exist with the given ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public long count() {
        return taskRepository.count();
    }

    private TaskResponse getTaskResponse(Page<Task> tasks) {
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

    private LocalDateTime getWeekStart(Integer weekNo, Integer year) {
        return this.getWeekStart(weekNo, year, Locale.getDefault());
    }

    private LocalDateTime getWeekStart(Integer weekNo, Integer year, Locale locale) {
        // Get the first day of the specified week
        return LocalDate.ofYearDay(year, 1)
                .with(WeekFields.of(locale).weekOfYear(), weekNo)
                .with(WeekFields.of(locale).dayOfWeek(), 1).atStartOfDay();
    }

    private List<Criteria> prepareWeekCriteria(int weekNo, int year) {
        LocalDateTime startDate = this.getWeekStart(weekNo, year);
        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("archived").is(Boolean.FALSE));
        criteria.add(Criteria.where("taskDateTime").gte(startDate));
        criteria.add(Criteria.where("taskDateTime").lt(startDate.plusWeeks(1)));
        return criteria;
    }

    private Query weekTasksQuery(List<Criteria> criteria) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(criteria));
        query.with(Sort.by(Sort.Direction.DESC, "taskDateTime"));
        return query;
    }
}
