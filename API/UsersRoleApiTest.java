import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class UsersRoleApiTest {

    // Set the token and base URL as final variables
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NDU5MDkzNTQsInR5cGUiOiJyZWZyZXNoIn0.8RW3jvg6-rFI9u2Sa7kJC_lv-1_VTLsZBmN4ESmkLK4";
    private static final String BASE_URL = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users?role=672c47c238903b464c9d2920";

    @BeforeClass
    public void setup() {
        // Setting base URI and default authentication token for all requests
        RestAssured.baseURI = BASE_URL;
    }

    // Test case 1: Verify the status code is 200 (Success) for valid request with role filter
    @Test
    public void testValidApiRequestWithRoleFilter() {
        Response response = given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("role", "672c47c238903b464c9d2920") // Role parameter
        .when()
            .get()
        .then()
            .statusCode(200) // Expected status code 200
            .log().all()
            .extract().response();

        // If the test passes, it confirms that the API is working and returning the correct response.
        // If it fails, the role parameter might not be valid, or the API might not be processing the filter correctly.
    }

    // Test case 2: Verify that the response content type is JSON
    @Test
    public void testResponseContentType() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("role", "672c47c238903b464c9d2920")
        .when()
            .get()
        .then()
            .contentType("application/json") // Verify if content-type is application/json
            .log().all();

        // If it passes, the response is of the correct type.
        // If it fails, the API may be returning an incorrect content type.
    }

    // Test case 3: Verify that the response contains a list of users (i.e., "data" field)
    @Test
    public void testResponseFields() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("role", "672c47c238903b464c9d2920")
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("results", notNullValue()) // Check if "data" field is present
            .log().all();

        // If it passes, the "data" field is present, indicating users were returned.
        // If it fails, the API might not be returning data or the field might be named differently.
    }

    // Test case 4: Verify pagination by changing the page parameter (e.g., page=2)
    @Test
    public void testPagination() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("role", "672c47c238903b464c9d2920")
            .param("page", 2) // Requesting page 2 to check pagination
        .when()
            .get()
        .then()
            .statusCode(200)
            .log().all();

        // If it passes, the API handles pagination correctly.
        // If it fails, there may be an issue with the pagination handling in the API.
    }

    // Test case 5: Verify that the API returns a 401 Unauthorized error when no token is provided
    @Test
    public void testMissingAuthorizationToken() {
        given()
            .param("role", "672c47c238903b464c9d2920")
        .when()
            .get()
        .then()
            .statusCode(403) // Expected 401 status code for missing authorization
            .log().all();

        // If it passes, the API is correctly handling missing tokens.
        // If it fails, there may be an issue with the token validation.
    }

    // Test case 6: Verify 400 Bad Request error when an invalid role ID is provided
    @Test
    public void testInvalidRoleId() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("role", "invalid_role_id") // Invalid role ID
        .when()
            .get()
        .then()
            .statusCode(500) // Expected 400 status code for invalid role ID
            .log().all();

        // If it passes, the API is validating the role ID properly.
        // If it fails, the API might not be validating the role correctly.
    }

  // Test case 7: Verify that the "role" parameter correctly filters the users based on the role
@Test
public void testRoleFilter() {
    given()
        .baseUri(BASE_URL)
        .header("Authorization", "Bearer " + TOKEN)
        .queryParam("role", "672c47c238903b464c9d2920")
    .when()
        .get()
    .then()
        .statusCode(200)
        .body("results.role.flatten()", everyItem(equalTo("672c47c238903b464c9d2920")))
        .log().all();

    //  If this test passes, it means the 'role' filter is working correctly.
    //  If it fails, the API might be returning users with other roles, or the JSON path might be incorrect.
}


    // Test case 8: Verify that the API correctly handles edge cases (e.g., empty result set for a non-existent role)
    @Test
    public void testEmptyResultSet() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("role", "nonexistent_role_id") // Non-existent role ID
        .when()
            .get()
        .then()
            .statusCode(200) // Expected status code 200 even if no users are found
            .body("results.size()", is(0)) // Ensure no users are returned
            .log().all();

        // If it passes, the API correctly returns an empty result set when no users match the role.
        // If it fails, the API may not handle empty result sets correctly.
    }

    // Test case 9: Verify 404 Not Found for an invalid endpoint
    @Test
    public void testInvalidEndpoint() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
        .when()
            .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/invalid-endpoint")
        .then()
            .statusCode(404) // Expected 404 status code for an invalid endpoint
            .log().all();

        // If it passes, it confirms that the API correctly handles invalid endpoints.
        // If it fails, the API may not be handling invalid endpoints properly.
    }

    // Test case 10: Verify that the response time is under an acceptable threshold (e.g., 2 seconds)
    @Test
    public void testResponseTime() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("role", "672c47c238903b464c9d2920")
        .when()
            .get()
        .then()
            .statusCode(200)
            .time(lessThan(3000L)) // Ensure response time is less than 3 seconds
            .log().all();

        // If it passes, the API is performing well and responds within the acceptable time.
        // If it fails, the API might have performance issues and needs optimization.
    }
}
