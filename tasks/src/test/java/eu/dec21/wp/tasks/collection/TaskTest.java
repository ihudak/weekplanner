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

        assertThrowsExactly(NullPointerException.class, () -> task.setTaskId(null));
        assertEquals("boo", task.getTaskId());
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

        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCronExpression(""));
        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCronExpression("**"));
        assertThrowsExactly(IllegalArgumentException.class, () -> task.setCronExpression("****************"));

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
        assertEquals(Boolean.FALSE, task.isBlocked());
        task.setBlockReason("");
        assertEquals("", task.getBlockReason());
        assertEquals(Boolean.FALSE, task.isBlocked());

        task.setBlockReason("something");
        assertEquals("something", task.getBlockReason());
        assertEquals(Boolean.TRUE, task.getIsBlocked());

        task.setBlockReason(null);
        assertNull(task.getBlockReason());
        assertEquals(Boolean.TRUE, task.isBlocked());

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
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        TaskLink link1 = new TaskLink("text1", "https://example1.com");
        TaskLink link2 = new TaskLink("text2", "https://example2.com");
        ArrayList<TaskLink> links = new ArrayList<>();
        links.add(link1);
        links.add(link2);

        task.setBlockingIssues(links);
        assertEquals(links, task.getBlockingIssues());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

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
        assertEquals(Boolean.TRUE, task.getIsBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        assertThrows(NullPointerException.class, () -> task.removeBlockLink(null));
        assertThrows(NullPointerException.class, () -> task.addBlockLink(null));

        assertEquals(2, task.getBlockingIssues().size());
        task.clearBlockLinks();
        assertNull(task.getBlockingIssues());
        assertEquals(Boolean.TRUE, task.getIsBlocked());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());
    }

    @Test
    void setIsBlocked() {
        Task task = new Task();
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.FALSE, task.getIsBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());
        task.setIsBlocked(true);
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.getIsBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());
        task.unblock();
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.FALSE, task.getIsBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());
        task.block();
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.getIsBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());
    }

    @Test
    void setIsActive() {
        Task task = new Task();
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.TRUE, task.getIsActive());

        task.deactivate();
        assertEquals(Boolean.FALSE, task.isActive());
        assertEquals(Boolean.FALSE, task.getIsActive());

        task.activate();
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.TRUE, task.getIsActive());

        task.setIsActive(false);
        assertEquals(Boolean.FALSE, task.isActive());
        assertEquals(Boolean.FALSE, task.getIsActive());

        task.setIsActive(true);
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.TRUE, task.getIsActive());
    }

    @Test
    void taskProgress() {
        Task task = new Task();
        task.block();

        assertEquals(TaskStates.PREP, task.getState());
        assertEquals(Boolean.TRUE, task.getState().isNew());
        assertEquals(Boolean.FALSE, task.getState().isStarted());
        assertEquals(Boolean.FALSE, task.getState().isDone());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.nextState();
        assertEquals(TaskStates.READY, task.getState());
        assertEquals(Boolean.TRUE, task.getState().isNew());
        assertEquals(Boolean.FALSE, task.getState().isStarted());
        assertEquals(Boolean.FALSE, task.getState().isDone());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.nextState();
        assertEquals(TaskStates.IMPL, task.getState());
        assertEquals(Boolean.FALSE, task.getState().isNew());
        assertEquals(Boolean.TRUE, task.getState().isStarted());
        assertEquals(Boolean.FALSE, task.getState().isDone());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.nextState();
        assertEquals(TaskStates.DONE, task.getState());
        assertEquals(Boolean.FALSE, task.getState().isNew());
        assertEquals(Boolean.TRUE, task.getState().isStarted());
        assertEquals(Boolean.TRUE, task.getState().isDone());
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.nextState();
        assertEquals(TaskStates.DONE, task.getState());
        assertEquals(Boolean.FALSE, task.getState().isNew());
        assertEquals(Boolean.TRUE, task.getState().isStarted());
        assertEquals(Boolean.TRUE, task.getState().isDone());
        assertEquals(Boolean.FALSE, task.isBlocked());
        task.block(); // should not block completed task
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.cancel();
        assertEquals(TaskStates.CANCEL, task.getState());
        assertEquals(Boolean.FALSE, task.getState().isNew());
        assertEquals(Boolean.TRUE, task.getState().isStarted());
        assertEquals(Boolean.TRUE, task.getState().isDone());
        assertEquals(Boolean.FALSE, task.isBlocked());
        task.block(); // should not block completed task
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.nextState();
        assertEquals(TaskStates.CANCEL, task.getState());
        assertEquals(Boolean.FALSE, task.getState().isNew());
        assertEquals(Boolean.TRUE, task.getState().isStarted());
        assertEquals(Boolean.TRUE, task.getState().isDone());
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.complete();
        assertEquals(TaskStates.DONE, task.getState());
        assertEquals(Boolean.FALSE, task.getState().isNew());
        assertEquals(Boolean.TRUE, task.getState().isStarted());
        assertEquals(Boolean.TRUE, task.getState().isDone());
        assertEquals(Boolean.FALSE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.reopen();
        assertEquals(TaskStates.READY, task.getState());
        assertEquals(Boolean.TRUE, task.getState().isNew());
        assertEquals(Boolean.FALSE, task.getState().isStarted());
        assertEquals(Boolean.FALSE, task.getState().isDone());
        assertEquals(Boolean.FALSE, task.isBlocked());
        task.block();
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.prevState();
        assertEquals(TaskStates.PREP, task.getState());
        assertEquals(Boolean.TRUE, task.getState().isNew());
        assertEquals(Boolean.FALSE, task.getState().isStarted());
        assertEquals(Boolean.FALSE, task.getState().isDone());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.prevState();
        assertEquals(TaskStates.PREP, task.getState());
        assertEquals(Boolean.TRUE, task.getState().isNew());
        assertEquals(Boolean.FALSE, task.getState().isStarted());
        assertEquals(Boolean.FALSE, task.getState().isDone());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.start();
        assertEquals(TaskStates.IMPL, task.getState());
        assertEquals(Boolean.FALSE, task.getState().isNew());
        assertEquals(Boolean.TRUE, task.getState().isStarted());
        assertEquals(Boolean.FALSE, task.getState().isDone());
        assertEquals(Boolean.TRUE, task.isBlocked());
        assertEquals(Boolean.TRUE, task.isActive());
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());
    }

    @Test
    void archiving() {
        Task task = new Task();
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.archive();
        assertEquals(Boolean.TRUE, task.isArchived());
        assertEquals(Boolean.FALSE, task.isActual());

        task.setArchived(Boolean.FALSE);
        assertEquals(Boolean.FALSE, task.isArchived());
        assertEquals(Boolean.TRUE, task.isActual());

        task.setArchived(Boolean.TRUE);
        assertEquals(Boolean.TRUE, task.isArchived());
        assertEquals(Boolean.FALSE, task.isActual());
    }

    @Test
    void taskDateTime() {
        Task task = new Task();
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), task.getTaskDateTime().plus(500, ChronoUnit.MICROS).truncatedTo(ChronoUnit.SECONDS));
    }
}
