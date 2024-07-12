#!/bin/bash
set -e

cd "$WORKING_DIR"/functions || {
    echo "Error moving to the 'functions' directory."
    exit 1
}
echo ""
echo "AWS INFORMATION:"
echo ""
echo "- Workloads Profile: $AWS_WORKLOADS_PROFILE"
echo "- Workloads Region : $AWS_WORKLOADS_REGION"

echo ""
echo "DELETING MANAGED STACKS FROM <WORKLOADS> ACCOUNT..."
sam delete --stack-name "city-data-function-dev" \
  --profile "$AWS_WORKLOADS_PROFILE"
echo ""
echo "DONE!"

echo ""
echo "DELETING PENDING BUCKETS FROM S3..."
aws s3api list-buckets --query "Buckets[?contains(Name, 'aws-sam-cli-managed')].[Name]" \
    --output text \
    --profile "$AWS_WORKLOADS_PROFILE" | while read -r bucket_name; do
        "$WORKING_DIR"/utils/scripts/common/delete-s3-bucket.sh "$bucket_name" "$AWS_WORKLOADS_PROFILE"
done

### REMOVE SAM CONFIGURATION FILES
rm -rf "$WORKING_DIR"/.aws-sam
