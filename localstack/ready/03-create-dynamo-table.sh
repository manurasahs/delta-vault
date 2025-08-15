#!/usr/bin/env bash
set -euo pipefail

EP=${LOCALSTACK_ENDPOINT:-http://localhost:4566}
REGION=${AWS_DEFAULT_REGION:-us-east-1}
TABLE=FileMetadata

echo "Creating DynamoDB table: $TABLE"

if aws --endpoint-url="$EP" dynamodb describe-table --table-name "$TABLE" >/dev/null 2>&1; then
  echo " - Table already exists, skipping"
else
  aws --endpoint-url="$EP" dynamodb create-table \
    --table-name "$TABLE" \
    --attribute-definitions \
      AttributeName=fileId,AttributeType=S \
      AttributeName=version,AttributeType=N \
    --key-schema \
      AttributeName=fileId,KeyType=HASH \
      AttributeName=version,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --region "$REGION"
  echo " - Created"
fi