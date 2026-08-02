# File Inspector

File Inspector examines a file through one streaming read. It calculates CRC32, MD5, SHA-1, SHA-256, and SHA-512 checksums, identifies common file signatures, and reports basic metadata.

MD5 and SHA-1 are marked as legacy algorithms. Paste an expected checksum to verify it. You can also copy an individual checksum, or copy and share the full inspection report.

The plugin requires AutoJs6 build 5268+. It is implemented entirely on the JVM and is independent of device ABI.

Safety and privacy limits:

- Files larger than 8 TiB are rejected.
- File content is opened once and processed sequentially in read-only mode.
- Only the first 64 bytes are inspected as the file header.
- The plugin has no network access and requests no storage permission.
