File Inspector examines any file in the AutoJs6 file manager. It reads the file only once to compute seven checksums such as CRC32, MD5, SHA-1 and SHA-256, and shows the format, size and header of the file. Paste a checksum published by the provider to check whether the file is intact. It only reads the file and never changes the original.

### Getting started

1. Make sure this plugin is enabled in AutoJs6 "Plugin Center".
2. Open the AutoJs6 file manager and locate the file you want to check.
3. Choose "Inspect file" from the file menu (in AutoJs6 6.8.1 and later it is under the top-level menu "File Inspector").
4. When reading finishes, look at "File information", "Checksums" and "File header" in the report.

### Main features

- **Checksums**: computes CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384 and SHA-512 in a single pass; MD5 and SHA-1 are marked "Legacy" and are not suitable as proof of security.
- **Integrity check**: paste the published checksum into "Expected checksum" and tap "Verify"; green means a match and red means a mismatch. A full line of `sha256sum` output is accepted too.
- **File information**: shows the detected format, extension and MIME type, and estimates whether the content is text or binary; for ZIP files it also hints whether the file is an APK, a JAR or an Office document.
- **File header**: shows the first 64 bytes of the file side by side in hexadecimal and ASCII, and detects UTF-8, UTF-16 and UTF-32 byte order marks (BOM).
- **Size check**: shows both the declared size and the actual size; if the file changes while it is being read, an error is shown, which helps you spot incomplete downloads or files that are still being written.
- **Copy and share**: copy each checksum or the file header on its own; after choosing Markdown or JSON in "Report format", use "Copy report" or "Share" to send the full report without creating a file.
- **Reading progress**: large files show progress, speed and remaining time, and you can "Cancel" at any time; if reading fails, tap "Retry".

### Requirements

- Android 7.0 or later.
- AutoJs6 6.8.0 or later.
- No storage or network permission is needed; the plugin only reads the file you selected in the file manager.

### FAQ

**What to do when the checksum does not match**

First make sure the pasted checksum comes from the release page of this very file. If it still does not match, the file differs from the original published by the provider: the download may be incomplete or the file may have been changed. Download it again from the official source and check it again.

**What a matching checksum means**

A match only shows that the file has the same content as the file the checksum was published for; whether the file can be trusted depends on where the checksum came from. Prefer SHA-256 or SHA-512 values published over HTTPS by the official source.

**Why a file name warning appears when you paste a full line**

When you paste a full line from a tool such as `sha256sum`, the plugin compares the file name in that line. If it differs from the current file name, a warning reminds you to confirm that the checksum belongs to this file.

**Why some files cannot be inspected**

Common causes are a file larger than 8 TiB, a file changed by another app while it is being read, or a read permission from the file manager that has expired. The error message names the cause; tap "Retry" to check again.

See the [project homepage](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector) for more details.
