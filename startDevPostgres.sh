#!/usr/bin/env bash

docker network create --subnet=172.171.17.0/24 postgres-net

docker kill dictionary-postgres
docker rm dictionary-postgres

container=$(docker run --net postgres-net --name dictionary-postgres --ip 172.171.17.117 \
 -e POSTGRES_PASSWORD=123 -e POSTGRES_USER=license-dictionary -e POSTGRES_DB=license-dictionary \
  -d postgres:9.5-alpine)
