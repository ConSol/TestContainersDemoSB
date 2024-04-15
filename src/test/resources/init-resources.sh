#!/bin/bash
awslocal dynamodb create-table \
  --region eu-central-1 \
  --table-name test \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
