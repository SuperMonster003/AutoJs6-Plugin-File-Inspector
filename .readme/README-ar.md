<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>ملحق مدير الملفات. يفحص تواقيع الملفات ويتحقق من المجاميع الاختبارية التشفيرية</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### اللغات (Languages)

******

يدعم ملف README.md الحالي اللغات التالية:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- العربية [ar] # الحالي

******

### مقدمة

******

يفحص File Inspector اي ملف عادي قابل للقراءة يقدمه مدير الملفات عبر وصول مؤقت للقراءة فقط الى content URI. يعرض بيانات الملف واول 64 بايت من الرأس وعدة قيم digest من دون تعديل الملف المصدر.

******

### الميزات

******

- يسجل اجراء overflow للقراءة فقط لملف واحد عبر بروتوكول `org.autojs.plugin.EXPLORER_ACTION` المشترك.
- يقرا المصدر مرة واحدة ويحسب CRC32 وMD5 وSHA-1 وSHA-256 وSHA-512 معا مع عرض التقدم ودعم الالغاء.
- يعرض الحجم المعلن والحجم الفعلي ونوع MIME والامتداد واول 64 بايت بصيغتي hexadecimal وASCII وBOM وتوقيع الملف المعروف.
- يطبع قيمة digest المتوقعة بشكل صارم ويستنتج الخوارزمية من طول صالح او بادئة صريحة ويقارن البايتات متساوية الطول من دون خروج مبكر عند موضع الاختلاف.
- ينسخ قيمة checksum منفردة او ينسخ تقرير الفحص الكامل ويشاركه.

******

### البيانات التي يتم فحصها

******

يحسب الاصدار 1 قيم digest لكل ملف عادي قابل للقراءة ويتعرف على تواقيع الرأس الثابتة التالية عند الازاحة 0:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### واجهة الملحق

******

يكتشف المضيف الملحق وينفذه بالمعرفات التالية:

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

يوفر الاصدار 1 اجراء overflow للقراءة فقط لملف واحد في مدير الملفات الرئيسي.

يتطلب الملحق بناء المضيف رقم 5268 او احدث.

******

### الامان

******

لا يطلب الملحق اذن التخزين او الشبكة. يمنح المضيف وصولا مؤقتا للقراءة فقط الى content URI الهدف. يتحقق الملحق من اجراء Intent الدقيق وURI وClipData والاسم ونوع MIME والحجم المعلن ويرفض اذونات الكتابة او الاذونات الدائمة ولا يكتب المصدر مطلقا. يرفض اختلاف الحجم المعلن عن الفعلي والمدخلات الاكبر من 8 TiB. تتم معالجة بايتات الملف بمخزن مؤقت محدود ولا يحتفظ التقرير الا بلقطة رأس حجمها 64 بايت.

******

### حدود الامان

******

- الحد الاقصى لحجم المدخل: `8 TiB`.
- لقطة الرأس: `64 bytes`.
- الحد الاقصى لنص digest المتوقع: `512 ASCII characters`.
- ملف هدف واحد لكل اجراء.
- يعتمد اكتشاف التوقيع على بايتات ثابتة عند الازاحة 0 فقط ولا يمثل تحققا كاملا من التنسيق.
- تعرض MD5 وSHA-1 كقيم digest قديمة ولا ينبغي اعتبارهما اثبات امان مقاوما للتصادم.

******

### سجل الاصدارات

******

# v1.0.1

###### 2026/08/08

* `إصلاح` ربط خدمة فارغ عند تفعيل الملحق في مركز الملحقات
* `تحسين` اسم ووصف ووثائق مستخدم اوضح واكثر ايجازا

# v1.0.0

###### 2026/08/02

* `ميزة` ملحق File Inspector بمعرف الملحق `file-inspector` ومعرف الاجراء `inspect-file` والمحرك `explorer-action` والمتغير `default`
* `ميزة` اجراء مستكشف للقراءة فقط لملف عادي واحد قابل للقراءة مع حد ادخال 8 TiB ومن دون اذن تخزين او شبكة
* `ميزة` حساب CRC32 وMD5 وSHA-1 وSHA-256 وSHA-512 في قراءة واحدة مع عرض التقدم ودعم الالغاء
* `ميزة` تطبيع وتحقق صارمان لقيمة digest المتوقعة مع استنتاج الخوارزمية والبادئات الصريحة ومقارنة البايتات متساوية الطول بزمن ثابت
* `ميزة` لقطة رأس من 64 بايت بصيغتي hexadecimal وASCII واكتشاف BOM والتعرف على تواقيع ZIP وGZIP وPDF وPNG وJPEG وGIF87a وGIF89a وELF وDEX وSQLite 3
* `ميزة` بيانات الملحق ونصوص الواجهة وتعليمات الاستخدام وملفات README وسجلات التغييرات المترجمة الى الاسبانية والفرنسية والروسية والعربية واليابانية والكورية والانجليزية والصينية المبسطة والصينية التقليدية لهونغ كونغ والصينية التقليدية لتايوان
* `تبعية` إضافة AndroidX Lifecycle ViewModel الإصدار 2.9.4

##### لمزيد من الاصدارات

* [CHANGELOG-ar.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-ar.md)

******

### البناء

******

```powershell
.\gradlew.bat :app:assembleDebug
```

بناء Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

تاتي معاملات البناء من `version.properties`. الحد الادنى الحالي لاصدار SDK هو 24 واصدار SDK المستهدف هو 36.

******

### تخطيط الموارد

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

يوفر `strings.xml` ترجمة بيانات الملحق ونصوص الواجهة. يوفر `plugin_instruction.md` التعليمات التي يعرضها المضيف. ينشئ `.python/generate_markdown.py` ملفات README وسجل التغييرات المترجمة من مصادر JSON.

******

### الروابط

******

- وثائق AutoJs6: https://docs.autojs6.com
- مشاركة الملفات الامنة في Android: https://developer.android.com/training/secure-file-sharing
