package eu.dec21.wp.tasks.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStatesTest {
    TaskStates taskStates;

    @Test
    void nextState() {
        taskStates = TaskStates.PREP;
        assertEquals(TaskStates.READY, taskStates.next());
        assertEquals(TaskStates.IMPL, taskStates.next().next());
        assertEquals(TaskStates.DONE, taskStates.next().next().next());
        assertEquals(TaskStates.CANCEL, taskStates.next().next().next().next());
        assertEquals(TaskStates.PREP, taskStates.next().next().next().next().next());
        assertEquals(TaskStates.READY, taskStates.next().next().next().next().next().next());
    }

    @Test
    void prevState() {
        taskStates = TaskStates.CANCEL;
        assertEquals(TaskStates.DONE, taskStates.previous());
        assertEquals(TaskStates.IMPL, taskStates.previous().previous());
        assertEquals(TaskStates.READY, taskStates.previous().previous().previous());
        assertEquals(TaskStates.PREP, taskStates.previous().previous().previous().previous());
        assertEquals(TaskStates.CANCEL, taskStates.previous().previous().previous().previous().previous());
        assertEquals(TaskStates.DONE, taskStates.previous().previous().previous().previous().previous().previous());
    }

    @Test
    void isDone() {
        assertTrue(TaskStates.DONE.isDone());
        assertTrue(TaskStates.CANCEL.isDone());

        assertFalse(TaskStates.PREP.isDone());
        assertFalse(TaskStates.READY.isDone());
        assertFalse(TaskStates.IMPL.isDone());
    }

    @Test
    void isNew() {
        assertTrue(TaskStates.PREP.isNew());
        assertTrue(TaskStates.READY.isNew());

        assertFalse(TaskStates.DONE.isNew());
        assertFalse(TaskStates.CANCEL.isNew());
        assertFalse(TaskStates.IMPL.isNew());
    }

    @Test
    void isStarted() {
        assertTrue(TaskStates.DONE.isStarted());
        assertTrue(TaskStates.CANCEL.isStarted());
        assertTrue(TaskStates.IMPL.isStarted());

        assertFalse(TaskStates.PREP.isStarted());
        assertFalse(TaskStates.READY.isStarted());
    }

    @Test
    void activeStates() {
        assertNotNull(TaskStates.activeStates().stream().filter(state -> state == TaskStates.PREP).findFirst().orElse(null));
        assertNotNull(TaskStates.activeStates().stream().filter(state -> state == TaskStates.READY).findFirst().orElse(null));
        assertNotNull(TaskStates.activeStates().stream().filter(state -> state == TaskStates.IMPL).findFirst().orElse(null));

        assertNull(TaskStates.activeStates().stream().filter(state -> state == TaskStates.DONE).findFirst().orElse(null));
        assertNull(TaskStates.activeStates().stream().filter(state -> state == TaskStates.CANCEL).findFirst().orElse(null));
    }

    @Test
    void inactiveStates() {
        assertNull(TaskStates.inactiveStates().stream().filter(state -> state == TaskStates.PREP).findFirst().orElse(null));
        assertNull(TaskStates.inactiveStates().stream().filter(state -> state == TaskStates.READY).findFirst().orElse(null));
        assertNull(TaskStates.inactiveStates().stream().filter(state -> state == TaskStates.IMPL).findFirst().orElse(null));

        assertNotNull(TaskStates.inactiveStates().stream().filter(state -> state == TaskStates.DONE).findFirst().orElse(null));
        assertNotNull(TaskStates.inactiveStates().stream().filter(state -> state == TaskStates.CANCEL).findFirst().orElse(null));
    }
}
