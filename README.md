
# Application permettant l'inscription à des sessions de Yoga

Cette application web permet à des utilisateurs de s'incrire à des sessions de Yoga de manière simple et rapide.



## Pré-requis

Il est nécessaire d'avoir sur le poste :
- Java 8 et Maven
- Angular (14.1.0 ou +)
- MySQL


## Clonez le projet

Placez-vous dans le terminal de votre IDE dans le répertoire de votre projet puis saisissez la commande suivante :
```bash
  git clone https://github.com/proph-dev/projet5-oc-bac5
```


## Installez la base de données

Installez WampServer (https://www.wampserver.com/) puis lancez l'application. Ensuite, tapez `localhost/phpmyadmin` sur votre navigateur et connectez-vous avec `root` en nom d'utilisateur et aucun mot de passe. Suivez maintenant les étapes ci-dessous :

- Cliquez sur `Nouvelle base de données`
- Mettez un nom : `yoga_app`
- Choisissez l'encodage `utf8_general_ci`
- Cliquez sur `Créer`

Vous pouvez désormais simplement aller sur la base de données, cliquez sur `Importer` puis importez le fichier `schema.sql` ainsi que `data.sql` juste après qui se trouvent dans le répertoire "resources" dans le main/java.


## Installez l'application

**Front :**
- Dans votre terminal, depuis la racine du projet : `cd front`
- Puis `npm install` pour installer les dépendances du projet

**Back :**
- Dans votre terminal, depuis la racine du projet : `cd back`
- Puis `mvn clean compile` afin de s'assurer que la compilation fonctionne


## Lancez l'application

**Front :**
- Dans votre terminal, depuis le répertoire front : `npm start`
- L'application est disponible à l'url http://localhost:4200/

**Back :**
- Dans votre terminal, depuis le répertoire back, accédez à ce fichier : `src/main/java/com/openclassrooms/starterjwt/SpringBootSecurityJwtApplication.java` puis cliquez sur "Run" présent avant la ligne 10
- L'application est disponible à l'url http://localhost:8080/ (en tant qu'API)


## Lancez les tests

**Front :**
- Dans votre terminal, depuis le répertoire front : `npm run test`

**End to end :**
- Dans votre terminal, depuis le répertoire front : `npm run e2e`

**Back :**
- Dans votre terminal, depuis le répertoire back : `mvn clean test`


## Générer les rapports de couverture

**Front :**
- Dans votre terminal, depuis le répertoire front : `npm run test:coverage`

**End to end :**
- Dans votre terminal, depuis le répertoire front : `npm run e2e:coverage`

**Back :**
- Suite au lancement des tests, le rapport de couverture se trouve dans `back/target/site/index.html`


## Technologies

- Angular
- Spring Boot
- Java 8
- Maven
- MySql
- Git


## Auteur

[@proph-dev](https://github.com/proph-dev)

