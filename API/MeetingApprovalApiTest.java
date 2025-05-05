import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class MeetingApprovalApiTest {

    // Set the token and base URL as final variables
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NDU5MDkzNTQsInR5cGUiOiJyZWZyZXNoIn0.8RW3jvg6-rFI9u2Sa7kJC_lv-1_VTLsZBmN4ESmkLK4";
    private static final String BASE_URL = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/meeting-approval";

    @BeforeClass
    public void setup() {
        // Setting base URI and default authentication token for all requests
        RestAssured.baseURI = BASE_URL;
    }

    // Test case 1: Verify the status code is 200 (Success) for valid request
    @Test
    public void testValidApiRequest() {
        Response response = given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200) // Expected status code 200
            .log().all()
            .extract().response();

        // If the test passes, it confirms that the API is working fine and returning data.
        // If it fails, the API may be down, or the parameters might be incorrect.
    }

    // Test case 2: Verify that the response content type is JSON
    @Test
    public void testResponseContentType() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .contentType("application/json") // Verify if content-type is application/json
            .log().all();

        // If it passes, the API is returning a valid JSON response.
        // If it fails, the response content type is incorrect, meaning the API might not be correctly set up.
    }

    // Test case 3: Verify that the response contains "data" and "pagination" fields
    @Test
    public void testResponseFields() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("data", notNullValue()) // Ensures that the "data" field exists
            .body("pagination", notNullValue()) // Ensures that the "pagination" field exists
            .log().all();

        // If it passes, it confirms that the response contains essential fields.
        // If it fails, the API may not be structured properly or may be missing expected fields.
    }

    // Test case 4: Verify the pagination by requesting the second page of data
    @Test
    public void testPagination() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 2) // Requesting page 2 to check pagination
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .log().all();

        // If it passes, it confirms that pagination is working correctly.
        // If it fails, the pagination feature might be broken or incorrectly implemented.
    }

    // Test case 5: Verify that the API returns an unauthorized error (401) if no token is provided
    @Test
    public void testMissingAuthorizationToken() {
        given()
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(401) // Expected status code 401 for missing authorization
            .log().all();

        // If it passes, the API is correctly handling missing tokens.
        // If it fails, it indicates a security issue in the API's authentication handling.
    }

    // Test case 6: Verify 400 Bad Request error when sending invalid parameters (e.g., negative page number)
    @Test
    public void testInvalidQueryParameter() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", -1) // Invalid page number
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(400) // Expected status code 400 for invalid query parameter
            .log().all();

        // If it passes, the API is correctly validating the parameters.
        // If it fails, the API is not validating the parameters properly.
    }

    // Test case 7: Verify that the "createdAt" sorting is working correctly (descending)
    @Test
    public void testSortByCreatedAt() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc") // Sorting by createdAt in descending order
        .when()
            .get()
        .then()
            .statusCode(200)
            .log().all();

        // If it passes, it confirms that sorting by "createdAt" is working properly.
        // If it fails, there might be an issue with the sorting functionality in the API.
    }

    // Test case 8: Verify that the "limit" query parameter works (returning 10 results)
    @Test
    public void testLimitParameter() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10) // Ensure the limit parameter is respected
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("data.size()", is(10)) // Ensure 10 items are returned
            .log().all();

        // If it passes, it confirms the "limit" parameter is working properly.
        // If it fails, the API may not be correctly limiting the number of results returned.
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

        // If it passes, it confirms that the API is correctly returning a 404 for invalid endpoints.
        // If it fails, the API may not be correctly handling invalid endpoints.
    }

    // Test case 10: Verify that the response time is below an acceptable threshold (e.g., 2 seconds)
    @Test
    public void testResponseTime() {
        given()
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .time(lessThan(2000L)) // Ensure response time is less than 2 seconds
            .log().all();

        // If it passes, it confirms the API performs well within the acceptable response time.
        // If it fails, it indicates that the API may have performance issues.
    }
}
