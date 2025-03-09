package eu.dec21.wp.tasks.collection;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskLinkTest {

    @Test
    void testTaskLinkConstructor() {
        TaskLink taskLink = new TaskLink();
        assertThrowsExactly(IllegalArgumentException.class, () -> new TaskLink("", ""));

        assertNull(taskLink.getName());
        assertNull(taskLink.getUrl());

        String sampleUrl = "https://www.example.com";
        taskLink = new TaskLink("name", sampleUrl);
        assertEquals("name", taskLink.getName());
        assertEquals(sampleUrl, taskLink.getUrl());

        if (!sampleUrl.endsWith("/")) {
            sampleUrl += "/";
        }
        int lengthName = 25;
        int lengthUrl = 255 - sampleUrl.length();
        final Faker faker = new Faker();
        final String name = faker.regexify("[A-Z][a-z]{" + (lengthName - 1) + "}");
        final String url = sampleUrl + faker.regexify("[A-Z][a-z]{" + (lengthUrl - 1) + "}");

        taskLink = new TaskLink(name, url);
        assertEquals(name, taskLink.getName());
        assertEquals(url, taskLink.getUrl());

        assertThrowsExactly(IllegalArgumentException.class, () -> new TaskLink(name + "1", url));
        assertThrowsExactly(IllegalArgumentException.class, () -> new TaskLink(name, url + "1"));
        assertThrowsExactly(IllegalArgumentException.class, () -> new TaskLink("1", url));
        assertThrowsExactly(IllegalArgumentException.class, () -> new TaskLink(name, "0123456789A"));

        taskLink = new TaskLink(name, url);
        assertEquals(name, taskLink.getName());
        assertEquals(url, taskLink.getUrl());
    }

    @Test
    void testSetName() {
        TaskLink taskLink = new TaskLink();
        taskLink.setName("name");
        assertEquals("name", taskLink.getName());

        assertThrowsExactly(NullPointerException.class, () -> taskLink.setName(null));
        assertEquals("name", taskLink.getName());

        final Faker faker = new Faker();
        int length = 25;
        final String name = faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
        taskLink.setName(name);
        assertEquals(name, taskLink.getName());

        assertThrowsExactly(IllegalArgumentException.class, () -> taskLink.setName(name + "1"));
        assertEquals(name, taskLink.getName());
    }

    @Test
    void testSetUrl() {
        TaskLink taskLink = new TaskLink();
        String sampleUrl = "https://www.example.com/";
        taskLink.setUrl(sampleUrl);
        assertEquals(sampleUrl, taskLink.getUrl());

        assertThrowsExactly(NullPointerException.class, () -> taskLink.setUrl(null));
        assertEquals(sampleUrl, taskLink.getUrl());

        final Faker faker = new Faker();
        int length = 255 - sampleUrl.length();
        final String url = sampleUrl + faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
        taskLink.setUrl(url);
        assertEquals(url, taskLink.getUrl());

        assertThrowsExactly(IllegalArgumentException.class, () -> taskLink.setUrl(url + "1"));
        assertThrowsExactly(IllegalArgumentException.class, () -> taskLink.setUrl("not correct url to pass"));
        assertThrowsExactly(IllegalArgumentException.class, () -> taskLink.setUrl("/relative/path/to/something/fail"));
        assertEquals(url, taskLink.getUrl());
    }
}
