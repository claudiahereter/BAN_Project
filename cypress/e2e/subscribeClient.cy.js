describe('Subscribe Client API Tests', () => {
  const endpoint = '/at/client/subscribe';

  // TC2 – Invalid phone number
  it('TC2 - Rejects phone numbers that don’t have 7 to 15 digits', () => {
    cy.request({
      method: 'POST',
      url: endpoint,
      body: {
        phone: '123',
        telegramToken: 'validToken',
        stationsIds: [101]
      },
      failOnStatusCode: false
    }).then((response) => {
      cy.log(JSON.stringify(response.body)); // log for debugging
      expect(response.status).to.eq(400);
      expect(response.body).to.have.property('error', 'Please enter a valid phone number (7-15 digits)');
    });
  });

  // TC3 – Invalid station IDs
  it('TC3 - Rejects non-existing or malformed station IDs', () => {
    cy.request({
      method: 'POST',
      url: endpoint,
      body: {
        phone: '612345678',
        telegramToken: 'validToken',
        stationsIds: ['abc']
      },
      failOnStatusCode: false
    }).then((response) => {
      cy.log(JSON.stringify(response.body));
      expect(response.status).to.eq(400);
      expect(response.body.error).to.include('Invalid');
    });
  });

  // TC4 – Invalid or expired Telegram token
  it('TC4 - Rejects requests with an invalid or expired Telegram token', () => {
    cy.request({
      method: 'POST',
      url: endpoint,
      body: {
        phone: '612345678',
        telegramToken: 'badToken',
        stationsIds: [101]
      },
      failOnStatusCode: false
    }).then((response) => {
      cy.log(JSON.stringify(response.body));
      expect(response.status).to.eq(401);
      expect(response.body).to.have.property('error', 'Please enter a valid telegram token');
    });
  });
});
