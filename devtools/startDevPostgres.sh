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


docker network create --subnet=172.171.17.0/24 postgres-net

docker kill dictionary-postgres
docker rm dictionary-postgres

container=$(docker run --net postgres-net --name dictionary-postgres --ip 172.171.17.117 \
 -e POSTGRES_PASSWORD=123 -e POSTGRES_USER=license-dictionary -e POSTGRES_DB=license-dictionary \
  -d postgres:9.5-alpine)
