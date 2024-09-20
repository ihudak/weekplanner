package eu.dec21.wp.workitems.entity;

import com.github.javafaker.Address;
import lombok.*;
import com.github.javafaker.Faker;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkItem {

    private static Faker faker = new Faker();

    private long id;

    private String name;

    private String description;

    private String country;

    private String city;

    private String address;

    private String assignee;

    private int points;

    private double cost;

    private boolean blocked;

    public static WorkItem generateWorkItem() {
        WorkItem workItem = new WorkItem();
        workItem.setId(faker.random().nextLong());
        workItem.setName(faker.app().name());
        workItem.setDescription(faker.book().title());
        Address address = faker.address();
        workItem.setCountry(address.country());
        workItem.setCity(address.city());
        workItem.setAddress(address.fullAddress());
        workItem.setAssignee(address.lastName() + " " + address.firstName());
        workItem.setPoints(faker.number().numberBetween(1, 100));
        workItem.setCost(faker.random().nextDouble());
        workItem.setBlocked(faker.bool().bool());
        return workItem;
    }

    public long generateId() {
        if (  id == 0) {
            id = faker.random().nextLong();
        }
        return id;
    }
}
