#!/usr/bin/env bash
#
# JBoss, Home of Professional Open Source.
# Copyright 2017 Red Hat, Inc., and individual contributors
# as indicated by the @author tags.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


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