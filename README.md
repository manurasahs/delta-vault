# delta-vault

describe local start

future improvements:
- what exactly changed between two versions
- large JSON — multipart upload. and streaming
- encrypt data
- Integration LocalStack/Testcontainers (S3 + Dynamo).
- Bench: full-only vs full+delta
- think about other file types
- optimistic locking on dynamo table
- cleanup objects in S3 without metadata in Dynamo
- make Saga async
- Exception handling
- find in file history the same version and reuse it (?)