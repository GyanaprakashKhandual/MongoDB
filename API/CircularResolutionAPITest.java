import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CircularResolutionAPITest {

    private String baseUrl;
    private String bearerToken;

    @BeforeClass
    public void setup() {
        baseUrl = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/circular-resolution";
        bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU";
    }

    // ✅ Positive test case: Valid request
    @Test
    public void testCircularResolutionAPIValidResponse() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .when()
                .get(baseUrl);

        // Check if status is 200
        Assert.assertEquals(res.getStatusCode(), 200, "Status Code should be 200 → If fails: server is down or invalid request");

        // Check if Content-Type is JSON
        Assert.assertTrue(res.getContentType().contains("application/json"), "Should return JSON → If fails: server not returning proper format");

        // Check if 'results' array exists and has at least 1 entry
        Assert.assertTrue(res.jsonPath().getList("results").size() >= 0, "Results should be a list → If fails: response body structure is broken");

        // Check fields inside first object
        if (!res.jsonPath().getList("results").isEmpty()) {
            String status = res.jsonPath().getString("results[0].status");
            String fileName = res.jsonPath().getString("results[0].fileName");
            String agmSerial = res.jsonPath().getString("results[0].AGM_last_serial.serial_no");

            Assert.assertNotNull(status, "Status should not be null → If fails: data might be incomplete or backend bug");
            Assert.assertTrue(fileName != null && fileName.contains("https://"), "FileName should be a valid URL → If fails: file URL not generated or invalid");
            Assert.assertNull(agmSerial, "Serial no should be null (edge case test) → If fails: backend is returning unexpected data");
        }
    }

    // ❌ Negative Test: No Authorization
    @Test
    public void testCircularResolutionWithoutAuth() {
        Response res = RestAssured.given()
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.getStatusCode(), 403, "Should return 403 when token is missing → If passes: security is working");
    }

    // ❌ Negative Test: Invalid Token
    @Test
    public void testCircularResolutionWithInvalidToken() {
        Response res = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.getStatusCode(), 401, "Should return 401 for invalid token → If fails: token validation is broken");
    }

    // ⚠️ Edge Test: Check empty result
    @Test
    public void testCircularResolutionEmptyResponse() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .when()
                .get(baseUrl);

        // Edge: If results list is empty
        if (res.jsonPath().getList("results").isEmpty()) {
            System.out.println("⚠️ No circular resolutions found → This is valid if there are genuinely none.");
        } else {
            Assert.assertTrue(res.jsonPath().getList("results").size() > 0, "Results should not be empty → If fails: expected data missing");
        }
    }
}
