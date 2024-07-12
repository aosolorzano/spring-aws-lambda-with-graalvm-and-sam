#!/bin/bash
set -e

cd "$WORKING_DIR" || {
    echo "Error moving to the 'root' directory."
    exit 1
}

### REMOVING SAM LOCAL ARTIFACTS
rm -rf "$WORKING_DIR"/.aws-sam

echo ""
echo "VALIDATING SAM TEMPLATE..."
sam validate --lint
echo "DONE!"

echo ""
echo "BUILDING SAM PROJECT LOCALLY..."
sam build --config-env "$AWS_WORKLOADS_ENV"
echo ""
echo "DONE!"

echo ""
echo "DEPLOYING SAM PROJECT INTO AWS..."
sam deploy                                                  \
  --config-env "$AWS_WORKLOADS_ENV"                         \
  --parameter-overrides SpringProfile="$AWS_WORKLOADS_ENV"  \
  --disable-rollback                                        \
  --profile "$AWS_WORKLOADS_PROFILE"
echo ""
echo "DONE!"

echo ""
echo "LOADING DATA INTO DYNAMODB..."
aws dynamodb batch-write-item \
  --request-items file://"$WORKING_DIR"/functions/city-data-function/src/test/resources/localstack/table-data.json \
  --profile "$AWS_WORKLOADS_PROFILE" > /dev/null