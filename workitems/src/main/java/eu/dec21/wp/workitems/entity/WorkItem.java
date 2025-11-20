package eu.dec21.wp.workitems.entity;

import com.github.javafaker.Address;
import lombok.*;
import com.github.javafaker.Faker;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkItem {

    private static Random random = new Random();

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
        workItem.setId(random.nextLong());
        workItem.setName("Name" + random.nextLong());
        workItem.setDescription("Description" + random.nextLong());
        workItem.setCountry("Country" + random.nextLong());
        workItem.setCity("City" + random.nextLong());
        workItem.setAddress("Address" + random.nextLong());
        workItem.setAssignee("Assignee" + random.nextLong());
        workItem.setPoints(random.nextInt(100));
        workItem.setCost(random.nextDouble());
        workItem.setBlocked(random.nextBoolean());
        return workItem;
    }

    public long generateId() {
        if (id == 0) {
            id = random.nextLong();
        }
        return id;
    }
}
