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
        cy.contains('Delete my account:').should('be.visible');
        cy.contains('Create at: October 21, 2023').should('be.visible');
        cy.contains('Last update: October 25, 2023').should('be.visible');
    })

    it('should delete my account', () => {

        // We mocke the DELETE call from user 1 (return 200 OK expected)
        cy.intercept('DELETE', 'api/user/1', {
          statusCode: 200,
        })
    
        // Click on Delete
        cy.contains('span.ml1', 'Detail').click()
    
        // The message 'Session deleted!
        cy.contains('Your account has been deleted !').should('be.visible')
        cy.wait(3000)
    })
});