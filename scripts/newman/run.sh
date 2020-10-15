#!/usr/bin/env sh

APIURL=${APIURL:-http://localhost:8080/api}
USERNAME=${USERNAME:-john.doe}
EMAIL=${EMAIL:-$USERNAME@test.com}
PASSWORD=${PASSWORD:-S3cret!}

npx newman run scripts/newman/Conduit.postman_collection.json \
  --delay-request 100 \
  --global-var "APIURL=$APIURL" \
  --global-var "USERNAME=$USERNAME" \
  --global-var "EMAIL=$EMAIL" \
  --global-var "PASSWORD=$PASSWORD"
