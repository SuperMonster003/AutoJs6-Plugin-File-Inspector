<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="File Inspector" width="128" />
  </p>

  <h1>File Inspector</h1>

  <p>AutoJs6 파일 관리자 플러그인: 한 번의 읽기로 일곱 가지 체크섬을 계산하고, 공개된 값을 붙여넣으면 즉시 무결성을 확인하며, 파일의 실제 형식까지 한눈에 파악</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml/badge.svg"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

### 언어 (Languages)

이 README는 다음 언어로 제공됩니다:

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

### 소개

File Inspector는 AutoJs6 파일 관리자의 확장 플러그인입니다. 아무 파일에서나 "파일 검사"를 선택하면 플러그인이 파일을 딱 한 번 읽으면서 CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512 일곱 가지 체크섬을 동시에 계산하고, 파일의 실제 형식 시그니처, 크기, 앞부분 바이트까지 한 화면에 보여줍니다. 모든 과정은 읽기 전용이며, 저장소·네트워크 권한을 요청하지 않고 원본 파일을 절대 수정하지 않습니다.

보고서 화면에는 "무결성 검사" 입력란이 있습니다. 배포처가 공개한 체크섬을 붙여넣으면 플러그인이 알고리즘을 자동으로 판별해 일치 / 불일치 결론을 바로 알려주므로, 긴 16진수 문자열을 눈으로 대조할 필요가 없습니다. 체크섬과 헤더 스냅샷은 각각 복사할 수 있으며, 전체 보고서를 복사하거나 시스템으로 공유하기 전에 Markdown 또는 JSON을 선택할 수 있습니다.

### 주요 기능

- 한 번 읽고 전부 계산: 파일을 순차적으로 한 번만 읽는 동안 일곱 가지 체크섬을 함께 계산하므로, 큰 파일도 알고리즘별로 여러 번 기다릴 필요가 없습니다.
- 붙여넣으면 바로 대조: 16진수와 지문, Base64, SRI, `md5sum` / `sha256sum`의 전체 출력 줄을 지원하고 알고리즘을 자동 판별합니다. 붙여넣은 파일 이름이 다르면 경고합니다.
- 실제 형식 간파: 제한된 앞부분과 고정 오프셋에서 26가지 일반 시그니처를 인식하고, ZIP 기반 APK/JAR/Office 문서에는 용도 힌트를 더하며, 인쇄 가능 문자 비율과 엔트로피로 텍스트·바이너리를 추정합니다.
- 헤더 한눈에: 파일 앞 64 바이트를 16진수 + ASCII 대조 형식으로 보여주고, UTF-8 / UTF-16 / UTF-32의 바이트 순서 표식 (BOM) 을 감지합니다.
- 크기 이중 확인: 파일 관리자가 신고한 크기와 실제로 읽은 크기를 함께 표시하며, 읽는 도중 파일이 바뀌면 즉시 오류로 처리해 불완전한 다운로드나 기록 중인 파일을 잡아냅니다.
- 진행 상황 제어: 큰 파일은 읽은 바이트, 읽기 속도와 예상 남은 시간을 표시하고 언제든 취소할 수 있으며, 실패한 검사는 한 번의 탭으로 재시도할 수 있습니다.
- 결과 바로 활용: 체크섬과 헤더 스냅샷을 개별 복사하고 화면의 전체 결과를 Markdown 또는 JSON으로 클립보드나 시스템 공유 패널에 내보낼 수 있으며 파일은 만들지 않습니다.
- 구식 알고리즘 명시: MD5와 SHA-1에는 Legacy 배지가 붙어, 더 이상 보안 증명으로 적합하지 않음을 알립니다.

### 사용 방법

1. 같은 공식 Release에서 APK와 이름이 같은 `.apk.sha256` 검사 파일을 다운로드하고 APK를 설치한 뒤 AutoJs6 플러그인 센터에서 활성화합니다 (AutoJs6 버전 코드 5268 이상 필요).
2. 업그레이드 전에는 이미 설치된 File Inspector로 새 APK를 검사합니다. 처음 설치할 때는 APK를 보관했다가 플러그인을 활성화한 뒤 검사하십시오. 검사 파일의 64자 SHA-256을 붙여 넣고 초록색 일치를 확인하며, 두 파일은 같은 신뢰할 수 있는 HTTPS Release 페이지에서 받아야 합니다.
3. AutoJs6 파일 관리자를 열고 검사할 파일을 찾습니다. 일반 파일이라면 종류는 무엇이든 됩니다.
4. 파일 메뉴에서 "파일 검사"를 선택하면 곧바로 읽기가 시작되고 진행률이 표시됩니다. 완료되면 체크섬, 형식 시그니처, 헤더 바이트를 볼 수 있습니다.
5. 무결성을 확인하려면 공개된 체크섬을 "무결성 검사" 입력란에 붙여넣고 "검증"을 탭합니다. 초록색은 일치, 빨간색은 불일치입니다.
6. 체크섬 옆 또는 헤더 스냅샷 아래의 복사 버튼으로 개별 값을 얻고, Markdown 또는 JSON을 선택해 전체 보고서를 복사·공유한 뒤 뒤로 가기를 누르면 파일 관리자로 돌아갑니다.

