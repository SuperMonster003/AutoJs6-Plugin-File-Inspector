<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>파일 관리자 플러그인. 파일 시그니처를 검사하고 암호학적 체크섬을 검증</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어 (Languages)

******

현재 README.md는 다음 언어를 지원합니다:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- 한국어 [ko] # 현재
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

******

### 소개

******

파일 검사기는 파일 관리자가 임시 읽기 전용 content URI 접근으로 제공한 읽을 수 있는 일반 파일을 검사합니다. 원본 파일을 변경하지 않고 메타데이터, 처음 64바이트 헤더 및 여러 다이제스트를 표시합니다.

******

### 기능

******

- 공유 `org.autojs.plugin.EXPLORER_ACTION` 프로토콜을 통해 단일 파일용 읽기 전용 탐색기 오버플로 작업을 등록합니다.
- 원본을 한 번만 읽으면서 CRC32, MD5, SHA-1, SHA-256 및 SHA-512를 함께 계산하고 진행률과 취소를 지원합니다.
- 선언된 크기, 실제 크기, MIME 유형, 확장자, 처음 64바이트의 16진수 및 ASCII 헤더, BOM과 인식된 파일 시그니처를 표시합니다.
- 예상 다이제스트를 엄격하게 정규화하고 유효한 길이 또는 명시적 접두사로 알고리즘을 추론하며 불일치 위치에서 조기 종료하지 않고 같은 길이의 바이트를 비교합니다.
- 개별 체크섬을 복사하거나 전체 검사 보고서를 복사하고 공유할 수 있습니다.

******

### 검사 데이터

******

버전 1은 읽을 수 있는 모든 일반 파일의 다이제스트를 계산하고 오프셋 0의 다음 고정 헤더 시그니처를 인식합니다:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### 플러그인 인터페이스

******

호스트는 다음 식별자로 플러그인을 검색하고 실행합니다:

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

버전 1은 기본 파일 관리자에서 단일 파일용 읽기 전용 오버플로 작업을 제공합니다.

호스트 빌드 5268 이상이 필요합니다.

******

### 보안

******

플러그인은 저장소 또는 네트워크 권한을 요청하지 않습니다. 호스트는 대상 content URI에 임시 읽기 전용 접근만 부여합니다. 플러그인은 정확한 Intent 작업, URI, ClipData, 파일 이름, MIME 유형 및 선언된 크기를 검증하고 쓰기 또는 영구 권한을 거부하며 원본을 쓰지 않습니다. 선언된 크기와 실제 크기가 다르거나 입력이 8 TiB보다 크면 거부합니다. 파일 바이트는 제한된 버퍼로 처리되며 보고서에는 64바이트 헤더 스냅샷만 유지됩니다.

******

### 안전 제한

******

- 최대 입력 크기: `8 TiB`.
- 헤더 스냅샷: `64 bytes`.
- 예상 다이제스트 텍스트 최대 길이: `512 ASCII characters`.
- 작업당 대상 파일 1개.
- 시그니처 감지는 오프셋 0의 고정 바이트만 사용하며 완전한 형식 검증이 아닙니다.
- MD5와 SHA-1은 레거시 다이제스트로 표시되며 충돌 방지 보안 증명으로 간주해서는 안 됩니다.

******

### 릴리스 기록

******

# v1.0.1

###### 2026/08/08

* `수정` 플러그인 센터에서 활성화할 때 서비스 바인딩이 null이 되는 문제
* `개선` 더 명확하고 간결한 플러그인 이름, 설명 및 사용자 문서

# v1.0.0

###### 2026/08/02

* `기능` 플러그인 ID `file-inspector`, 작업 ID `inspect-file`, 엔진 `explorer-action`, 변형 `default`를 사용하는 File Inspector 플러그인
* `기능` 읽을 수 있는 모든 일반 파일을 위한 단일 파일 읽기 전용 탐색기 작업, 입력 제한 8 TiB, 저장소 또는 네트워크 권한 없음
* `기능` 한 번의 읽기로 CRC32, MD5, SHA-1, SHA-256 및 SHA-512를 함께 계산하고 진행률 및 취소 지원
* `기능` 알고리즘 추론, 명시적 접두사 및 같은 길이 바이트의 상수 시간 비교를 사용하는 예상 다이제스트의 엄격한 정규화 및 검증
* `기능` 64바이트 16진수 및 ASCII 헤더 스냅샷, BOM 감지, ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX 및 SQLite 3 시그니처 인식
* `기능` 스페인어, 프랑스어, 러시아어, 아랍어, 일본어, 한국어, 영어, 중국어 간체, 홍콩 중국어 번체 및 대만 중국어 번체로 현지화된 메타데이터, UI 텍스트, 사용 안내, README 및 변경 기록
* `의존성` AndroidX Lifecycle ViewModel 버전 2.9.4 추가

##### 더 많은 릴리스

* [CHANGELOG-ko.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

******

### 빌드

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 빌드:

```powershell
.\gradlew.bat :app:assembleRelease
```

빌드 매개변수는 `version.properties`에서 가져옵니다. 현재 최소 SDK는 24이고 대상 SDK는 36입니다.

******

### 리소스 구성

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml`은 플러그인 메타데이터와 UI 텍스트를 현지화합니다. `plugin_instruction.md`는 호스트에 표시되는 안내를 제공합니다. `.python/generate_markdown.py`는 JSON 원본에서 현지화된 README와 변경 기록을 생성합니다.

******

### 링크

******

- AutoJs6 문서: https://docs.autojs6.com
- Android 보안 파일 공유: https://developer.android.com/training/secure-file-sharing
