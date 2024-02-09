import '../support/commands.ts'

describe('Session Participation Workflow for Users', () => {
    it('User successfully participates and then opts out of a session', () => {

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


        // We expect the participate button to be proposed
        cy.contains('span.ml1', 'Participate').should('be.visible')

        // We mock the call to participate
        cy.intercept('POST', '/api/session/1/participate/1', {
            statusCode: 200
        })

        // Mock to receive session 1 retrieved with id 1 of the participating user
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
                    users: [
                        1
                    ]
                },
            })

        // Click on Participate
        cy.contains('span.ml1', 'Participate').click()

        // We expect the Do not participate button to be offered
        cy.contains('span.ml1', 'Do not participate').should('be.visible')

        // We mock the call to participate
        cy.intercept('DELETE', '/api/session/1/participate/1', {
            statusCode: 200
        })

        // Mock to receive session 1 retrieved with id 1 of the participating user
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

        // Click on Participate
        cy.contains('span.ml1', 'Do not participate').click()

        // We expect the Do not participate button to be offered
        cy.contains('span.ml1', 'Participate').should('be.visible')
    })
});