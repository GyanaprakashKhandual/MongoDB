
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100,              // Number of virtual users
  duration: '30s',       // Test duration
  thresholds: {
    http_req_duration: ['p(95)<2000'],  // 95% of requests should complete under 2 seconds
  },
};

const BASE_URL = 'https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/meeting-revise?page=1&limit=10';
const TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU';

export default function () {
  const url = `${BASE_URL}?page=1&limit=10&sortBy=createdAt:desc`;

  const headers = {
    Authorization: `Bearer ${TOKEN}`,
  };

  // Make the GET request to fetch meetings
  const res = http.get(url, { headers });

  // Check the response status and other conditions
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 2000ms': (r) => r.timings.duration < 2000,
  });

  sleep(1);  // Simulate real user pacing with a sleep of 1 second
}

