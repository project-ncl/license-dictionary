#!/usr/bin/env bash

java  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5015 -jar impl/target/license-dictionary-1.0-SNAPSHOT-swarm.jar 