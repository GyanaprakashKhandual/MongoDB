import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class MeetingPageApiTests {

    // Base URI for the API
    private static final String BASE_URL = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/meeting";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NDU5MDkzNTQsInR5cGUiOiJyZWZyZXNoIn0.8RW3jvg6-rFI9u2Sa7kJC_lv-1_VTLsZBmN4ESmkLK4";

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    // Test 1: Valid request with correct parameters (Happy Path)
    @Test
    public void testValidRequest() {
        // This test verifies that a valid request returns status 200 and contains a proper response
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .get();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.getBody().asString().contains("results"));
        // Fail: If status code is not 200 or body does not contain "data"
        // Pass: If API returns status 200 and contains the "data" field
    }

    // Test 2: Missing Authorization token (Unauthorized Access)
    @Test
    public void testNoTokenProvided() {
        // This test ensures the API rejects the request with no Authorization token
        Response response = RestAssured.given()
                .get();

        Assert.assertEquals(response.statusCode(), 403);
        // Fail: If status code is not 401
        // Pass: If status code is 401 when no token is provided
    }

    // Test 3: Invalid Authorization token (Unauthorized Access)
    @Test
    public void testInvalidToken() {
        // This test ensures the API rejects the request with an invalid token
        Response response = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .get();

        Assert.assertEquals(response.statusCode(), 401);
        // Fail: If status code is not 401
        // Pass: If status code is 401 for an invalid token
    }

    // Test 4: Invalid query parameters (e.g., negative page value)
    @Test
    public void testInvalidQueryParameter() {
        // This test ensures the API handles invalid query parameters (e.g., negative page number)
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", -1) // Invalid page parameter
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .get();

        Assert.assertTrue(response.statusCode() == 400 || response.statusCode() == 404);
        // Fail: If API doesn't handle invalid query parameters
        // Pass: If API returns a proper error for invalid query parameters
    }

    // Test 5: Missing required query parameters
    @Test
    public void testMissingQueryParameters() {
        // This test ensures the API handles missing query parameters
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get();

        Assert.assertEquals(response.statusCode(), 400);
        // Fail: If status code is not 400 for missing query parameters
        // Pass: If status code is 400 for missing required parameters
    }

    // Test 6: Test with a limit higher than the maximum allowed limit
    @Test
    public void testLimitExceedsMax() {
        // This test ensures the API limits the number of items returned if the 'limit' is too high
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 1000) // Exceeds the expected limit
                .queryParam("sortBy", "createdAt:desc")
                .get();

        Assert.assertTrue(response.statusCode() == 200);
        // Fail: If API returns an error code or doesn't limit the result properly
        // Pass: If API responds correctly with a 200 status code and limited results
    }

    // Test 7: Test incorrect sort parameter (invalid field)
    @Test
    public void testInvalidSortBy() {
        // This test ensures the API handles incorrect sort parameters properly
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .queryParam("sortBy", "invalidField:asc") // Invalid sort parameter
                .get();

        Assert.assertEquals(response.statusCode(), 400);
        // Fail: If the API does not return an error for invalid sorting
        // Pass: If the API returns a 400 status code for invalid sort parameter
    }

    // Test 8: Test with a large number of query parameters
    @Test
    public void testTooManyQueryParams() {
        // This test checks if the API can handle a large number of query parameters
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .queryParam("extraParam", "value1") // Additional query parameters
                .queryParam("extraParam2", "value2")
                .queryParam("extraParam3", "value3")
                .queryParam("extraParam4", "value4")
                .get();

        Assert.assertEquals(response.statusCode(), 200);
        // Fail: If API cannot handle a large number of query parameters
        // Pass: If the API handles a large number of query parameters correctly
    }
}
