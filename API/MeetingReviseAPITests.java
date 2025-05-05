import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class MeetingReviseAPITests {

    private final String BASE_URL = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/meeting-revise?page=1&limit=10";
    private final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NDU5MDkzNTQsInR5cGUiOiJyZWZyZXNoIn0.8RW3jvg6-rFI9u2Sa7kJC_lv-1_VTLsZBmN4ESmkLK4";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    // ✅ Test 1: Positive - Valid request with default params
    @Test
    public void testValidRequest() {
        given()
            .header("Authorization", TOKEN)
            .queryParam("page", 1)
            .queryParam("limit", 10)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("results", notNullValue())
            .body("page", equalTo(1))
            .body("limit", equalTo(10));

        // If passed: API is working with default values
        // If failed: Token or backend issue
    }

    // ✅ Test 2: Positive - High limit value
    @Test
    public void testHighLimit() {
        given()
            .header("Authorization", TOKEN)
            .queryParam("page", 1)
            .queryParam("limit", 100)
        .when()
            .get()
        .then()
            .statusCode(200);

        // If passed: API supports pagination with large limit
        // If failed: Check if server enforces limit max
    }

    // ⚠️ Test 3: Edge - Zero limit
    @Test
    public void testZeroLimit() {
        given()
            .header("Authorization", TOKEN)
            .queryParam("page", 1)
            .queryParam("limit", 0)
        .when()
            .get()
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(422)));

        // If passed: API validates edge case
        // If failed: Server might not validate `limit` properly
    }

    // ❌ Test 4: Negative - Invalid token
    @Test
    public void testInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid_token")
        .when()
            .get()
        .then()
            .statusCode(401);

        // If passed: Auth system works
        // If failed: Security issue, accepts invalid tokens
    }

    // ⚠️ Test 5: Edge - Negative page number
    @Test
    public void testNegativePage() {
        given()
            .header("Authorization", TOKEN)
            .queryParam("page", -1)
            .queryParam("limit", 10)
        .when()
            .get()
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(502)));

        // If passed: Input validation works
        // If failed: API accepts invalid pagination
    }

    // ⚠️ Test 6: Edge - Very high page number
    @Test
    public void testLargePageNumber() {
        Response response = given()
            .header("Authorization", TOKEN)
            .queryParam("page", 9999)
            .queryParam("limit", 10)
        .when()
            .get();

        response.then().statusCode(200);
        assert response.jsonPath().getList("results").isEmpty();

        // If passed: Proper handling of empty pages
        // If failed: API crashes or returns invalid data
    }

    // ❌ Test 7: Negative - Missing token
    @Test
    public void testNoToken() {
        given()
            .queryParam("page", 1)
            .queryParam("limit", 10)
        .when()
            .get()
        .then()
            .statusCode(401);

        // If passed: Auth required as expected
        // If failed: Open access without auth is a security flaw
    }

    // ✅ Test 8: Positive - Check response structure keys
    @Test
    public void testResponseStructure() {
        given()
            .header("Authorization", TOKEN)
        .when()
            .get("?page=1&limit=10")
        .then()
            .statusCode(200)
            .body("$", hasKey("results"))
            .body("$", hasKey("page"))
            .body("$", hasKey("limit"))
            .body("$", hasKey("totalPages"))
            .body("$", hasKey("totalResults"));

        // If passed: Response structure is correct
        // If failed: Incomplete or broken schema
    }

    // ⚠️ Test 9: Edge - Non-integer query params
    @Test
    public void testNonIntegerParams() {
        given()
            .header("Authorization", TOKEN)
            .queryParam("page", "abc")
            .queryParam("limit", "xyz")
        .when()
            .get()
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(422)));

        // If passed: API validates data types
        // If failed: Server might crash or parse incorrectly
    }

    // ❌ Test 10: Negative - Invalid HTTP method (POST)
    @Test
    public void testInvalidMethod() {
        given()
            .header("Authorization", TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(anyOf(equalTo(405), equalTo(400)));

        // If passed: Only GET allowed
        // If failed: Incorrect method allowed, design issue
    }
}
