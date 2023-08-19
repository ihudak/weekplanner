package eu.dec21.wp.users.entity;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class UserDirector {
    private UserBuilder userBuilder = new UserBuilder();
    private Faker faker = new Faker();

    public User constructRandomUser() {
        return userBuilder.reset()
                .setId(faker.random().nextLong())
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail(faker.internet().emailAddress())
                .setAuthSystem("fb")
                .setAuthID(faker.internet().uuid())
                .build();
    }

    public List<User> constructRandomUsers(int numUsers) {
        if (numUsers < 0) {
            numUsers = 0;
        }

        var users = new ArrayList<User>(numUsers);
        for (int i = 0; i < numUsers; i++) {
            users.add(this.constructRandomUser());
        }

        return users;
    }
}
