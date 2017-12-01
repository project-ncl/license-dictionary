# Developer's guide

The app is secured with keycloak and stores the data in the database.
Which DB is used depends on the chosen [project stage](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/configuration/project_stages.html)
(see `src/main/resources/project-stages.yml`).

For local development, use `development` - it will talk 
to the keycloak server created via `devtools/startDevKeycloak.sh`
and to an in-memory h2 database.

By default the app is exposed at port 8181.

## UI development

To work with UI you can use [ng-cli](https://github.com/angular/angular-cli#installation)

To work efficiently, start the server (by running the `*-swarm.jar`),
then go to `impl/src/main/ui` and run the following script:
```
./startUi.sh
``` 