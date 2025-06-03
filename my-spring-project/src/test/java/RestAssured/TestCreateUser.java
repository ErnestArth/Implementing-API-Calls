package RestAssured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class TestCreateUser {
//    private final String CONTEXT_PATH = "/my-spring-project";

    @BeforeEach
    void setUp() throws Exception {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/my-spring-project";

    }

    @Test
  final void createUser(){

        List<Map<String,Object>> addresses = new ArrayList<>();

        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Madina");
        shippingAddress.put("country", "Ghana");
        shippingAddress.put("streetName", "Afriyie Street");
        shippingAddress.put("postalCode", "739332");
        shippingAddress.put("type", "shipping");

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city",  "Madina");
        billingAddress.put("country", "Ghana");
        billingAddress.put("streetName", "Afriyie Street");
        billingAddress.put("postalCode", "739332");
        billingAddress.put("type", "billing");

        addresses.add(shippingAddress);
        addresses.add(billingAddress);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Ernest");
        userDetails.put("lastName", "Arthur");
        userDetails.put("email", "ernest5arthur@gmail.com");
        userDetails.put("password", "xzibit5+");
        userDetails.put("addresses", addresses);

       Response response = given()
               .contentType("application/json")
                .accept("application/json")
                .body(userDetails)
               .when().
                post( "/users")
                .then().statusCode(200)
               .contentType("application/json")
               .extract().response();

      String userId = response.jsonPath().getString("userId");
      assertNotNull(userId);
        assertEquals(50, userId.length());

      //validating the address details
        String stringBody = response.body().asString();  //converting the response body as a String
        try {
            JSONObject responseBodyJSON = new JSONObject(stringBody);  //returning the String object as a JSON object
            JSONArray userAddresses = responseBodyJSON.getJSONArray("addresses"); // getting a JSON array out responseBodyJSON i.e, it's an arrayList

            assertNotNull(userAddresses);
            assertEquals(2, userAddresses.length());

            //getting a specific address from the address array
           String addressId = userAddresses.getJSONObject(0).getString("addressId");
           assertNotNull(addressId);
            assertEquals(10, addressId.length());
        } catch (JSONException e) {
            fail(e.getMessage());
        }

    }
}
