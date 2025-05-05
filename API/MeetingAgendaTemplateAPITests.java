import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class MeetingAgendaTemplateAPITests {

    private static final String BASE_URL = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/meeting-agenda-template";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NDU5MDkzNTQsInR5cGUiOiJyZWZyZXNoIn0.8RW3jvg6-rFI9u2Sa7kJC_lv-1_VTLsZBmN4ESmkLK4"; // Replace with your valid token

    // Test Case 1: Valid Request (Positive Test Case)
    @Test
    public void testValidRequest() {
        // Sending a GET request with valid authorization token
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert that the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");

        // If the test passes, the API responded correctly with status 200
        // If it fails, the API did not return 200 OK
    }

    // Test Case 2: Invalid Token (Negative Test Case)
    @Test
    public void testInvalidToken() {
        // Sending a GET request with an invalid authorization token
        Response response = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert that the response status code is 401 Unauthorized
        Assert.assertEquals(response.statusCode(), 401, "Expected status code is 401 Unauthorized");

        // If the test passes, the server correctly rejected the invalid token with a 401 Unauthorized
        // If it fails, the server did not reject the invalid token as expected
    }

    // Test Case 3: Missing Authorization Header (Negative Test Case)
    @Test
    public void testMissingAuthorizationHeader() {
        // Sending a GET request without the Authorization header
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert that the response status code is 400 Bad Request
        Assert.assertEquals(response.statusCode(), 403, "Expected status code is 403 Bad Request");

        // If the test passes, the server correctly handled the missing Authorization header with a 400 Bad Request
        // If it fails, the server did not handle the missing Authorization header correctly
    }

    // Test Case 4: Invalid API Endpoint (Negative Test Case)
    @Test
    public void testInvalidEndpoint() {
        // Sending a GET request to an invalid endpoint
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/invalid-endpoint");

        // Assert that the response status code is 404 Not Found
        Assert.assertEquals(response.statusCode(), 404, "Expected status code is 404 Not Found");

        // If the test passes, the server correctly responded with a 404 Not Found
        // If it fails, the server did not return a 404 status for an invalid endpoint
    }

    // Test Case 5: Authorization Header with Invalid Format (Negative Test Case)
    @Test
    public void testInvalidAuthorizationFormat() {
        // Sending a GET request with an incorrect Authorization header format
        Response response = RestAssured.given()
                .header("Authorization", "InvalidBearer " + TOKEN)
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert that the response status code is 400 Bad Request
        Assert.assertEquals(response.statusCode(), 401, "Expected status code is 401 Bad Request");

        // If the test passes, the server correctly handled the invalid Authorization header format with a 400 Bad Request
        // If it fails, the server did not handle the invalid Authorization header format correctly
    }

    // Test Case 6: Empty Token (Negative Test Case)
    @Test
    public void testEmptyToken() {
        // Sending a GET request with an empty token in the Authorization header
        Response response = RestAssured.given()
                .header("Authorization", "Bearer ")
                .header("Accept", "application/json")
                .get(BASE_URL);

        // Assert that the response status code is 401 Unauthorized
        Assert.assertEquals(response.statusCode(), 401, "Expected status code is 401 Unauthorized");

        // If the test passes, the server correctly rejected the empty token with a 401 Unauthorized
        // If it fails, the server did not reject the empty token as expected
    }

    // Test Case 7: Missing Accept Header (Edge Case)
    @Test
    public void testMissingAcceptHeader() {
        // Sending a GET request without the Accept header
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get(BASE_URL);

        // Assert that the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");

        // If the test passes, the server handled the missing Accept header and returned a 200 OK
        // If it fails, the server did not handle the missing Accept header correctly
    }

    // Test Case 8: Valid Query Parameters (Positive Test Case)
    @Test
    public void testValidQueryParameters() {
        // Sending a GET request with valid query parameters (if supported by the API)
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .get(BASE_URL);

        // Assert that the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");

        // If the test passes, the server successfully handled the query parameters
        // If it fails, the server did not handle the valid query parameters as expected
    }

    // Test Case 9: Large Response Body (Edge Case)
    @Test
    public void testLargeResponseBody() {
        // Sending a GET request to receive a large response body (if applicable)
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1000)  // Requesting a large page number to fetch a large result set
                .get(BASE_URL);

        // Assert that the response status code is 200 OK
        Assert.assertEquals(response.statusCode(), 200, "Expected status code is 200 OK");

        // Optionally, assert that the response body is large (this depends on your API and response size)
        Assert.assertTrue(response.body().asString().length() > 1000, "Response body is too small");

        // If the test passes, the server correctly handled large response data
        // If it fails, the server did not handle large responses as expected
    }

    // Test Case 10: Invalid Content-Type (Negative Test Case)
    @Test
    public void testInvalidContentType() {
        // Sending a GET request with an incorrect Content-Type header
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .header("Content-Type", "text/html")  // Invalid content type
                .get(BASE_URL);

        // Assert that the response status code is 415 Unsupported Media Type
        Assert.assertEquals(response.statusCode(), 415, "Expected status code is 415 Unsupported Media Type");

        // If the test passes, the server correctly handled the invalid Content-Type with a 415 status
        // If it fails, the server did not handle the invalid Content-Type header correctly
    }
}
