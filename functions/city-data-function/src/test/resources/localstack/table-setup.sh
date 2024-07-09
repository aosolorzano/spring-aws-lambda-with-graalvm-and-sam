#!/bin/bash

echo ""
echo "CREATING DYNAMODB TABLE..."
awslocal dynamodb create-table                \
  --table-name 'Cities'                       \
  --attribute-definitions                     \
    AttributeName=id,AttributeType=S          \
  --key-schema                                \
    AttributeName=id,KeyType=HASH             \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

echo ""
echo "WRITING ITEMS INTO DYNAMODB..."
awslocal dynamodb batch-write-item  \
  --request-items file:///var/lib/localstack/table-data.json
