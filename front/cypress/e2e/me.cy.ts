import '../support/commands.ts'

describe('User Account Verification', () => {
    it('logs in and verifies user account details', () => {

    // Mock the user data
    cy.intercept('GET', '/api/user/1', {
        body: {
            email: 'toto@gmail.com',
            firstName: 'toto',
            lastName: 'titi',
            admin: false,
            createdAt: new Date(2023, 9, 21),
            updatedAt: new Date(2023, 9, 25)
        }
    })

    // Log in as a user
    cy.loginUser()

    cy.url().should('include', '/sessions')

    // Expect the Account button to be visible
    cy.contains('span.link', 'Account').should('be.visible')

    // Click on Account
    cy.contains('span.link', 'Account').click()

    // Verify mocked information is displayed correctly
    cy.contains('Name: toto TITI').should('be.visible')
    cy.contains('Email: toto@gmail.com').should('be.visible');
    cy.contains('Create at: October 21, 2023').should('be.visible');
    cy.contains('Last update: October 25, 2023').should('be.visible');

  })
});