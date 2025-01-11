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
        return mongoTemplate.find(this.dateBasedTasksQuery(this.prepareWeekCriteria(weekNo, year)), Task.class);
    }

    @Override
    public List<Task> activeTasksOfWeek(int weekNo, int year) {
        List<Criteria> criteria = this.prepareWeekCriteria(weekNo, year);
        criteria.add(Criteria.where("state").in(TaskStates.activeStates()));
        return mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
    }

    @Override
    public List<Task> completeTasksOfWeek(int weekNo, int year) {
        List<Criteria> criteria = this.prepareWeekCriteria(weekNo, year);
        criteria.add(Criteria.where("state").in(TaskStates.inactiveStates()));
        return mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
    }

    @Override
    public List<Task> allTasksOfDay(int plusDaysFromToday) {
        return mongoTemplate.find(this.dateBasedTasksQuery(this.prepareDayCriteria(plusDaysFromToday)), Task.class);
    }

    @Override
    public List<Task> activeTasksOfDay(int plusDaysFromToday) {
        List<Criteria> criteria = this.prepareDayCriteria(plusDaysFromToday);
        criteria.add(Criteria.where("state").in(TaskStates.activeStates()));
        return mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
    }

    @Override
    public List<Task> completeTasksOfDay(int plusDaysFromToday) {
        List<Criteria> criteria = this.prepareDayCriteria(plusDaysFromToday);
        criteria.add(Criteria.where("state").in(TaskStates.inactiveStates()));
        return mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
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

    @Override
    public Task archiveTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (!task.isComplete()) {
            throw new BadRequestException("Task must be completed before archiving");
        }
        task.archive();
        return taskRepository.save(task);
    }

    @Override
    public Task completeTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot complete a task that is archived");
        } else if (task.isComplete()) {
            throw new BadRequestException("Cannot complete a task that is already completed");
        }
        task.complete();
        return taskRepository.save(task);
    }

    @Override
    public Task cancelTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot cancel a task that is archived");
        }
        task.cancel();
        return taskRepository.save(task);
    }

    @Override
    public Task reopenTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot reopen a task that is archived");
        } else if (!task.isComplete()) {
            throw new BadRequestException("Cannot reopen a task that is not completed");
        }
        task.reopen();
        return taskRepository.save(task);
    }

    @Override
    public Task blockTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot block a task that is archived");
        } else if (task.isComplete()) {
            throw new BadRequestException("Cannot block a completed task");
        }
        task.block();
        return taskRepository.save(task);
    }

    @Override
    public Task unblockTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot unblock a task that is archived");
        }
        task.unblock();
        return taskRepository.save(task);
    }

    @Override
    public Task activateTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot activate a task that is archived");
        }
        task.activate();
        return taskRepository.save(task);
    }

    @Override
    public Task deactivateTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        task.deactivate();
        return taskRepository.save(task);
    }

    @Override
    public Task stateForwardTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot forward a task that is archived");
        }
        if (task.isComplete()) {
            throw new BadRequestException("Cannot forward a completed task");
        }
        task.nextState();
        return taskRepository.save(task);
    }

    @Override
    public Task stateBackwardTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot backward a task that is archived");
        }
        if (task.isComplete()) {
            throw new BadRequestException("Cannot backward a completed task");
        }
        if (TaskStates.PREP == task.getState()) {
            throw new BadRequestException("Cannot backward a task in the " + task.getState() + " state");
        }
        task.prevState();
        return taskRepository.save(task);
    }

    @Override
    public Task startTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + id));
        if (task.isArchived()) {
            throw new BadRequestException("Cannot start a task that is archived");
        }
        if (task.isComplete()) {
            throw new BadRequestException("Cannot start a completed task");
        }
        if (!task.getState().isNew()) {
            throw new BadRequestException("Cannot start a task in state " + task.getState());
        }
        task.start();
        return taskRepository.save(task);
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

    private LocalDateTime getWeekEnd(Integer weekNo, Integer year) {
        return this.getWeekEnd(weekNo, year, Locale.getDefault());
    }

    private LocalDateTime getWeekEnd(Integer weekNo, Integer year, Locale locale) {
        // Get the first day of the specified week
        return LocalDate.ofYearDay(year, 1)
                .with(WeekFields.of(locale).weekOfYear(), weekNo)
                .with(WeekFields.of(locale).dayOfWeek(), 1).plusWeeks(1).atStartOfDay();
    }

    private List<Criteria> prepareWeekCriteria(int weekNo, int year) {
        LocalDateTime endDate = this.getWeekEnd(weekNo, year).plusWeeks(1);
        return getDateCriteria(endDate);
    }

    private List<Criteria> prepareDayCriteria(int plusDays) {
        LocalDateTime endDate = LocalDate.now().plusDays(plusDays + 1).atStartOfDay();
        return getDateCriteria(endDate);
    }

    private List<Criteria> getDateCriteria(LocalDateTime endDate) {
        List<Criteria> criteria = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.of(1970, 1, 1, 0, 0);

        criteria.add(Criteria.where("archived").is(Boolean.FALSE));
        criteria.add(Criteria.where("taskDateTime").gte(startDate)); // make sure index is used
        criteria.add(Criteria.where("taskDateTime").lt(endDate));
        return criteria;
    }

    private Query dateBasedTasksQuery(List<Criteria> criteria) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(criteria));
        query.with(Sort.by(Sort.Direction.DESC, "taskDateTime"));
        return query;
    }
}
