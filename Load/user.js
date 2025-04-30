import http from 'k6/http';
import { check, sleep } from 'k6';

// K6 test configuration
export const options = {
  vus: 100,          // 100 virtual users
  duration: '30s',   // Run for 30 seconds
};

const BASE_URL = 'https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users';
const TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU';

export default function () {
  const headers = {
    headers: {
      Authorization: `Bearer ${TOKEN}`,
    },
  };

  // Make GET request
  const res = http.get(BASE_URL, headers);

  // Check response
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response is JSON': (r) => r.headers['Content-Type'] && r.headers['Content-Type'].includes('application/json'),
  });

  sleep(1); // Wait 1 second between requests per user
}
