import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 10,          // Number of virtual users
    duration: '30s',  // How long the test runs
};

export default function () {
    const url = 'http://localhost:8080/deals'; // Change port if needed
    const payload = JSON.stringify({
        dealId: `D-${Math.floor(Math.random() * 1000)}`,
        fromCurrency: 'USD',
        toCurrency: 'EUR',
        amount: 100
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    // Check response
    check(res, {
        'status is 201 or 409': (r) => r.status === 201 || r.status === 409,
        'response body contains Deal': (r) => r.body.includes('Deal')
    });

    sleep(1); // Wait 1 second between iterations
}
