package eu.dec21.wp.tasks.collection;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void setTaskId() {
        Task task = new Task();

        task.setTaskId("");
        assertEquals("", task.getTaskId());

        task.setTaskId("boo");
        assertEquals("boo", task.getTaskId());

//        assertThrowsExactly(NullPointerException.class, () -> task.setTaskId(null));
//        assertEquals("boo", task.getTaskId());
    }

    @Test
    void setCategoryId() {
        Task task = new Task();
        assertThrowsExactly(NullPointerException.class, () -> task.setCategoryId(null));

        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCategoryId(-10L));
        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCategoryId(0L));

        task.setCategoryId(1L);
        assertEquals(1L, task.getCategoryId());

        task.setCategoryId(10L);
        assertEquals(10L, task.getCategoryId());
    }

    @Test
    void setTitle() {
        Task task = new Task();
        assertThrowsExactly(IllegalArgumentException.class, () -> task.setTitle(""));

        task.setTitle("something");
        assertEquals("something", task.getTitle());

        assertThrowsExactly(NullPointerException.class, () -> task.setTitle(null));
        assertEquals("something", task.getTitle());

        final Faker faker = new Faker();
        int length = 127;
        final String title = faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
        task.setTitle(title);
        assertEquals(title, task.getTitle());

        assertThrowsExactly(IllegalArgumentException.class, () -> task.setTitle(title + "1"));
        assertEquals(title, task.getTitle());
    }

    @Test
    void setDescription() {
        Task task = new Task();
        task.setDescription("");
        assertEquals("", task.getDescription());

        task.setDescription("something");
        assertEquals("something", task.getDescription());

        task.setDescription(null);
        assertNull(task.getDescription());

        final Faker faker = new Faker();
        int length = 255;
        final String description = faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
        task.setDescription(description);
        assertEquals(description, task.getDescription());

        assertThrowsExactly(IllegalArgumentException.class, () -> task.setDescription(description + "1"));
        assertEquals(description, task.getDescription());
    }

    @Test
    void setState() {
        Task task = new Task();

        TaskStates state;
        state = TaskStates.CANCEL;

        task.setState(state);
        assertEquals(TaskStates.CANCEL, task.getState());

        assertThrowsExactly(NullPointerException.class, () -> task.setState(null));
        assertEquals(TaskStates.CANCEL, task.getState());
    }

    @Test
    void setCronExpression() {
        Task task = new Task();

        String failoverCronExpression = "0 0 1 1 1970";
        task.setCronExpression("**");
        assertEquals(failoverCronExpression, task.getCronExpression());
//        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCronExpression(""));
//        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCronExpression("**"));
//        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCronExpression("****************"));

        String cronExpression = "0 0 0 ? * MON#1";
        task.setCronExpression(cronExpression);
        assertEquals(cronExpression, task.getCronExpression());

        assertThrowsExactly(NullPointerException.class, () -> task.setCronExpression(null));
        assertEquals(cronExpression, task.getCronExpression());
    }

    @Test
    void setPriority() {
        Task task = new Task();

        assertThrowsExactly(IllegalArgumentException.class, () -> task.setAddedPriority(-1000));
        assertThrowsExactly(IllegalArgumentException.class, () -> task.setAddedPriority(-31));
        assertThrowsExactly(IllegalArgumentException.class, () -> task.setAddedPriority(31));
        assertThrowsExactly(IllegalArgumentException.class, () -> task.setAddedPriority(1000));

        task.setAddedPriority(-30);
        assertEquals(-30, task.getAddedPriority());

        task.setAddedPriority(0);
        assertEquals(0, task.getAddedPriority());

        task.setAddedPriority(30);
        assertEquals(30, task.getAddedPriority());

        assertThrowsExactly(NullPointerException.class, () -> task.setAddedPriority(null));
        assertEquals(30, task.getAddedPriority());
    }

    @Test
    void setTaskLinks() {
        Task task = new Task();

        task.setTaskLinks(null);
        assertNull(task.getTaskLinks());

        TaskLink link1 = new TaskLink("text1", "https://example1.com");
        TaskLink link2 = new TaskLink("text2", "https://example2.com");
        ArrayList<TaskLink> links = new ArrayList<>();
        links.add(link1);
        links.add(link2);

        task.setTaskLinks(links);
        assertEquals(links, task.getTaskLinks());

        TaskLink link3 = new TaskLink("text3", "https://example3.com");
        task.addTaskLink(link3);

        assertEquals(3, task.getTaskLinks().size());
        assertEquals(link1, task.getTaskLinks().get(0));
        assertEquals(link2, task.getTaskLinks().get(1));
        assertEquals(link3, task.getTaskLinks().get(2));

        task.removeTaskLink(link2);
        assertEquals(2, task.getTaskLinks().size());
        assertEquals(link1, task.getTaskLinks().get(0));
        assertEquals(link3, task.getTaskLinks().get(1));

        task.removeTaskLink(link2);
        assertEquals(2, task.getTaskLinks().size());
        assertEquals(link1, task.getTaskLinks().get(0));
        assertEquals(link3, task.getTaskLinks().get(1));

        assertThrows(NullPointerException.class, () -> task.removeTaskLink(null));
        assertThrows(NullPointerException.class, () -> task.addTaskLink(null));

        assertEquals(2, task.getTaskLinks().size());
        task.clearTaskLinks();
        assertNull(task.getTaskLinks());
    }

    @Test
    void setBlockReason() {
        Task task = new Task();
        assertFalse(task.isBlocked());
        task.setBlockReason("");
        assertEquals("", task.getBlockReason());
        assertFalse(task.isBlocked());

        task.setBlockReason("something");
        assertEquals("something", task.getBlockReason());
        assertTrue(task.getIsBlocked());

        task.setBlockReason(null);
        assertNull(task.getBlockReason());
        assertTrue(task.isBlocked());

        final Faker faker = new Faker();
        int length = 255;
        final String blockReason = faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
        task.setBlockReason(blockReason);
        assertEquals(blockReason, task.getBlockReason());

        assertThrowsExactly(IllegalArgumentException.class, () -> task.setBlockReason(blockReason + "1"));
        assertEquals(blockReason, task.getBlockReason());
    }

    @Test
    void setBlockingIssues() {
        Task task = new Task();

        task.setBlockingIssues(null);
        assertNull(task.getBlockingIssues());
        assertFalse(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());

        TaskLink link1 = new TaskLink("text1", "https://example1.com");
        TaskLink link2 = new TaskLink("text2", "https://example2.com");
        ArrayList<TaskLink> links = new ArrayList<>();
        links.add(link1);
        links.add(link2);

        task.setBlockingIssues(links);
        assertEquals(links, task.getBlockingIssues());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());

        TaskLink link3 = new TaskLink("text3", "https://example3.com");
        task.addBlockLink(link3);

        assertEquals(3, task.getBlockingIssues().size());
        assertEquals(link1, task.getBlockingIssues().get(0));
        assertEquals(link2, task.getBlockingIssues().get(1));
        assertEquals(link3, task.getBlockingIssues().get(2));

        task.removeBlockLink(link2);
        assertEquals(2, task.getBlockingIssues().size());
        assertEquals(link1, task.getBlockingIssues().get(0));
        assertEquals(link3, task.getBlockingIssues().get(1));

        task.removeBlockLink(link2);
        assertEquals(2, task.getBlockingIssues().size());
        assertEquals(link1, task.getBlockingIssues().get(0));
        assertEquals(link3, task.getBlockingIssues().get(1));
        assertTrue(task.getIsBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());

        assertThrows(NullPointerException.class, () -> task.removeBlockLink(null));
        assertThrows(NullPointerException.class, () -> task.addBlockLink(null));

        assertEquals(2, task.getBlockingIssues().size());
        task.clearBlockLinks();
        assertNull(task.getBlockingIssues());
        assertTrue(task.getIsBlocked());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
    }

    @Test
    void setIsBlocked() {
        Task task = new Task();
        assertFalse(task.isBlocked());
        assertFalse(task.getIsBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        task.setIsBlocked(true);
        assertTrue(task.isBlocked());
        assertTrue(task.getIsBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        task.unblock();
        assertFalse(task.isBlocked());
        assertFalse(task.getIsBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        task.block();
        assertTrue(task.isBlocked());
        assertTrue(task.getIsBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
    }

    @Test
    void setIsActive() {
        Task task = new Task();
        assertTrue(task.isActive());
        assertTrue(task.getIsActive());

        task.deactivate();
        assertFalse(task.isActive());
        assertFalse(task.getIsActive());

        task.activate();
        assertTrue(task.isActive());
        assertTrue(task.getIsActive());

        task.setIsActive(false);
        assertFalse(task.isActive());
        assertFalse(task.getIsActive());

        task.setIsActive(true);
        assertTrue(task.isActive());
        assertTrue(task.getIsActive());
    }

    @Test
    void taskProgress() {
        Task task = new Task();
        task.block();

        assertEquals(TaskStates.PREP, task.getState());
        assertTrue(task.getState().isNew());
        assertFalse(task.getState().isStarted());
        assertFalse(task.getState().isDone());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertFalse(task.isComplete());
        assertTrue(task.isToDo());

        task.nextState();
        assertEquals(TaskStates.READY, task.getState());
        assertTrue(task.getState().isNew());
        assertFalse(task.getState().isStarted());
        assertFalse(task.getState().isDone());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertFalse(task.isComplete());
        assertTrue(task.isToDo());

        task.nextState();
        assertEquals(TaskStates.IMPL, task.getState());
        assertFalse(task.getState().isNew());
        assertTrue(task.getState().isStarted());
        assertFalse(task.getState().isDone());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertFalse(task.isComplete());
        assertTrue(task.isToDo());

        task.nextState();
        assertEquals(TaskStates.DONE, task.getState());
        assertFalse(task.getState().isNew());
        assertTrue(task.getState().isStarted());
        assertTrue(task.getState().isDone());
        assertFalse(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertTrue(task.isComplete());
        assertFalse(task.isToDo());

        task.nextState();
        assertEquals(TaskStates.DONE, task.getState());
        assertFalse(task.getState().isNew());
        assertTrue(task.getState().isStarted());
        assertTrue(task.getState().isDone());
        assertFalse(task.isBlocked());
        task.block(); // should not block completed task
        assertFalse(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertTrue(task.isComplete());
        assertFalse(task.isToDo());

        task.cancel();
        assertEquals(TaskStates.CANCEL, task.getState());
        assertFalse(task.getState().isNew());
        assertTrue(task.getState().isStarted());
        assertTrue(task.getState().isDone());
        assertFalse(task.isBlocked());
        task.block(); // should not block completed task
        assertFalse(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertTrue(task.isComplete());
        assertFalse(task.isToDo());

        task.nextState();
        assertEquals(TaskStates.CANCEL, task.getState());
        assertFalse(task.getState().isNew());
        assertTrue(task.getState().isStarted());
        assertTrue(task.getState().isDone());
        assertFalse(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertTrue(task.isComplete());
        assertFalse(task.isToDo());

        task.complete();
        assertEquals(TaskStates.DONE, task.getState());
        assertFalse(task.getState().isNew());
        assertTrue(task.getState().isStarted());
        assertTrue(task.getState().isDone());
        assertFalse(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertTrue(task.isComplete());
        assertFalse(task.isToDo());

        task.reopen();
        assertEquals(TaskStates.READY, task.getState());
        assertTrue(task.getState().isNew());
        assertFalse(task.getState().isStarted());
        assertFalse(task.getState().isDone());
        assertFalse(task.isBlocked());
        task.block();
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertFalse(task.isComplete());
        assertTrue(task.isToDo());

        task.prevState();
        assertEquals(TaskStates.PREP, task.getState());
        assertTrue(task.getState().isNew());
        assertFalse(task.getState().isStarted());
        assertFalse(task.getState().isDone());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertFalse(task.isComplete());
        assertTrue(task.isToDo());

        task.prevState();
        assertEquals(TaskStates.PREP, task.getState());
        assertTrue(task.getState().isNew());
        assertFalse(task.getState().isStarted());
        assertFalse(task.getState().isDone());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertFalse(task.isComplete());
        assertTrue(task.isToDo());

        task.start();
        assertEquals(TaskStates.IMPL, task.getState());
        assertFalse(task.getState().isNew());
        assertTrue(task.getState().isStarted());
        assertFalse(task.getState().isDone());
        assertTrue(task.isBlocked());
        assertTrue(task.isActive());
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertFalse(task.isComplete());
        assertTrue(task.isToDo());
    }

    @Test
    void archiving() {
        Task task = new Task();
        assertFalse(task.isArchived());
        assertTrue(task.isActual());
        assertTrue(task.isActive());

        task.archive();
        assertTrue(task.isArchived());
        assertFalse(task.isActual());
        assertFalse(task.isActive()); // archiving deactivates the task

        task.setArchived(Boolean.FALSE);
        assertFalse(task.isArchived());
        assertTrue(task.isActual());

        task.setArchived(Boolean.TRUE);
        assertTrue(task.isArchived());
        assertFalse(task.isActual());
    }

    @Test
    void taskDateTime() {
        Task task = new Task();
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), task.getTaskDateTime().plus(500, ChronoUnit.MICROS).truncatedTo(ChronoUnit.SECONDS));
    }
}
