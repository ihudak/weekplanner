package eu.dec21.wp.users.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
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
        User user = new User();

        for(String email: validEmails) {
            user.setEmail(email);
            assertEquals(email, user.getEmail());
        }
    }

    @Test
    void setEmailToInvalidValue() {
        User user = new User();

        for(String email: invalidEmails) {
            user.setEmail(email);
            assertEquals(email, user.getEmail());
        }
    }
}