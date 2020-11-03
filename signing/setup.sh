#!/bin/bash

# Decrypt Google Services key
gpg --quiet --batch --yes --decrypt --passphrase=$GOOGLE_SERVICES_KEY --output app/google-services.json signing/google-services.json.gpg