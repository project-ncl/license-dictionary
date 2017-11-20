# Developer's guide

To build the project one needs Node, NPM and NG CLI.

Please follow https://github.com/angular/angular-cli#installation

By default app is exposed at port 8181.

## UI development
To efficiently work on UI, start the server (by running the `*-swarm.jar`),
then go to `impl/src/main/ui` and run:
```
ng serve --proxy-config proxy.conf.json
``` 