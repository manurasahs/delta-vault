#!/usr/bin/env bash
set -euo pipefail

BUCKETS=("my-test-bucket")

for b in "${BUCKETS[@]}"; do
  echo "Creating bucket: $b"
  aws --endpoint-url=http://localhost:4566 s3api create-bucket --bucket "$b" --region us-east-1 || true
done
