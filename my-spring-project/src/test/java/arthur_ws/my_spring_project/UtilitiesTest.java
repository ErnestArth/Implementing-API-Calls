package arthur_ws.my_spring_project;

import arthur_ws.my_spring_project.shared.dto.Utilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UtilitiesTest {

    @Autowired
    Utilities utilities;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    final void testGenerateUserId() {
       String userId1 = utilities.generateUserId(50);
       String userId2 = utilities.generateUserId(50);

       assertNotNull(userId1);
       assertNotNull(userId2);
        assertTrue(userId1.length() == 50);
        assertFalse(userId1.equalsIgnoreCase(userId2));
    }

    @Test
    final void testHasTokenExpired() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkaXZpbmVnaWRlb25AZ21haWwuY29tIiwicm9sZXMiOltdLCJleHAiOjE3NDg1MDYyNTYsImlhdCI6MTc0NzY0MjI1Nn0.eiLsIQZA-Kbld8JB1kFARTgikq4HNvHtlanqvxrRT8b1alJWiHq9MimvQ_gz5hW0lWSsjw1tscovU9saXcGYww";
        boolean hasTokenExpired = Utilities.hasTokenExpired(token);

       assertFalse(hasTokenExpired);
    }
}
