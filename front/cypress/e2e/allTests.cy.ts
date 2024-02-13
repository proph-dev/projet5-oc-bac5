import '../support/commands.ts';

describe('Login Logout spec', () => {

  it('ne devrait pas se connecter avec succès (e-mail non trouvé dans la base de données)', () => {
    cy.visit('/login');
    // Mock de l'appel à login avec retour 401
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        error: 'Mauvaises informations d\'identification',
      },
    });

    cy.get('input[formControlName=email]').type("notfound@studio.com");
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.contains('An error occurred').should('be.visible');
  });

  it('devrait se connecter avec succès', () => {
    cy.loginAdmin();
  });

  it('devrait se déconnecter avec succès', () => {
    cy.contains('Logout').click();
    cy.url().should('include', '');
  });
});

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

describe('Not Found Spec', () => {
    it('should redirect to the not found page', () => {
      //Redirection vers une page qui n'existe pas
        cy.visit('/notexist');
  
      //On s'vérifie qu'on soit bien redirigé vers /404 et que le message "Page not found" présent dans un élément H1
      cy.url().should('include', '/404')
      cy.contains('h1','Page not found').should('be.visible');

    });
})

describe('User Signup Process', () => {

  it('loads the signup form correctly', () => {
    cy.visit('/register');
    cy.get('app-register').should('be.visible');
    cy.get('app-register form').should('be.visible');
    cy.get('app-register form input[formControlName=firstName]').should('be.visible');
    cy.get('app-register form input[formControlName=lastName]').should('be.visible');
    cy.get('app-register form input[formControlName=email]').should('be.visible');
    cy.get('app-register form input[formControlName=password]').should('be.visible');
    cy.get('app-register form button[type="submit"]').should('be.visible');
  })

  it('completes the signup and redirects to login', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      body: {
      },
    })

    cy.get('input[formControlName=firstName]').type("toto")
    cy.get('input[formControlName=lastName]').type("titi")
    cy.get('input[formControlName=email]').type("toto@gmail.com")
    cy.get('input[formControlName=password]').type(`${"test123!"}{enter}{enter}`)
    

    cy.url().should('include', '/login')

    cy.loginUser()
  })
});

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