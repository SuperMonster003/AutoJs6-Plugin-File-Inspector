# File Inspector

## Introduction

File Inspector is a companion plugin for the AutoJs6 file manager. Choose "Inspect file" on any file in the file manager and the plugin reads it just once, computing the CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384, and SHA-512 checksums all at the same time, while also showing the file's true format signature, size, and leading bytes on a single screen. Everything is read-only: the plugin requests no storage or network permission and never modifies the original file.

The report page includes an "Integrity check" box: paste the checksum published by the file's provider, and the plugin detects the algorithm automatically and answers with a clear match / mismatch verdict, so you never have to compare long hex strings by eye. Each checksum and the header snapshot can be copied individually; choose Markdown or JSON before copying the whole report or sending it through the system share sheet.

## Highlights

- One read, all results: the file is read sequentially exactly once while all seven checksums are computed together, so large files never wait through multiple passes.
- Paste to verify: accepts hex and fingerprint notation, Base64, SRI, and complete `md5sum` / `sha256sum` output lines; it detects the algorithm automatically and warns when a pasted file name differs.
- True format detection: recognizes 26 common signatures from bounded leading and fixed-offset bytes, adds a purpose hint for ZIP-based APK/JAR/Office documents, and estimates text versus binary content from printable ratio and entropy.
- Header at a glance: shows the file's first 64 bytes in a hex + ASCII side-by-side view and detects UTF-8 / UTF-16 / UTF-32 byte order marks.
- Double size check: shows both the size declared by the file manager and the size actually read; if the file changes during reading, the inspection fails immediately, catching incomplete downloads and files still being written.
- Progress under control: large files show live bytes, read speed, and estimated time remaining; reading can be canceled at any time, and a failed inspection can be retried with one tap.
- Results ready to use: copy any checksum or the header snapshot independently, then export the complete on-screen result as structured Markdown or JSON to the clipboard or system share sheet without creating a file.
- Legacy algorithms flagged: MD5 and SHA-1 carry a Legacy badge as a reminder that they are no longer suitable as proof of security.

## How to use

1. Download the APK and its same-named `.apk.sha256` sidecar from the same official Release, install the APK, then enable it in the AutoJs6 plugin center (AutoJs6 version code 5268 or later is required).
2. Before upgrading, inspect the downloaded APK with the already installed File Inspector; for a first installation, retain the APK and inspect it after enabling the plugin. Paste the 64-character SHA-256 from the sidecar and require a green match; obtain both files from the same trusted HTTPS Release page.
3. Open the AutoJs6 file manager and locate the file you want to inspect; any kind of regular file works.
4. Choose "Inspect file" from the file's menu; the plugin starts reading right away with live progress, and the checksums, format signature, and header bytes appear when it finishes.
5. To verify integrity, paste the published checksum into the "Integrity check" box and tap "Verify": green means match, red means mismatch.
6. Tap the copy button next to any checksum or below the header snapshot for an individual value; choose Markdown or JSON before copying or sharing the complete report, then press back to return to the file manager.

> The checksum input accepts plain hex, prefixes such as `sha256: <value>` or `MD5=<value>`, `AB:CD:EF` fingerprints, CRC32 with `0x`, standard Base64, SRI such as `sha256-<base64>`, and complete `<hex>  <file>` or `<hex> *<file>` lines from coreutils. Case and surrounding whitespace are ignored; the algorithm is inferred when its digest length is unique, and a differing pasted file name produces a warning.

## Permissions and safety

The plugin requests no storage or network permission and can only reach the single file the user selected, through a temporary read-only content URI granted by the host; the grant expires when the inspection ends, and no other file is reachable. Requests from the file manager are validated field by field, including action ID, protocol version, content URI shape, file name, MIME type, declared size, and read-only grants, and any request carrying write or persistable grants is rejected outright. The file is processed as a read-only stream; memory holds only bounded buffers, the first 4096 bytes for analysis, the first 64 bytes for display, and a four-byte PE-signature window, and the source file is never modified.

To keep every inspection predictable, the plugin enforces the following bounds:

- A single file may be at most 8 TiB, and each action handles exactly one target file.
- Analysis samples at most the first 4096 bytes plus a four-byte PE-signature window, while the displayed header remains 64 bytes; checksum input is limited to 512 characters, with non-ASCII accepted only in a coreutils file name.
- If the size actually read differs from the declared size, the file is considered changed and the inspection fails with a message.
- Checksum comparison uses constant-time comparison of equal-length values; MD5 and SHA-1 are for checking legacy data only and must not be treated as collision-resistant proof.
