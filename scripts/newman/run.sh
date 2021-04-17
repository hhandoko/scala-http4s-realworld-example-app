#!/usr/bin/env sh

APIHOST=${APIHOST:-http://localhost}
APIPORT=${APIPORT:-8080}
USERNAME=${USERNAME:-john.doe}
EMAIL=${EMAIL:-$USERNAME@test.com}
PASSWORD=${PASSWORD:-S3cret!}

npx newman run scripts/newman/Conduit.postman_collection.json \
  --delay-request 100 \
  --global-var "APIURL=$APIHOST:$APIPORT/api" \
  --global-var "USERNAME=$USERNAME" \
  --global-var "EMAIL=$EMAIL" \
  --global-var "PASSWORD=$PASSWORD"
