#!/bin/bash

pushd src/main/ui
if [ ! -d node_modules ]
then
    npm install
fi
ng serve --proxy-config proxy.conf.json 
