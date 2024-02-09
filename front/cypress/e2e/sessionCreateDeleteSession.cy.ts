import '../support/commands.ts'

describe('Admin Session Management', () => {
    it('Admin can create a session, which then appears in the session list', () => {

    // Mock call to receive the list of teachers and feed the listbox
    cy.intercept(
      {
        method: 'GET',
        url: '/api/teacher',
      },
      [
        {
          id: 1,
          lastName: 'THIERCELIN',
          firstName: 'Hélène',
          createdAt: new Date(2020, 1, 1),
          updatedAt: new Date(2021, 1, 1)
        },
        {
          id: 1,
          lastName: 'DELAHAYE',
          firstName: 'Margot',
          createdAt: new Date(2020, 1, 1),
          updatedAt: new Date(2021, 1, 1)
        }
      ])

    // Mock for session creation (CREATE)
    cy.intercept(
      {
        method: 'POST',
        url: '/api/session',
      },
      {
        id: 1,
        name: 'Séance pour les débutants',
        description: 'Séance réservée aux débutants',
        date: new Date(2023, 7, 10),
        teacher_id: 1,
        createdAt: new Date(2023, 9, 25)
      })

    // Log in as administrator
    cy.loginAdmin()

    // The Create button is expected to be visible
    cy.contains('span.ml1', 'Create').should('be.visible')

    // Click on Create
    cy.contains('span.ml1', 'Create').click()

    // Create Session is expected on the
    cy.contains('Create session').should('be.visible')

    // Fill in the form's input fields
    cy.get('input[formControlName=name]').type('Séance pour les débutants')
    cy.get('input[formControlName=date]').type('2023-07-10')
    cy.get('mat-select[formControlName=teacher_id]').click().get('mat-option').contains('Margot DELAHAYE').click()
    cy.get('textarea[formControlName=description]').type('Séance réservée aux débutants')

    // Mock for session recovery
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      {
        body: [
          {
            id: 1,
            name: 'Séance pour les débutants',
            description: 'Séance réservée aux débutants',
            date: new Date(2023, 7, 10),
            teacher_id: 1,
            users: [],
          },
        ],
      })

    // Click on Save
    cy.get('button[type=submit]').click();

    // We expect to come back to the sessions page, get the Session created message and see the session created!
    cy.url().should('include', '/sessions')
    cy.contains('Session created !').should('be.visible')

    // 3s delay to observe the matSnackBar message
    cy.wait(3000)
    cy.contains('Séance pour les débutants').should('be.visible')

  })

  it('should update a session and appears in the list updated', () => {
    // Mock call to receive the list of teachers and feed the listbox
    cy.intercept(
      {
        method: 'GET',
        url: '/api/teacher',
      },
      [
        {
            id: 1,
            lastName: 'THIERCELIN',
            firstName: 'Hélène',
            createdAt: new Date(2020, 1, 1),
            updatedAt: new Date(2021, 1, 1)
        },
        {
            id: 1,
            lastName: 'DELAHAYE',
            firstName: 'Margot',
            createdAt: new Date(2020, 1, 1),
            updatedAt: new Date(2021, 1, 1)
        }
      ])

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

    // Click on Create
    cy.contains('span.ml1', 'Edit').click()

    // Fill in the form's input fields (add 'modified')
    cy.get('input[formControlName=name]').type(' modifiée')
    cy.get('textarea[formControlName=description]').type(' modifiée')


    // Mock for session recovery
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session'
      },
      {
        body: [
          {
            id: 1,
            name: 'Séance pour les débutants modifiée',
            description: 'Séance réservée aux débutants modifiée',
            date: new Date(2023, 7, 10),
            teacher_id: 1,
            users: [],
          },
        ],
      })

    // Mock for session modification (UPDATE)
    cy.intercept(
      {
        method: 'PUT',
        url: '/api/session/1',
      },
      {
        id: 1,
        name: 'Séance pour les débutants modifiée',
        description: 'Séance réservée aux débutants modifiée',
        date: new Date(2023, 7, 10),
        teacher_id: 1,
        updatedAt: new Date(2023, 9, 26)
      })

    cy.get('button[type=submit]').click();

    // We expect to come back to the sessions page, get the Session updated message and see the session modified!
    cy.url().should('include', '/sessions')
    cy.contains('Session updated !').should('be.visible')

    // 3s delay to observe the matSnackBar message
    cy.wait(3000)
    cy.contains('Séance pour les débutants modifiée').should('be.visible')
  })


  it('should see the detail of a session and delete it', () => {
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

    // We mocke the call to retrieve session 1
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session/1'
      },
      {
      body: {
        id: 1,
        name: 'Séance pour les débutants modifiée',
        description: 'Séance réservée aux débutants modifiée',
        date: '2023-10-07T00:00:00.000+00:00',
        createdAt: '2023-09-25T00:00:00.000+00:00',
        updateAd: '2023-09-26T00:00:00.000+00:00',
        teacher_id: 1,
        users: []
      },
    })

    // Click on Detail
    cy.contains('span.ml1', 'Detail').click()

    // We expect to have the session details (we check the description)
    cy.contains('div.description', 'Séance réservée aux débutants modifiée').should('be.visible')

    // We mocke the DELETE call for session 1 (return 200 OK expected)
    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200
    })

    // Click on Delete
    cy.contains('span.ml1', 'Delete').click()

    // We expect to come back to the list and get the message 'Session deleted!
    cy.url().should('include', '/sessions')
    cy.contains('Session deleted !').should('be.visible')

    // 3s delay to observe the matSnackBar message
    cy.wait(3000)
  })
});