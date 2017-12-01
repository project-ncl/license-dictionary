#!/usr/bin/env bash

# enter devtools directory:
cd $(dirname "${0}")

if [ ! -f keycloak.jar ]
then
    echo "downloading keycloak server..."
    curl -s 'http://repo2.maven.org/maven2/org/wildfly/swarm/servers/keycloak/2017.11.0/keycloak-2017.11.0-swarm.jar' -o keycloak.jar
fi

java  -Djava.net.preferIPv4Stack=true -Dswarm.port.offset=100 \
    -Dkeycloak.migration.action=import \
    -Dkeycloak.migration.provider=singleFile \
    -Dkeycloak.migration.file=keycloak-data.json \
    -Dkeycloak.migration.strategy=OVERWRITE_EXISTING \
    -jar keycloak.jar