import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CustomerMaintenanceAPITest {

    private String baseUrl;
    private String bearerToken;

    @BeforeClass
    public void setup() {
        // Base URL for the API
        baseUrl = "https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/customer-maintenance?page=1&limit=10";
        // Replace with a valid token
        bearerToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU";
    }

    // ✅ Positive Test Case: Basic GET Request
    @Test
    public void testValidCustomerMaintenanceRequest() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .param("page", 1)
                .param("limit", 10)
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.statusCode(), 200, "Status Code should be 200");
        Assert.assertTrue(res.getBody().asString().contains("docs"), "Response should contain 'docs'");
    }

    // ❌ Negative Test: No Authorization
    @Test
    public void testRequestWithoutAuthorization() {
        Response res = RestAssured.given()
                .param("page", 1)
                .param("limit", 10)
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.statusCode(), 401, "Should return 401 Unauthorized when no token is given");
    }

    // ❌ Negative Test: Invalid Token
    @Test
    public void testWithInvalidToken() {
        Response res = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .param("page", 1)
                .param("limit", 10)
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.statusCode(), 401, "Should return 401 for invalid token");
    }

    // ✅ Positive: Check if result is paginated
    @Test
    public void testPagination() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .param("page", 2)
                .param("limit", 5)
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.statusCode(), 200);
        Assert.assertTrue(res.jsonPath().getList("docs").size() <= 5, "Should return max 5 records");
    }

    // ✅ Positive: Check required fields in response
    @Test
    public void testResponseFieldsExist() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .param("page", 1)
                .param("limit", 1)
                .when()
                .get(baseUrl);

        String companyName = res.jsonPath().getString("docs[0].company_name");
        String cin = res.jsonPath().getString("docs[0].cin");

        Assert.assertNotNull(companyName, "Company name should be present");
        Assert.assertNotNull(cin, "CIN should be present");
    }

    // ⚠️ Edge Case: Very high page number (no data)
    @Test
    public void testVeryHighPageNumber() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .param("page", 99999)
                .param("limit", 10)
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.statusCode(), 200);
        Assert.assertEquals(res.jsonPath().getList("docs").size(), 0, "Should return empty list");
    }

    // ⚠️ Edge Case: Negative page number
    @Test
    public void testNegativePageNumber() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .param("page", -1)
                .param("limit", 10)
                .when()
                .get(baseUrl);

        Assert.assertTrue(res.statusCode() == 400 || res.statusCode() == 422, "Should return error for negative page number");
    }

    // ⚠️ Edge Case: Zero limit
    @Test
    public void testZeroLimit() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .param("page", 1)
                .param("limit", 0)
                .when()
                .get(baseUrl);

        Assert.assertTrue(res.statusCode() == 400 || res.statusCode() == 422, "Should return error for limit 0");
    }

    // ❌ Negative: Missing both page and limit
    @Test
    public void testMissingQueryParams() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .when()
                .get(baseUrl);

        Assert.assertTrue(res.statusCode() == 400 || res.statusCode() == 422, "Should return error for missing query params");
    }

    // ✅ Positive: Check if response is JSON
    @Test
    public void testContentTypeJson() {
        Response res = RestAssured.given()
                .header("Authorization", bearerToken)
                .param("page", 1)
                .param("limit", 1)
                .when()
                .get(baseUrl);

        Assert.assertEquals(res.getContentType(), "application/json; charset=utf-8", "Response should be JSON");
    }
}
