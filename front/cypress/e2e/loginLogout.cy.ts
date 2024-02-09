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