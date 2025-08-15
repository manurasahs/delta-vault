#!/usr/bin/env bash
set -euo pipefail

EP=${LOCALSTACK_ENDPOINT:-http://localhost:4566}
REGION=${AWS_DEFAULT_REGION:-us-east-1}

BUCKETS=(  "my-test-bucket")

create_bucket() {
  local bucket="$1"
  echo "[init] Ensuring bucket exists: ${bucket}"

  if aws --endpoint-url="${EP}" s3api head-bucket --bucket "${bucket}" >/dev/null 2>&1; then
    echo " - already exists"
    return 0
  fi

  aws --endpoint-url="${EP}" s3api create-bucket \
    --bucket "${bucket}" \
    --region "${REGION}" \
    >/dev/null || true

  if aws --endpoint-url="${EP}" s3api head-bucket --bucket "${bucket}" >/dev/null 2>&1; then
    echo " - created"
    return 0
  fi

  echo " - ERROR: unable to create ${bucket}" >&2
  return 1
}

for b in "${BUCKETS[@]}"; do
  create_bucket "${b}"
done

aws --endpoint-url="${EP}" s3api list-buckets || true
