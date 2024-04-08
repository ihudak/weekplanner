package eu.dec21.wp.tasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


//@AutoConfigureDataMongo
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
//@ActiveProfilesutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
public class TasksApplicationTest {

    @Test
    void contextLoads() {
    }
}
