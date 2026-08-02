<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>Read-only file signatures, header details, and checksum verification for AutoJs6 Explorer</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The current README.md supports the following languages:

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

******

### Introduction

******

The AutoJs6 File Inspector plugin inspects any readable regular file supplied by AutoJs6 Explorer through temporary read-only content URI access. It reports file metadata, the first 64 header bytes, and multiple digests without changing the source file.

******

### Features

******

- Registers a read-only single-file Explorer overflow action through the shared `org.autojs.plugin.EXPLORER_ACTION` protocol.
- Reads the source once while computing CRC32, MD5, SHA-1, SHA-256, and SHA-512 together, with progress and cancellation.
- Displays declared size, actual size, MIME type, extension, the first 64 bytes in hexadecimal and ASCII, BOM, and a recognized file signature.
- Strictly normalizes an expected digest, infers its algorithm from a valid length or explicit prefix, and compares equal-length bytes without an early exit at the mismatch position.
- Copies an individual checksum or copies and shares the complete inspection report.

******

### Inspected data

******

Version 1 computes digests for every readable regular file and recognizes these fixed header signatures at offset 0:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### Plugin interface

******

AutoJs6 discovers and executes the plugin with the following identities:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: file-inspector
engine: explorer-action
variant: default
Explorer action id: inspect-file
MIME type: */*
required host build: 5268
supported ABIs: unrestricted (supportedAbis = emptyArray())
```

Version 1 is limited to a read-only single-file overflow action in the main AutoJs6 Explorer.

The plugin is implemented entirely on the JVM and contains no native library. It declares `supportedAbis = emptyArray()` and is released as one ABI-independent APK. AutoJs6 host build 5268 or later is required.

******

### Security

******

The plugin requests no storage or network permission. The host grants temporary read-only access to the target content URI. The plugin verifies the exact intent action, URI, clip data, file name, MIME type, and declared size, rejects write or persistable grants, and never writes the source. It rejects declared or actual size mismatches and inputs larger than 8 TiB. File bytes are processed with a bounded buffer, and only the 64-byte header snapshot is retained in the report.

******

### Safety limits

******

- Maximum input size: `8 TiB`.
- Header snapshot: `64 bytes`.
- Maximum expected digest text: `512 ASCII characters`.
- One target file per action.
- Signature detection uses only fixed bytes at offset 0 and is not complete format validation.
- MD5 and SHA-1 are displayed as legacy digests and should not be treated as collision-resistant security proofs.

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

##### For more releases

* [CHANGELOG-en.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

******

### Build

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release build:

```powershell
.\gradlew.bat :app:assembleRelease
```

Build parameters come from `version.properties`. The current minimum SDK is 24 and the target SDK is 36.

******

### Resource layout

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localizes plugin metadata and UI text. `plugin_instruction.md` provides instructions shown by the host. `.python/generate_markdown.py` generates localized README and changelog files from JSON sources.

******

### Links

******

- AutoJs6 documentation: https://docs.autojs6.com
- Android secure file sharing: https://developer.android.com/training/secure-file-sharing