> 체크섬 입력은 순수 16진수, `sha256: <값>` 또는 `MD5=<값>` 같은 접두사, `AB:CD:EF` 지문, CRC32용 `0x`, 표준 Base64, `sha256-<base64>` 형식의 SRI, coreutils의 전체 `<16진수>  <파일>` 또는 `<16진수> *<파일>` 줄을 지원합니다. 대소문자와 앞뒤 공백은 무시되며 길이가 고유하면 알고리즘을 자동 판별하고, 파일 이름이 다르면 경고합니다.

### 인식 가능한 형식 시그니처

체크섬 계산은 읽을 수 있는 모든 일반 파일에 사용할 수 있으며, 현재 버전은 여기에 더해 제한된 앞부분 또는 고정 오프셋의 다음 형식 시그니처를 인식합니다:

```text
ZIP, 7z, RAR 4, RAR 5, GZIP, XZ, BZIP2, Zstandard, LZ4, TAR, PDF, PNG, JPEG, GIF87a, GIF89a, WebP, MP4 / ISO-BMFF, EBML / Matroska, ELF, DEX, Java Class, Mach-O, PE, SQLite 3, WOFF, WOFF2
```

시그니처 감지는 제한된 샘플과 오프셋 257의 TAR 매직, 오프셋 4의 ISO-BMFF `ftyp`, PE 헤더 포인터 같은 구조 필드를 사용합니다. 이는 빠른 힌트일 뿐 완전한 형식 검증이 아니며, 인쇄 가능 비율과 엔트로피도 휴리스틱 추정값입니다. 일치하는 시그니처가 없으면 "알 수 없음"으로 표시되지만 체크섬은 정상 계산됩니다.

### 자주 묻는 질문

#### 이 플러그인은 언제 쓰면 좋나요?

대표적인 용도는 다운로드 검증입니다. 설치 파일, 펌웨어, 문서를 받은 뒤 배포처가 공개한 SHA-256 등의 체크섬을 붙여넣으면 파일이 온전한지, 변조되지 않았는지 즉시 알 수 있습니다. 확장자가 위장된 파일의 실제 형식을 확인하거나, 보관·비교용으로 아무 파일의 체크섬을 빠르게 만들 때도 유용합니다.

#### 체크섬이 "일치"하면 파일이 안전하다는 뜻인가요?

일치는 그 체크섬이 공개된 바로 그 파일과 바이트 단위로 동일하다는 사실만 증명합니다. 신뢰 여부는 체크섬의 출처에 달려 있습니다. 공식 출처가 HTTPS로 공개한 SHA-256 또는 SHA-512 값을 우선 사용하세요. MD5와 SHA-1은 의도적인 충돌을 만들 수 있어 플러그인이 Legacy로 표시하며, 보안 증명으로 삼아서는 안 됩니다.

#### 일부 파일은 왜 검사할 수 없나요?

흔한 원인: 파일이 8 TiB 한도를 초과함, 읽는 도중 다른 앱이 파일을 수정해 실제 크기가 신고된 크기와 달라짐, 호스트가 부여한 읽기 전용 권한이 만료됨 등입니다. 오류 메시지에 구체적인 이유가 표시되며, "재시도"로 다시 검사할 수 있습니다.

#### 큰 파일은 검사가 오래 걸리지 않나요?

플러그인은 파일을 순차적으로 한 번만 읽으면서 일곱 가지 체크섬을 모두 계산하므로, 소요 시간은 알고리즘 수가 아니라 저장소 읽기 속도에 좌우됩니다. 진행률이 실시간으로 표시되고 언제든 취소할 수 있습니다.

### 권한과 보안

플러그인은 저장소·네트워크 권한을 요청하지 않으며, 호스트가 임시로 부여한 읽기 전용 content URI를 통해 사용자가 선택한 그 파일 하나에만 접근할 수 있습니다. 권한은 검사가 끝나면 만료되고 다른 파일에는 닿을 수 없습니다. 파일 관리자에서 온 요청은 동작 ID, 프로토콜 버전, content URI 형식, 파일 이름, MIME 유형, 신고 크기, 읽기 전용 권한을 항목별로 검증하며, 쓰기 또는 영구 권한이 딸린 요청은 그 자리에서 거부합니다. 파일은 항상 읽기 전용 스트림으로 처리되며, 메모리에는 제한된 버퍼, 분석용 앞 4096 바이트, 표시용 앞 64 바이트, 4바이트 PE 시그니처 창만 남고 원본은 수정되지 않습니다.

모든 검사를 예측 가능하게 유지하기 위해 플러그인에는 다음 한계가 있습니다:

