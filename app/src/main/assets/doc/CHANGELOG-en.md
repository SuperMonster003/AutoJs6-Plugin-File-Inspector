# Release history

## v1.0.1

_2026/08/08_

- `Fix` Fixed the host failing to bind the plugin service after enabling it in the plugin center; the "Inspect file" action is now available immediately after enabling
- `Improvement` Streamlined the plugin name and description so the user-facing documentation reads more naturally

## v1.0.0

_2026/08/02_

- `Hint` First public release; requires AutoJs6 version code 5268 or later
- `Feature` Added the read-only "Inspect file" action to the AutoJs6 file manager's file menu, working on regular files of any type (plugin ID `file-inspector`, action ID `inspect-file`)
- `Feature` One sequential read computes the CRC32, MD5, SHA-1, SHA-256, SHA-512 checksums together, with live progress plus cancel and retry
- `Feature` Paste an expected checksum for integrity verification: the algorithm is detected by length or by prefixes such as `sha256:`, fingerprint notation like `AB:CD:EF` and the CRC32 `0x` prefix are accepted, and comparison is constant-time over equal-length values
- `Feature` The report shows the file name, MIME type, extension, declared and actual sizes, a hex + ASCII snapshot of the first 64 bytes, and UTF BOM detection
- `Feature` Recognizes 10 common formats from leading signature bytes: ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
- `Feature` Each checksum can be copied individually, the full report can be copied or shared through the system share sheet, and MD5 and SHA-1 carry a Legacy badge
- `Feature` The plugin requests no storage or network permission and reads files only through the temporary read-only content URI granted by the host, up to 8 TiB per file
- `Feature` Ships UI text, instructions, README, and CHANGELOG in 10 languages: Simplified Chinese, Traditional Chinese (Hong Kong), Traditional Chinese (Taiwan), English, French, Spanish, Japanese, Korean, Russian, and Arabic
- `Dependency` Introduced AndroidX Lifecycle ViewModel 2.9.4
