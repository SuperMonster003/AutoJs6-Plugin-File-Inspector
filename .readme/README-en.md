<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="File Inspector" width="128" />
  </p>

  <h1>File Inspector</h1>

  <p>AutoJs6 file manager plugin: seven checksums from a single read, paste the published value to verify integrity instantly, and see the file's true format at a glance</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml/badge.svg"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

### Languages

This README is available in the following languages:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- English [en] # current
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

### Introduction

File Inspector is a companion plugin for the AutoJs6 file manager. Choose "Inspect file" on any file in the file manager and the plugin reads it just once, computing the CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384, and SHA-512 checksums all at the same time, while also showing the file's true format signature, size, and leading bytes on a single screen. Everything is read-only: the plugin requests no storage or network permission and never modifies the original file.

The report page includes an "Integrity check" box: paste the checksum published by the file's provider, and the plugin detects the algorithm automatically and answers with a clear match / mismatch verdict, so you never have to compare long hex strings by eye. Each checksum and the header snapshot can be copied individually; choose Markdown or JSON before copying the whole report or sending it through the system share sheet.

### Highlights

- One read, all results: the file is read sequentially exactly once while all seven checksums are computed together, so large files never wait through multiple passes.
- Paste to verify: accepts hex and fingerprint notation, Base64, SRI, and complete `md5sum` / `sha256sum` output lines; it detects the algorithm automatically and warns when a pasted file name differs.
- True format detection: recognizes 26 common signatures from bounded leading and fixed-offset bytes, adds a purpose hint for ZIP-based APK/JAR/Office documents, and estimates text versus binary content from printable ratio and entropy.
- Header at a glance: shows the file's first 64 bytes in a hex + ASCII side-by-side view and detects UTF-8 / UTF-16 / UTF-32 byte order marks.
- Double size check: shows both the size declared by the file manager and the size actually read; if the file changes during reading, the inspection fails immediately, catching incomplete downloads and files still being written.
- Progress under control: large files show live bytes, read speed, and estimated time remaining; reading can be canceled at any time, and a failed inspection can be retried with one tap.
- Results ready to use: copy any checksum or the header snapshot independently, then export the complete on-screen result as structured Markdown or JSON to the clipboard or system share sheet without creating a file.
- Legacy algorithms flagged: MD5 and SHA-1 carry a Legacy badge as a reminder that they are no longer suitable as proof of security.

### How to use

1. Download the APK and its same-named `.apk.sha256` sidecar from the same official Release, install the APK, then enable it in the AutoJs6 plugin center (AutoJs6 version code 5268 or later is required).
2. Before upgrading, inspect the downloaded APK with the already installed File Inspector; for a first installation, retain the APK and inspect it after enabling the plugin. Paste the 64-character SHA-256 from the sidecar and require a green match; obtain both files from the same trusted HTTPS Release page.
3. Open the AutoJs6 file manager and locate the file you want to inspect; any kind of regular file works.
4. Choose "Inspect file" from the file's menu; the plugin starts reading right away with live progress, and the checksums, format signature, and header bytes appear when it finishes.
5. To verify integrity, paste the published checksum into the "Integrity check" box and tap "Verify": green means match, red means mismatch.
6. Tap the copy button next to any checksum or below the header snapshot for an individual value; choose Markdown or JSON before copying or sharing the complete report, then press back to return to the file manager.

> The checksum input accepts plain hex, prefixes such as `sha256: <value>` or `MD5=<value>`, `AB:CD:EF` fingerprints, CRC32 with `0x`, standard Base64, SRI such as `sha256-<base64>`, and complete `<hex>  <file>` or `<hex> *<file>` lines from coreutils. Case and surrounding whitespace are ignored; the algorithm is inferred when its digest length is unique, and a differing pasted file name produces a warning.

### Recognized format signatures

Checksums work on any readable regular file; on top of that, the current version recognizes the following bounded leading or fixed-offset format signatures:

```text
ZIP, 7z, RAR 4, RAR 5, GZIP, XZ, BZIP2, Zstandard, LZ4, TAR, PDF, PNG, JPEG, GIF87a, GIF89a, WebP, MP4 / ISO-BMFF, EBML / Matroska, ELF, DEX, Java Class, Mach-O, PE, SQLite 3, WOFF, WOFF2
```

