#!/bin/bash

echo ""
echo "CREATING LAMBDA ROLE..."
awslocal iam create-role                \
  --role-name 'lambda-role'             \
  --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'

echo ""
echo "ALLOWING LAMBDA ROLE TO ACCESS DYNAMODB..."
awslocal iam put-role-policy            \
    --role-name 'lambda-role'           \
    --policy-name DynamoDBReadPolicy    \
    --policy-document '{"Version": "2012-10-17", "Statement": [{"Effect": "Allow", "Action": "dynamodb:GetItem", "Resource": "arn:aws:dynamodb:us-east-1:000000000000:table/Cities"}]}'

echo ""
echo "WAITING FOR LAMBDA RESOURCES FROM BUILDER CONTAINER..."
FUNCTION_PATH="/var/tmp/native-assembly.zip"
while [ ! -f "$FUNCTION_PATH" ]; do
  sleep 1
done
echo "DONE!"

echo ""
echo "CREATING LAMBDA FUNCTION..."
awslocal lambda create-function                                                               \
  --function-name 'city-data-function'                                                        \
  --runtime 'provided.al2023'                                                                 \
  --architectures 'arm64'                                                                     \
  --zip-file fileb://"$FUNCTION_PATH"                                                         \
  --handler 'org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest'   \
  --timeout 20                                                                                \
  --memory-size 512                                                                           \
  --role 'arn:aws:iam::000000000000:role/lambda-role'                                         \
  --environment 'Variables={SPRING_CLOUD_AWS_ENDPOINT=http://host.docker.internal:4566}'

echo ""
echo "CREATING FUNCTION LOG-GROUP..."
awslocal logs create-log-group              \
  --log-group-name '/lambda/function'

echo ""
echo "CREATING FUNCTION LOG-STREAM..."
awslocal logs create-log-stream             \
  --log-group-name '/lambda/function'       \
  --log-stream-name 'function-log-stream'

echo ""
echo "DONE!"
