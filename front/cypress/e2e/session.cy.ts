import '../support/commands.ts'

describe('Viewing a session', () => {
    it('User successfully opens the details of a session', () => {

        // Login as a user
        cy.loginUserWithSession()

        // We expect to have a list of sessions
        cy.contains('Séance pour les débutants').should('be.visible')

        // Mock to receive the recovered session 1
        cy.intercept(
            {
                method: 'GET',
                url: '/api/session/1'
            },
            {
                body: {
                    id: 1,
                    name: 'Séance pour les débutants',
                    description: 'Séance réservée aux débutants',
                    date: '2023-10-07T00:00:00.000+00:00',
                    createdAt: '2023-09-25T00:00:00.000+00:00',
                    teacher_id: 1,
                    users: []
                },
            })

        // We mocke the call to teacher id1
        cy.intercept(
            {
                method: 'GET',
                url: '/api/teacher/1',
            },
            [
                {
                    id: 1,
                    lastName: 'DELAHAYE',
                    firstName: 'Margot',
                    createdAt: new Date(2020, 1, 1),
                    updatedAt: new Date(2021, 1, 1)
                }
            ])

        // Click on Detail
        cy.contains('span.ml1', 'Detail').click()


        // We expect the title to be offered
        cy.contains('h1','Séance Pour Les Débutants').should('be.visible');
        // We expect the participate button to be proposed
        cy.contains('span.ml1', 'Participate').should('be.visible')
        // We expect the description to be proposed
        cy.contains('div.description', 'Séance réservée aux débutants').should('be.visible')
        // The date of the session should be proposed
        cy.contains('span.ml1', 'October 7, 2023').should('be.visible')
        // La date de création devrait être proposée
        cy.contains('div.created', 'Create at: September 25, 2023').should('be.visible')
    })
});