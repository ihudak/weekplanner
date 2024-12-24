package eu.dec21.wp.users.entity;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class UserDirector {
    private final UserBuilder userBuilder = new UserBuilder();
    private final Faker faker = new Faker();

    public User constructRandomUser() {
        return userBuilder.reset()
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail(faker.internet().emailAddress())
                .setPassword(faker.internet().password(8, 15, true, true, true))
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
