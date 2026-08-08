<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>Плагин файлового менеджера. Проверяет сигнатуры файлов и криптографические контрольные суммы</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Языки (Languages)

******

Текущий README.md поддерживает следующие языки:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- Русский [ru] # текущий
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

******

### Введение

******

File Inspector проверяет любой доступный для чтения обычный файл, переданный файловым менеджером через временный доступ только для чтения к content URI. Он показывает метаданные, первые 64 байта заголовка и несколько дайджестов, не изменяя исходный файл.

******

### Возможности

******

- Регистрирует действие переполнения Проводника только для чтения для одного файла через общий протокол `org.autojs.plugin.EXPLORER_ACTION`.
- Читает источник один раз и одновременно вычисляет CRC32, MD5, SHA-1, SHA-256 и SHA-512 с отображением прогресса и возможностью отмены.
- Показывает заявленный и фактический размер, MIME-тип, расширение, первые 64 байта заголовка в шестнадцатеричном виде и ASCII, BOM и распознанную сигнатуру файла.
- Строго нормализует ожидаемый дайджест, определяет алгоритм по допустимой длине или явному префиксу и сравнивает байты одинаковой длины без раннего выхода в позиции несовпадения.
- Копирует отдельную контрольную сумму либо копирует и отправляет полный отчет проверки.

******

### Проверяемые данные

******

Версия 1 вычисляет дайджесты для любого доступного для чтения обычного файла и распознает следующие фиксированные сигнатуры заголовка со смещением 0:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### Интерфейс плагина

******

Хост обнаруживает и запускает плагин со следующими идентификаторами:

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

Версия 1 предоставляет действие дополнительного меню только для чтения для одного файла в основном файловом менеджере.

Требуется сборка хоста 5268 или новее.

******

### Безопасность

******

Плагин не запрашивает разрешения хранилища или сети. Хост предоставляет временный доступ только для чтения к целевому content URI. Плагин проверяет точное действие Intent, URI, ClipData, имя, MIME-тип и заявленный размер, отклоняет разрешения на запись и постоянный доступ и никогда не записывает источник. Несоответствие заявленного и фактического размера, а также входные данные больше 8 TiB отклоняются. Байты файла обрабатываются ограниченным буфером, а в отчете сохраняется только снимок заголовка размером 64 байта.

******

### Ограничения безопасности

******

- Максимальный размер входного файла: `8 TiB`.
- Снимок заголовка: `64 bytes`.
- Максимальный текст ожидаемого дайджеста: `512 ASCII characters`.
- Один целевой файл на действие.
- Определение сигнатуры использует только фиксированные байты со смещением 0 и не является полной проверкой формата.
- MD5 и SHA-1 помечаются как устаревшие дайджесты и не должны считаться доказательством безопасности с устойчивостью к коллизиям.

******

### История выпусков

******

# v1.0.1

###### 2026/08/08

* `Исправление` Пустая привязка службы при включении плагина в центре плагинов
* `Улучшение` Более ясные и краткие название, описание и пользовательская документация

# v1.0.0

###### 2026/08/02

* `Функция` Плагин File Inspector с ID плагина `file-inspector`, ID действия `inspect-file`, движком `explorer-action` и вариантом `default`
* `Функция` Действие Проводника только для чтения для одного доступного обычного файла, ограничение входа 8 TiB, без разрешений хранилища и сети
* `Функция` Вычисление CRC32, MD5, SHA-1, SHA-256 и SHA-512 за одно чтение с отображением прогресса и отменой
* `Функция` Строгая нормализация и проверка ожидаемого дайджеста с определением алгоритма, явными префиксами и сравнением байтов одинаковой длины за постоянное время
* `Функция` Снимок 64 байтов заголовка в шестнадцатеричном виде и ASCII, определение BOM и распознавание сигнатур ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX и SQLite 3
* `Функция` Локализованные метаданные, текст интерфейса, инструкции, README и журналы изменений на испанском, французском, русском, арабском, японском, корейском, английском, упрощенном китайском, традиционном китайском Гонконга и традиционном китайском Тайваня
* `Зависимость` Добавлена зависимость AndroidX Lifecycle ViewModel версии 2.9.4

##### Другие выпуски

* [CHANGELOG-ru.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-ru.md)

******

### Сборка

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release-сборка:

```powershell
.\gradlew.bat :app:assembleRelease
```

Параметры сборки берутся из `version.properties`. Текущий минимальный SDK равен 24, целевой SDK равен 36.

******

### Структура ресурсов

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` локализует метаданные плагина и текст интерфейса. `plugin_instruction.md` содержит инструкции, показываемые хостом. `.python/generate_markdown.py` создает локализованные README и журналы изменений из источников JSON.

******

### Ссылки

******

- Документация AutoJs6: https://docs.autojs6.com
- Безопасная передача файлов Android: https://developer.android.com/training/secure-file-sharing
