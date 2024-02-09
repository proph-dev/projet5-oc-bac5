describe('Not Found Spec', () => {
    it('should redirect to the not found page', () => {
      //Redirection vers une page qui n'existe pas
        cy.visit('/notexist');
  
      //On s'vérifie qu'on soit bien redirigé vers /404 et que le message "Page not found" présent dans un élément H1
      cy.url().should('include', '/404')
      cy.contains('h1','Page not found').should('be.visible');

    });
})