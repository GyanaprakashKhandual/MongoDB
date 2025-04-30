import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100,              // Number of virtual users
  duration: '30s',       // Test duration
  thresholds: {
    http_req_duration: ['p(95)<2000'],  // 95% of requests must complete below 2s
  },
};

const BASE_URL = 'https://5chfmeq9qw.us-east-1.awsapprunner.com/v1/users';
const TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2N2UxMjg5MzE2NGZkZWYzZDVkMDQzOTciLCJpYXQiOjE3NDI4ODcyNjUsInR5cGUiOiJhY2Nlc3MifQ.U1FQhulfkIfHAgvzBoihYR12xmDal8iBScwns7DNMQU';

export default function () {
  const headers = {
    headers: {
      Authorization: `Bearer ${TOKEN}`,
    },
  };

  const res = http.get(BASE_URL, headers);

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 2000ms': (r) => r.timings.duration < 2000,
  });

  sleep(1); // Sleep 1s to simulate real user pacing
}
