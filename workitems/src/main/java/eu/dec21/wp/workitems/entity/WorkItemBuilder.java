package eu.dec21.wp.workitems.entity;

import com.github.javafaker.Faker;

public class WorkItemBuilder {
    private WorkItem workItem;

    public WorkItemBuilder() {
        this.reset();
    }

    public WorkItemBuilder reset() {
        this.workItem = new WorkItem();
        Faker faker = new Faker();
        this.workItem.setId(faker.number().randomNumber());
        return this;
    }


    public WorkItemBuilder setName(String name) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setName(name);
        return this;
    }

    public WorkItemBuilder setDescription(String description) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setDescription(description);
        return this;
    }

    public WorkItemBuilder setCountry(String country) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setCountry(country);
        return this;
    }

    public WorkItemBuilder setCity(String city) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setCity(city);
        return this;
    }

    public WorkItemBuilder setId(long id) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setId(id);
        return this;
    }

    public WorkItemBuilder setPoints(int points) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setPoints(points);
        return this;
    }

    public WorkItemBuilder setCost(double cost) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setCost(cost);
        return this;
    }

    public WorkItemBuilder setAddress(String address) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setAddress(address);
        return this;
    }

    public WorkItemBuilder setAssignee(String assignee) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setAssignee(assignee);
        return this;
    }

    public WorkItemBuilder setBlocked(boolean blocked) {
        if (this.workItem == null) {
            this.reset();
        }
        this.workItem.setBlocked(blocked);
        return this;
    }

    public WorkItem build() {
        if (this.workItem == null) {
            this.reset();
        }
        return this.workItem;
    }
}
