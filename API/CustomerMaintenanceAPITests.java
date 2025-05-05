import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CustomerMaintenanceAPITests {

    private static final String BASE_URL = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/customer-maintenance";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NDU5MDkzNTQsInR5cGUiOiJyZWZyZXNoIn0.8RW3jvg6-rFI9u2Sa7kJC_lv-1_VTLsZBmN4ESmkLK4"; // Replace with your valid token

    // Test Case 1: Valid Request (Positive Test Case)
    @Test
    public void testValidRequest() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");
    }

    // Test Case 2: Invalid Token (Negative Test Case)
    @Test
    public void testInvalidToken() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert the response status code is 401 Unauthorized
        Assert.assertEquals(response.statusCode(), 401, "Expected status code is 401 Unauthorized");
    }

    // Test Case 3: Missing Authorization Header (Negative Test Case)
    @Test
    public void testMissingAuthorizationHeader() {
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert the response status code is 400 Bad Request
        Assert.assertEquals(response.statusCode(), 403, "Expected status code is 403 Bad Request");
    }

    // Test Case 4: Invalid Query Parameter (Negative Test Case)
    @Test
    public void testInvalidQueryParameter() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", -1)  // Invalid page number
                .get(BASE_URL);

        // Assert the response status code is 400 Bad Request or 404 Not Found
        Assert.assertTrue(response.statusCode() == 400 || response.statusCode() == 404,
                "Expected status code is 400 or 404 for invalid query parameter");
    }

    // Test Case 5: Valid Query Parameters (Positive Test Case)
    @Test
    public void testValidQueryParameters() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .get(BASE_URL);

        // Assert the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");
    }

    // Test Case 6: Query Parameter Missing (Edge Case)
    @Test
    public void testQueryParameterMissing() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get(BASE_URL);  // Missing query parameters

        // Assert the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");
    }

    // Test Case 7: Authorization Header with Invalid Format (Negative Test Case)
    @Test
    public void testInvalidAuthorizationFormat() {
        Response response = RestAssured.given()
                .header("Authorization", "InvalidBearer " + TOKEN)
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert the response status code is 400 Bad Request
        Assert.assertEquals(response.statusCode(), 401, "Expected status code is 401 Bad Request");
    }

    // Test Case 8: Authorization Header with Empty Token (Negative Test Case)
    @Test
    public void testEmptyToken() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer ")
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert the response status code is 401 Unauthorized
        Assert.assertEquals(response.statusCode(), 401, "Expected status code is 401 Unauthorized");
    }

    // Test Case 9: Invalid API Endpoint (Negative Test Case)
    @Test
    public void testInvalidEndpoint() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/invalid-endpoint");

        // Assert the response status code is 404 Not Found
        Assert.assertEquals(response.statusCode(), 404, "Expected status code is 404 Not Found");
    }

    // Test Case 10: Large Response Body (Edge Case)
    @Test
    public void testLargeResponseBody() {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1000)  // A large page number to fetch large results
                .get(BASE_URL);

        // Assert the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");

        // Optionally, assert the size of the response body if needed
        Assert.assertTrue(response.body().asString().length() > 1000, "Response body is too small");
    }
}
