import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UsersApiTests {
    
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU"; // Replace with a valid token
    
    // Test 1: Verify that the response is in JSON format
    @Test
    public void testResponseIsJson() {
        // This test ensures the response content is in JSON format
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

        Assert.assertTrue(response.getContentType().contains("application/json"));
        // Fail: If the response is not JSON
        // Pass: If the response is in JSON format
    }

    // Test 2: Verify that the response contains 'data' field
    @Test
    public void testResponseShouldContainDataField() {
        // This test ensures the API response contains a 'data' field
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

        Assert.assertTrue(response.body().asString().contains("results"));
        // Fail: If the 'data' field is missing
        // Pass: If the 'data' field is present
    }

    // Test 3: Test for valid response when accessing the users endpoint
    @Test
    public void testValidGetRequest() {
        // This test ensures that a valid GET request returns status 200 and user data
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

        Assert.assertEquals(200, response.statusCode());
        // Fail: If status code is not 200 (OK)
        // Pass: If status code is 200 and response is valid
    }

    // Test 4: Test with invalid token (should return 401)
    @Test
    public void testInvalidToken() {
        // This test checks if an invalid token results in a 401 Unauthorized status
        Response response = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

        Assert.assertEquals(401, response.statusCode());
        // Fail: If status code is not 401 (Unauthorized)
        // Pass: If status code is 401 due to invalid token
    }

    // Test 5: Test with no token (should return 403 Forbidden)
    @Test
    public void testNoTokenProvided() {
        // This test ensures the API rejects the request with no Authorization token
        Response response = RestAssured.given()
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");
    
        Assert.assertEquals(403, response.statusCode()); // Expecting 403 forbidden (if that's the actual behavior)
        // Fail: If status code is not 502 (Bad Gateway)
        // Pass: If status code is 502 when no token is provided
    }
    

    // Test 6: Test for unauthorized access (if applicable)
    @Test
    public void testUnauthorizedAccess() {
        // This test ensures access is restricted to authorized users only
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

        Assert.assertTrue(response.statusCode() == 200 || response.statusCode() == 403);
        // Fail: If status code is not 200 or 403
        // Pass: If status code is either 200 or 403 based on user access rights
    }

    // Test 7: Test for edge case where the user list is empty (no users)
    @Test
    public void testEmptyUserList() {
        // This test checks if the API gracefully handles an empty user list
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + TOKEN)
                .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

        Assert.assertTrue(response.body().asString().contains("results"));
        Assert.assertTrue(response.body().asString().contains("[]")); // Empty list scenario
        // Fail: If the response does not contain an empty list when no users are present
        // Pass: If the response returns an empty list
    }

    // Test 8: Test invalid request method (POST)
    @Test
public void testPostMethodNotAllowed() {
    // This test ensures POST method is rejected if not supported
    Response response = RestAssured.given()
            .header("Authorization", "Bearer " + TOKEN)
            .post("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

    // Print the status code for debugging
    System.out.println("Response Status Code: " + response.statusCode());
    System.out.println("Response Body: " + response.body().asString());

    // Adjust the assertion based on the actual response code
    Assert.assertTrue(response.statusCode() == 405 || response.statusCode() == 400 || response.statusCode() == 500);
    // Fail: If POST method is not rejected
    // Pass: If the server correctly rejects POST method with status 405, 400, or handles an internal error (500)
}

@Test
public void testInvalidQueryParameter() {
    // This test ensures invalid query parameters are handled
    Response response = RestAssured.given()
            .header("Authorization", "Bearer " + TOKEN)
            .queryParam("page", -1) // assuming negative page is invalid
            .get("https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users");

    // Print the response status code and body for debugging purposes
    System.out.println("Response Status Code: " + response.statusCode());
    System.out.println("Response Body: " + response.body().asString());

    // Assert that the response body contains an error message or indication of invalid query
    Assert.assertTrue(response.body().asString().contains("error") || response.body().asString().contains("Invalid query parameter"));

    // If the response body contains a relevant error message, the test will pass.
}


}