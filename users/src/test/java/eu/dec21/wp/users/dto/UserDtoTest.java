package eu.dec21.wp.users.dto;

import eu.dec21.wp.exceptions.BadRequestException;
import eu.dec21.wp.users.entity.User;
import eu.dec21.wp.users.entity.UserDirector;
import eu.dec21.wp.users.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    String[] validEmails = {
            "boo@foo.com",
            "email@example.com",
            "firstname.lastname@example.com",
            "email@subdomain.example.com",
            "email@example.web",
            "1234567890@example.com",
            "email@example-one.com",
            "_______@example.com",
            "email@example.name",
            "email@example.museum",
            "email@example.co.jp",
            "firstname-lastname@example.com"
    };

    String[] invalidEmails = {
            "boo.foo.com",
            "plainaddress",
            "#@%^%#$@#$@#.com",
            "@example.com",
            "firstname+lastname@example.com",
            "email@123.123.123.123",
            "email@[123.123.123.123]",
            "\"email\"@example.com",
            "Joe Smith <email@example.com>",
            "email.example.com",
            "email@example@example.com",
            ".email@example.com",
            "email.@example.com",
            "email..email@example.com",
            "あいうえお@example.com",
            "email@example.com (Joe Smith)",
            "email@example",
            "email@-example.com",
            "email@111.222.333.44444",
            "email@example..com",
            "Abc..123@example.com",
            "”(),:;<>[\\]@example.com",
            "just”not”right@example.com",
            "this\\ is\"really\"not\\allowed@example.com",
            "much.”more\\ unusual”@example.com",
            "very.unusual.”@”.unusual.com@example.com",
            "very.”(),:;<>[]”.VERY.”very@\\\\ \"very”.unusual@strange.example.com"
    };

    @Test
    void setEmailToValidValue() {
        var user = new UserDto();

        for(String email: validEmails) {
            user.setEmail(email);
            assertEquals(email, user.getEmail());
        }
    }

    @Test
    void setEmailToInvalidValue() {
        var user = new UserDto();

        for(String email: invalidEmails) {
            assertThrowsExactly(BadRequestException.class, () -> {
                user.setEmail(email);
            });
        }
    }

    @Test
    void setId() {
        var user = new UserDto();
        user.setId((long) -1);
        assertEquals(-1L, user.getId());
    }

    @Test
    void setFirstName() {
        var user = new UserDto();
        user.setFirstName("Boo");
        assertEquals("Boo", user.getFirstName());

        user.setFirstName("BooBooBooBooBooBooBooBooBoo");
        assertEquals("BooBooBooBooBooBooBooBooBoo", user.getFirstName());
    }

    @Test
    void setLastName() {
        var user = new UserDto();
        user.setLastName("Boo");
        assertEquals("Boo", user.getLastName());

        user.setLastName("BooBooBooBooBooBooBooBooBoo");
        assertEquals("BooBooBooBooBooBooBooBooBoo", user.getLastName());
    }

    @Test
    void setAuthSystem() {
        var user = new UserDto();
        user.setAuthSystem("Boo");
        assertEquals("Boo", user.getAuthSystem());

        user.setAuthSystem("BooBooBooBooBooBooBooBooBoo");
        assertEquals("BooBooBooBooBooBooBooBooBoo", user.getAuthSystem());
    }

    @Test
    void setAuthID() {
        var user = new UserDto();
        user.setAuthID("Boo");
        assertEquals("Boo", user.getAuthID());

        user.setAuthID("BooBooBooBooBooBooBooBooBoo");
        assertEquals("BooBooBooBooBooBooBooBooBoo", user.getAuthID());
    }

    @Test
    void equalsComparesUsers() {
        UserDirector userDirector = new UserDirector();
        User user = userDirector.constructRandomUser();

        UserDto user1, user2;
        user1 = UserMapper.mapToUserDto(user);
        user2 = UserMapper.mapToUserDto(user);
        user1.setId(10L);
        user2.setId(20L);

        // different IDs
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));

        // different emails
        user2.setEmail("bii@fii.cii");
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));

        // different FirstName
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setFirstName("Boy");
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));

        // different LastName
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setLastName("Boy");
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));

        // different AuthSystem
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setAuthSystem("Boy");
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));

        // different AuthId
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setAuthID("Boy");
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));

        // firstName is null
        user1 = UserMapper.mapToUserDto(user);
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setFirstName(null);
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));
        user1.setFirstName(null);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));

        // lastName is null
        user1 = UserMapper.mapToUserDto(user);
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setLastName(null);
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));
        user1.setLastName(null);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));

        // authSystem is null
        user1 = UserMapper.mapToUserDto(user);
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setAuthSystem(null);
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));
        user1.setAuthSystem(null);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));

        // authId is null
        user1 = UserMapper.mapToUserDto(user);
        user2 = UserMapper.mapToUserDto(user);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        user2.setAuthID(null);
        assertFalse(user1.equals(user2));
        assertFalse(user2.equals(user1));
        user1.setAuthID(null);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
    }
}