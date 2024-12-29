package eu.dec21.wp.tasks.collection;

public enum TaskStates {
    PREP,READY,IMPL,DONE,CANCEL;

    private static final TaskStates[] VALUES = values();
    public TaskStates next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
    public TaskStates previous() {
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
}
