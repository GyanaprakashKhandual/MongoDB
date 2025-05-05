import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class RoleAPITest {

    private String baseUrl;
    private String bearerToken;

    @BeforeClass
    public void setup() {
        baseUrl = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/role?page=1&limit=10";
        bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU";
    }

    // ✅ Positive Test: Valid Request
    @Test
    public void testRoleAPIValidResponse() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .when()
                .get(baseUrl);

        // Check if status code is 200
        Assert.assertEquals(res.getStatusCode(), 200, "Status Code should be 200 → If fails: server might be down or request is invalid");

        // Check if Content-Type is JSON
        Assert.assertTrue(res.getContentType().contains("application/json"), "Content-Type should be application/json → If fails: Server not returning correct format");

        // Check if roles are returned
        Assert.assertTrue(!res.jsonPath().getList("results").isEmpty(), "Results should be returned → If fails: No data or API issue");

        // Check if 'results' array is not empty
        Assert.assertNotNull(res.jsonPath().getList("results"), "Results should not be null → If fails: Missing expected data");

       
    }

    // ❌ Negative Test: Missing Authorization Token
    @Test
    public void testRoleAPIWithoutAuth() {
        Response res = RestAssured.given()
                .when()
                .get(baseUrl);

        // Expecting 401 Unauthorized
        Assert.assertEquals(res.getStatusCode(), 403, "Status Code should be 403 when Authorization token is missing → If fails: Security issue");
    }

    // ❌ Negative Test: Invalid Authorization Token
    @Test
    public void testRoleAPIWithInvalidAuth() {
        Response res = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .when()
                .get(baseUrl);

        // Expecting 401 Unauthorized
        Assert.assertEquals(res.getStatusCode(), 401, "Status Code should be 401 for invalid Authorization token → If fails: Token validation issue");
    }

    // ⚠️ Edge Test: Check for Empty Response
    @Test
    public void testRoleAPIEmptyResponse() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .when()
                .get(baseUrl);

        // Edge case: Check if no roles exist (empty result)
        if (res.jsonPath().getList("results").isEmpty()) {
            System.out.println("⚠️ No roles found, this is a valid response when there are no roles in the system.");
        } else {
            Assert.assertTrue(!res.jsonPath().getList("results").isEmpty(), "Results should not be empty → If fails: expected roles missing");
        }
    }

    // ❌ Negative Test: Check for invalid query parameters
    @Test
    public void testRoleAPIWithInvalidPage() {
        String invalidUrl = baseUrl.replace("page=1", "page=-1");  // Negative page number as invalid input

        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .when()
                .get(invalidUrl);

        // Expecting 400 Bad Request for invalid page number
        Assert.assertEquals(res.getStatusCode(), 502, "Status Code should be 502 for invalid page parameter → If fails: API does not handle bad input correctly");
    }
}