Signature detection uses a bounded sample and structural fields such as TAR magic at offset 257, ISO-BMFF `ftyp` at offset 4, and the PE-header pointer. It is a fast hint, not full format validation; printable-ratio and entropy results are heuristic as well. Files with no matching signature show "Unknown", and their checksums are still computed normally.

### FAQ

#### When is this plugin useful?

The classic scenario is verifying downloads: after fetching an installer, firmware image, or document, paste the SHA-256 (or similar) checksum published by the provider and know immediately whether the file is complete and untampered. It is also handy for revealing the true format of files with misleading extensions, or for quickly producing checksums of any file for archiving and comparison.

#### Does a checksum "match" mean the file is safe?

A match only proves the file is byte-for-byte identical to the one the checksum was published for; whether that is trustworthy depends on where the checksum came from. Prefer SHA-256 or SHA-512 values published over HTTPS by the official source. MD5 and SHA-1 collisions can be crafted deliberately, which is why the plugin flags them as Legacy: do not treat them as proof of security.

#### Why can't some files be inspected?

Common reasons: the file exceeds the 8 TiB limit; the file was modified by another app while being read, so the actual size no longer matches the declared size; or the read-only grant issued by the host has expired. The error message states the specific reason, and "Retry" runs the inspection again.

#### Are large files slow to inspect?

The plugin reads the file sequentially once and computes all seven checksums during that single pass, so the time is bounded by storage read speed rather than by the number of algorithms. Live progress is shown throughout, and the inspection can be canceled at any time.

### Permissions and safety

The plugin requests no storage or network permission and can only reach the single file the user selected, through a temporary read-only content URI granted by the host; the grant expires when the inspection ends, and no other file is reachable. Requests from the file manager are validated field by field, including action ID, protocol version, content URI shape, file name, MIME type, declared size, and read-only grants, and any request carrying write or persistable grants is rejected outright. The file is processed as a read-only stream; memory holds only bounded buffers, the first 4096 bytes for analysis, the first 64 bytes for display, and a four-byte PE-signature window, and the source file is never modified.

To keep every inspection predictable, the plugin enforces the following bounds:

- A single file may be at most 8 TiB, and each action handles exactly one target file.
- Analysis samples at most the first 4096 bytes plus a four-byte PE-signature window, while the displayed header remains 64 bytes; checksum input is limited to 512 characters, with non-ASCII accepted only in a coreutils file name.
- If the size actually read differs from the declared size, the file is considered changed and the inspection fails with a message.
- Checksum comparison uses constant-time comparison of equal-length values; MD5 and SHA-1 are for checking legacy data only and must not be treated as collision-resistant proof.

### Plugin interface

The host (AutoJs6) discovers and invokes the plugin through the following identities, provided here for plugin and host developers:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: file-inspector
engine: explorer-action
variant: default
Explorer action id: inspect-file
MIME type: */*
required host build: 5268
```

The current version contributes a single-file read-only overflow menu action to the host file manager; it never modifies the source file and never enumerates directories. If the plugin is missing or disabled, the host silently falls back to its default behavior.

### Roadmap

The capabilities above and the checked Roadmap items reflect what is implemented; future work such as provider-backed digest algorithms, batch verification, and host protocol extensions is tracked in the Roadmap, and unchecked items are not current capabilities.

- [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/ROADMAP.md)

### Release history

#### v1.0.1

_2026/08/08_

- `Fix` Fixed the host failing to bind the plugin service after enabling it in the plugin center; the "Inspect file" action is now available immediately after enabling
- `Improvement` Streamlined the plugin name and description so the user-facing documentation reads more naturally

#### v1.0.0

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

##### Full history

- [CHANGELOG-en.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

### Build

```powershell
.\gradlew.bat :app:assembleDebug
```

Release build:

```powershell
.\gradlew.bat :app:appendDigestToReleasedFiles
.\gradlew.bat :app:verifyReleaseChecksums
```

Build and signing parameters come from version.properties and sign.properties; the current minimum is Android 7.0 (SDK 24) with target SDK 36.

The README, CHANGELOG, and in-plugin instructions under res/raw*/plugin_instruction.md are generated by .python/generate_markdown.py from the JSON language sources and templates under .readme/ and .changelog/ (10 languages). To change the documentation, edit the JSON sources and re-run the script instead of editing generated Markdown.

### Links

- AutoJs6 documentation: https://docs.autojs6.com
- Android secure file sharing: https://developer.android.com/training/secure-file-sharing
