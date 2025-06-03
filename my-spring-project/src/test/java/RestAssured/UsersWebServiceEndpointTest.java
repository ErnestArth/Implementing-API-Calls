package RestAssured;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersWebServiceEndpointTest {

    private static String authorizationHeader;
    private static String userId;

    @BeforeEach
     void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/my-spring-project";
    }

    @Order(1)
    @Test
    final void testLoginUser() {
        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", "ernest5arthur@gmail.com");
        loginDetails.put("password", "xzibit5+");

    Response response =  given().contentType("application/json")
              .accept("application/json")
              .body(loginDetails)
              .when().post("/users/login")
              .then().statusCode(200)
              .extract().response();

     authorizationHeader = response.header("Authorization");
     userId = response.header("UserId");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }


    @Order(2)
    @Test
    final void testGetUserDetails() {

       Response response = given().pathParam("id", userId)
                .header("Authorization", authorizationHeader)
                .contentType("application/json")
                .accept("application/json")
                .when().get("/users/{id}")
                .then().statusCode(200)
                .contentType("application/json")
                .extract().response();

       String userPublicId = response.jsonPath().getString("userId");
       String userEmail = response.jsonPath().getString("email");
       String firstName = response.jsonPath().getString("firstName");
       String lastName = response.jsonPath().getString("lastName");
//       List<Map<String, String>> addresses = response.jsonPath().getList("userAddresses");
//       String addressId = addresses.get(0).get("addressId");

       assertNotNull(userPublicId);
       assertNotNull(userEmail);
       assertNotNull(firstName);
       assertNotNull(lastName);
       assertEquals("ernest5arthur@gmail.com", userEmail);
//       assertEquals(2, addresses.size());
//       assertEquals(10, addressId.length());
    }


    @Order(3)
    @Test
    final void testUpdateUserDetails() {


        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Exhibit");
        userDetails.put("lastName", "Wilson");

          Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", authorizationHeader).pathParam("id", userId)
                  .body(userDetails)
                        .when().put("/users/{id}")
                        .then().statusCode(200)
                        .contentType("application/json")
                        .extract().response();

          String firstName = response.jsonPath().getString("firstName");
          String lastName = response.jsonPath().getString("lastName");
//          List<Map<String, Object>> savedAddresses = response.jsonPath().getList("userAddresses");

          assertEquals("Exhibit",  firstName);
          assertEquals("Wilson",  lastName);
//          assertNotNull(savedAddresses);
    }


    @Order(4)
    @Disabled  // if we don't want to execute this method
    @Test
    final void testDeleteUser() {

       Response response = given()
                .header("Authorization", authorizationHeader)
                .accept("application/json")
                .pathParam("id", userId)
                .when().delete("/users/{id}")
                .then().statusCode(200)
                .contentType("application/json")
                .extract().response();

      String operationResult = response.jsonPath().getString("operationResult");
      assertEquals("SUCCESS", operationResult);
    }
}