- 파일 하나의 최대 크기는 8 TiB 이며, 한 번의 동작은 대상 파일 하나만 처리합니다.
- 분석은 앞 4096 바이트와 4바이트 PE 시그니처 창만 샘플링하며, 표시 헤더는 64 바이트로 유지됩니다. 체크섬 입력은 512 자까지이며 비ASCII 문자는 coreutils 파일 이름 부분에서만 허용됩니다.
- 실제로 읽은 크기가 신고된 크기와 다르면 파일이 변경된 것으로 판정하고 검사를 실패 처리하며 사유를 알립니다.
- 체크섬 대조는 같은 길이 값의 상수 시간 비교로 수행합니다. MD5와 SHA-1은 구형 데이터 대조 용도일 뿐, 충돌 저항성의 증명으로 간주해서는 안 됩니다.

### 플러그인 인터페이스

호스트 (AutoJs6) 는 다음 식별자로 플러그인을 발견하고 호출합니다. 플러그인·호스트 개발자를 위한 참고 정보입니다:

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

현재 버전은 호스트 파일 관리자에 단일 파일 대상의 읽기 전용 오버플로 메뉴 동작을 추가합니다. 원본 파일을 수정하지 않고 디렉터리를 열거하지도 않습니다. 플러그인이 없거나 비활성화되면 호스트는 기본 동작으로 자연스럽게 되돌아갑니다.

### Roadmap

구현된 기능은 위 내용과 Roadmap의 체크된 항목이 기준입니다. 추가 provider가 필요한 다이제스트 알고리즘, 일괄 검증, 호스트 프로토콜 확장 등의 계획은 Roadmap에서 관리하며, 체크되지 않은 항목은 현재 기능이 아닙니다.

- [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/ROADMAP.md)

### 릴리스 기록

#### v1.0.1

_2026/08/08_

- `수정` 플러그인 센터에서 활성화한 뒤 호스트가 플러그인 서비스에 바인딩하지 못하던 문제를 수정. 이제 활성화 직후부터 "파일 검사" 동작을 바로 사용할 수 있습니다
- `개선` 플러그인 이름과 설명을 간결하게 다듬어 사용자 문서가 더 자연스럽게 읽히도록 개선

#### v1.0.0

_2026/08/02_

- `힌트` 첫 공개 버전. AutoJs6 버전 코드 5268 이상이 필요합니다
- `기능` AutoJs6 파일 관리자의 파일 메뉴에 읽기 전용 동작 "파일 검사"를 추가. 모든 종류의 일반 파일에 사용 가능 (플러그인 ID `file-inspector`, 동작 ID `inspect-file`)
- `기능` 한 번의 순차 읽기로 CRC32, MD5, SHA-1, SHA-256, SHA-512 다섯 가지 체크섬을 동시에 계산. 실시간 진행률 표시와 취소·재시도 지원
- `기능` 예상 체크섬을 붙여넣는 무결성 검증 지원: 길이 또는 `sha256:` 등의 접두사로 알고리즘을 자동 판별하고, `AB:CD:EF` 식 지문 구분자와 CRC32의 `0x` 접두사를 허용하며, 같은 길이 값의 상수 시간 비교로 대조
- `기능` 보고서에 파일 이름, MIME 유형, 확장자, 신고 크기와 실제 크기, 앞 64 바이트의 16진수 + ASCII 스냅샷, UTF BOM 감지 표시
- `기능` 앞부분 시그니처 바이트로 10가지 일반 형식 인식: ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
- `기능` 체크섬 개별 복사, 보고서 전체 복사와 시스템 공유 지원. MD5와 SHA-1에는 Legacy 배지 표시
- `기능` 플러그인은 저장소·네트워크 권한을 요청하지 않으며 호스트가 임시로 부여한 읽기 전용 content URI로만 파일을 읽음. 파일당 상한 8 TiB
- `기능` 인터페이스 문구, 사용 설명, README, CHANGELOG를 10개 언어로 내장: 중국어 간체, 중국어 번체 (홍콩), 중국어 번체 (대만), 영어, 프랑스어, 스페인어, 일본어, 한국어, 러시아어, 아랍어
- `의존성` AndroidX Lifecycle ViewModel 2.9.4 도입

##### 전체 기록

- [CHANGELOG-ko.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

### 빌드

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 빌드:

```powershell
.\gradlew.bat :app:appendDigestToReleasedFiles
.\gradlew.bat :app:verifyReleaseChecksums
```

빌드와 서명 매개변수는 version.properties와 sign.properties가 관리합니다. 현재 최소 지원은 Android 7.0 (SDK 24), 대상 SDK는 36입니다.

README, CHANGELOG와 res/raw*/plugin_instruction.md의 플러그인 내 설명은 .readme/ 와 .changelog/ 의 JSON 언어 소스와 템플릿을 바탕으로 .python/generate_markdown.py 가 생성합니다 (10개 언어). 수정할 때는 생성된 Markdown을 직접 고치지 말고 JSON 소스를 수정한 뒤 스크립트를 다시 실행하세요.

### 관련 링크

- AutoJs6 문서: https://docs.autojs6.com
- Android 안전한 파일 공유: https://developer.android.com/training/secure-file-sharing
