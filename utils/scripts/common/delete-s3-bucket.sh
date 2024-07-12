#!/bin/bash
set -e

S3_BUCKET_NAME=$1
AWS_PROFILE=$2

### VALIDATING INPUT PARAMETERS
if [ -z "$S3_BUCKET_NAME" ]; then
    echo "ERROR: No bucket name provided."
    exit 1
fi
if [ -z "$AWS_PROFILE" ]; then
    echo "ERROR: No AWS profile provided."
    exit 1
fi

echo ""
echo "- Bucket: $S3_BUCKET_NAME"

### REMOVE ALL BUCKET VERSION AND DELETE-MARKERS
"$WORKING_DIR"/utils/scripts/common/emptying-s3-bucket.sh "$S3_BUCKET_NAME" "$AWS_PROFILE"

### DELETE S3_BUCKET_NAME
echo ">> Removing..."
dynamodb s3 rb "s3://$S3_BUCKET_NAME"        \
    --force                             \
    --profile "$AWS_PROFILE" >/dev/null
echo "Done!"
echo ""
