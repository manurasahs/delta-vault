#!/usr/bin/env bash
set -euo pipefail

python3 -m pip install --no-cache-dir awscli
aws --version
awslocal --version