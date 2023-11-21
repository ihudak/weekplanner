package eu.dec21.wp.users.repository;

import com.github.javafaker.Faker;
import eu.dec21.wp.users.entity.User;
import eu.dec21.wp.users.entity.UserBuilder;
import eu.dec21.wp.users.entity.UserDirector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {
    private static final Faker faker = new Faker();

    private final UserBuilder userBuilder = new UserBuilder();

    private final UserDirector userDirector = new UserDirector();

    private String getRandStr(int length) {
        return faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void save10Users() {
        var users = userDirector.constructRandomUsers(10);

        userRepository.saveAll(users);

        assertEquals(10, userRepository.count());
    }

    @Test
    void findByEmail() {
        List<User> users = userDirector.constructRandomUsers(2);

        String email = users.get(0).getEmail();

        User user1 = userRepository.save(users.get(0));
        assertNotNull(user1);
        userRepository.save(users.get(1));

        Optional<User> user = userRepository.findByEmail(email);
        assertTrue(user.isPresent());
        assertEquals(user1.getId(), user.get().getId());

        user = userRepository.findByEmail("boo@foo.com");
        assertFalse(user.isPresent());
    }

    @Test
    void emailConstraintShouldViolate() {
        String sameEmail = faker.internet().emailAddress();
        List<User> users = userDirector.constructRandomUsers(3);
        users.get(0).setEmail(sameEmail);
        users.get(2).setEmail(sameEmail);

        userRepository.save(users.get(0));
        userRepository.save(users.get(1));

        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            userRepository.save(users.get(2));
        });
    }

    @Test
    void authSystemAndIDConstraintShouldViolate() {
        String sameID = faker.internet().uuid();
        List<User> users = userDirector.constructRandomUsers(3);
        users.get(0).setAuthID(sameID);
        users.get(2).setAuthID(sameID);

        userRepository.save(users.get(0));
        userRepository.save(users.get(1));

        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            userRepository.save(users.get(2));
        });
    }

    @Test
    void firstNameTooLong() {
        var user = userDirector.constructRandomUser();

        String normName = getRandStr(25);
        String longName = getRandStr(26);

        user.setFirstName(normName);
        var usr = userRepository.save(user);
        assertEquals(normName, usr.getFirstName());

        user.setFirstName(longName);
        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
                    userRepository.save(user);
        });
    }

    @Test
    void lastNameTooLong() {
        var user = userDirector.constructRandomUser();

        String normName = getRandStr(25);
        String longName = getRandStr(26);

        user.setLastName(normName);
        var usr = userRepository.save(user);
        assertEquals(normName, usr.getLastName());

        user.setLastName(longName);
        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    void authSystemTooLong() {
        var user = userDirector.constructRandomUser();

        String normName = getRandStr(12);
        String longName = getRandStr(13);

        user.setAuthSystem(normName);
        var usr = userRepository.save(user);
        assertEquals(normName, usr.getAuthSystem());

        user.setAuthSystem(longName);
        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    void authIDTooLong() {
        var user = userDirector.constructRandomUser();

        String normName = getRandStr(255);
        String longName = getRandStr(256);

        user.setAuthID(normName);
        var usr = userRepository.save(user);
        assertEquals(normName, usr.getAuthID());

        user.setAuthID(longName);
        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    void emailMustExist() {
        // build user w/o email
        var user = userBuilder.reset()
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .build();
        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    void suspendedWorks() {
        var user = userBuilder.reset()
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail(faker.internet().emailAddress())
                .setPassword(faker.internet().password(8, 15, true, true, true))
                .build();
        assertFalse(user.isSuspended());
        user.setSuspended(true);
        userRepository.save(user);

        var user1 = userRepository.findByEmail(user.getEmail());
        assertTrue(user1.get().isSuspended());
    }
}