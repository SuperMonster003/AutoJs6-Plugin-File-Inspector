******

### 릴리스 기록

******

# v1.0.0

###### 2026/08/02

* `기능` 플러그인 ID `file-inspector`, 작업 ID `inspect-file`, 엔진 `explorer-action`, 변형 `default`를 사용하는 File Inspector 플러그인
* `기능` 읽을 수 있는 모든 일반 파일을 위한 단일 파일 읽기 전용 탐색기 작업, 입력 제한 8 TiB, 저장소 또는 네트워크 권한 없음
* `기능` 한 번의 읽기로 CRC32, MD5, SHA-1, SHA-256 및 SHA-512를 함께 계산하고 진행률 및 취소 지원
* `기능` 알고리즘 추론, 명시적 접두사 및 같은 길이 바이트의 상수 시간 비교를 사용하는 예상 다이제스트의 엄격한 정규화 및 검증
* `기능` 64바이트 16진수 및 ASCII 헤더 스냅샷, BOM 감지, ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX 및 SQLite 3 시그니처 인식
* `기능` 네이티브 라이브러리가 없는 순수 JVM 구현, `supportedAbis = emptyArray()`를 통한 ABI 무제한 선언, ABI 독립적인 단일 APK 및 필수 AutoJs6 호스트 빌드 5268
* `기능` 스페인어, 프랑스어, 러시아어, 아랍어, 일본어, 한국어, 영어, 중국어 간체, 홍콩 중국어 번체 및 대만 중국어 번체로 현지화된 메타데이터, UI 텍스트, 사용 안내, README 및 변경 기록
* `의존성` AndroidX Lifecycle ViewModel 버전 2.9.4 추가
