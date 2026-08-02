******

### Release history

******

# v1.0.0

###### 2026/08/02

* `Feature` File Inspector plugin with plugin ID `file-inspector`, action ID `inspect-file`, engine `explorer-action`, and variant `default`
* `Feature` Read-only single-file Explorer action for any readable regular file, with an 8 TiB input limit and no storage or network permission
* `Feature` One-pass CRC32, MD5, SHA-1, SHA-256, and SHA-512 calculation with progress and cancellation
* `Feature` Strict expected digest normalization and verification with algorithm inference, explicit prefixes, and constant-time comparison of equal-length bytes
* `Feature` A 64-byte hexadecimal and ASCII header snapshot, BOM detection, and recognition of ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, and SQLite 3 signatures
* `Feature` Pure JVM implementation with no native library, unrestricted ABIs declared by `supportedAbis = emptyArray()`, one ABI-independent APK, and required AutoJs6 host build 5268
* `Feature` Localized metadata, interface text, usage instructions, README files, and changelogs in Spanish, French, Russian, Arabic, Japanese, Korean, English, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese
* `Dependency` Added AndroidX Lifecycle ViewModel version 2.9.4
