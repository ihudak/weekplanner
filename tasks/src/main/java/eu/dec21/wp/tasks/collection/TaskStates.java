package eu.dec21.wp.tasks.collection;

import java.util.ArrayList;
import java.util.List;

public enum TaskStates {
    PREP,READY,IMPL,DONE,CANCEL;

    private static final TaskStates[] VALUES = values();
    public TaskStates next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
    public TaskStates previous() {
        if (this.ordinal() == 0) {
            return CANCEL;
        }
        return VALUES[(this.ordinal() - 1) % VALUES.length];
    }

    public boolean isDone() {
        return this == DONE || this == CANCEL;
    }

    public boolean isNew() {
        return this == PREP || this == READY;
    }

    public boolean isStarted() {
        return this == IMPL || this == DONE || this == CANCEL;
    }

    public static List<TaskStates> activeStates() {
        ArrayList<TaskStates> states = new ArrayList<TaskStates>();
        for (TaskStates state : VALUES) {
            if (!state.isDone()) {
                states.add(state);
            }
        }
        return states;
    }

    public static List<TaskStates> inactiveStates() {
        ArrayList<TaskStates> states = new ArrayList<TaskStates>();
        for (TaskStates state : VALUES) {
            if (state.isDone()) {
                states.add(state);
            }
        }
        return states;
    }
}
