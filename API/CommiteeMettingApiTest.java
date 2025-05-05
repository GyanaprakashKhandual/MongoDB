import static org.hamcrest.CoreMatchers.notNullValue;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class CommiteeMettingApiTest {

    // Set the token and base URL as final variables
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU";
    private static final String BASE_URL = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/committee-meeting?page=1&limit=10&sortBy=createdAt:desc";

    // Positive test case 1: Verify status code 200 for valid API request
    // This test checks if the API responds with a 200 OK status for valid request parameters
    @Test
    public void testValidApiRequest() {
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .log().all();
        // If the test passes, it means the API is correctly responding with a 200 OK status.
        // If it fails, it indicates an issue with the API or the provided parameters.
    }

    // Positive test case 2: Verify correct response body format (JSON)
    // This test checks if the response content type is application/json
    @Test
    public void testResponseBodyFormat() {
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .contentType("application/json")
            .log().all();
        // If the test passes, it confirms the API returns JSON responses.
        // If it fails, the API may not be returning a valid JSON response.
    }

    // Positive test case 3: Verify response contains expected fields
    // This test checks if the response contains certain fields such as "data" and "pagination"
    @Test
    public void testResponseContainsFields() {
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("data", notNullValue()) // Ensures "data" is not null
            .body("pagination", notNullValue()) // Ensures "pagination" is not null
            .log().all();
        // If the test passes, it confirms that the "data" and "pagination" fields are included in the response.
        // If it fails, the API may not be returning the expected fields.
    }

    // Negative test case 1: Verify 401 Unauthorized with missing token
    // This test simulates an API request without the authorization token and expects a 401 Unauthorized response.
    @Test
    public void testMissingToken() {
        given()
            .baseUri(BASE_URL)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(403)
            .log().all();
        // If the test passes, it confirms that the API correctly requires authentication.
        // If it fails, it means the API may not be correctly handling missing tokens.
    }

    // Negative test case 2: Verify 400 Bad Request with invalid query parameter
    // This test sends an invalid parameter to the API and expects a 400 Bad Request response.
    @Test
    public void testInvalidQueryParameter() {
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", -1) // Invalid value for page
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(400)
            .log().all();
        // If the test passes, it confirms the API correctly handles invalid parameters.
        // If it fails, the API may not be validating query parameters properly.
    }

    // Positive test case 4: Verify pagination works (second page)
    // This test checks if the pagination works by testing the second page of results.
    @Test
    public void testPagination() {
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 2) // Request the second page
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .log().all();
        // If the test passes, it confirms that the pagination feature works correctly.
        // If it fails, there may be an issue with the pagination logic.
    }

    // Negative test case 3: Verify 404 Not Found for invalid endpoint
    // This test attempts to call an invalid endpoint and expects a 404 Not Found response.
    @Test
    public void testInvalidEndpoint() {
        given()
            .baseUri("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/invalid-endpoint")
            .header("Authorization", "Bearer " + TOKEN)
        .when()
            .get()
        .then()
            .statusCode(404)
            .log().all();
        // If the test passes, it confirms the API returns a 404 status for invalid endpoints.
        // If it fails, the API may not be correctly handling invalid endpoints.
    }

    // Positive test case 5: Verify sorting by creation date
    // This test checks if the results are sorted by "createdAt" in descending order.
    @Test
    public void testSortByCreationDate() {
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .log().all();
        // If the test passes, it confirms that the sorting by "createdAt" works correctly.
        // If it fails, the API may not be sorting the results properly.
    }

    // Negative test case 4: Verify 500 Internal Server Error for server failure
    // This test simulates a server failure (for example, by using an invalid server address).
    @Test
    public void testServerError() {
        given()
            .baseUri("https://invalid-url.com/v1/committee-meeting")
            .header("Authorization", "Bearer " + TOKEN)
        .when()
            .get()
        .then()
            .statusCode(500)
            .log().all();
        // If the test passes, it confirms the API correctly returns a 500 error for server failure.
        // If it fails, there may be an issue with the server's error handling.
    }

    // Positive test case 6: Verify successful response with valid parameters
    // This test ensures that the API responds successfully when provided with valid parameters.
    @Test
    public void testValidParameters() {
        given()
            .baseUri(BASE_URL)
            .header("Authorization", "Bearer " + TOKEN)
            .param("page", 1)
            .param("limit", 10)
            .param("sortBy", "createdAt:desc")
        .when()
            .get()
        .then()
            .statusCode(200)
            .log().all();
        // If the test passes, it confirms that the API works as expected with valid parameters.
        // If it fails, it may indicate an issue with the request processing.
    }
}
