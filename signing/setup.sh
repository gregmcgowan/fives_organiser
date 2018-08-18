#!/bin/sh

ENCRYPT_KEY=$1

# Decrypt Google Services key
openssl enc -aes-256-cbc -d -in signing/google-services.aes -out app/google-services.json -k $ENCRYPT_KEY