<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>Complemento del gestor de archivos. Inspecciona firmas de archivo y verifica sumas de comprobación criptográficas</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas (Languages)

******

El README.md actual admite los siguientes idiomas:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- Español [es] # actual
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

******

### Introducción

******

File Inspector inspecciona cualquier archivo normal legible que proporciona el gestor de archivos mediante acceso temporal de solo lectura a un content URI. Muestra metadatos, los primeros 64 bytes de la cabecera y varios resúmenes sin modificar el archivo de origen.

******

### Funciones

******

- Registra una acción adicional de solo lectura para un único archivo mediante el protocolo compartido `org.autojs.plugin.EXPLORER_ACTION`.
- Lee el origen una sola vez mientras calcula CRC32, MD5, SHA-1, SHA-256 y SHA-512 en conjunto, con progreso y cancelación.
- Muestra tamaño declarado, tamaño real, tipo MIME, extensión, los primeros 64 bytes en hexadecimal y ASCII, BOM y una firma de archivo reconocida.
- Normaliza estrictamente un resumen esperado, deduce el algoritmo por una longitud válida o un prefijo explícito y compara bytes de igual longitud sin finalizar antes en la posición diferente.
- Copia una suma de comprobación individual o copia y comparte el informe de inspección completo.

******

### Datos inspeccionados

******

La versión 1 calcula resúmenes para cualquier archivo normal legible y reconoce estas firmas de cabecera fijas en el desplazamiento 0:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### Interfaz del plugin

******

El host descubre y ejecuta el plugin con las siguientes identidades:

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

La versión 1 ofrece una acción adicional de solo lectura para un único archivo en el gestor de archivos principal.

Se requiere la compilación 5268 o posterior del host.

******

### Seguridad

******

El plugin no solicita permisos de almacenamiento ni de red. El host concede acceso temporal de solo lectura al content URI de destino. El plugin verifica la acción Intent exacta, URI, ClipData, nombre, tipo MIME y tamaño declarado, rechaza permisos de escritura o persistentes y nunca escribe el origen. Rechaza diferencias entre el tamaño declarado y el real, además de entradas mayores de 8 TiB. Los bytes se procesan con un búfer limitado y el informe solo conserva una captura de cabecera de 64 bytes.

******

### Límites de seguridad

******

- Tamaño máximo de entrada: `8 TiB`.
- Captura de cabecera: `64 bytes`.
- Texto máximo del resumen esperado: `512 ASCII characters`.
- Un archivo de destino por acción.
- La detección de firmas solo usa bytes fijos en el desplazamiento 0 y no es una validación completa del formato.
- MD5 y SHA-1 se muestran como resúmenes heredados y no deben considerarse pruebas de seguridad resistentes a colisiones.

******

### Historial de versiones

******

# v1.0.1

###### 2026/08/08

* `Corrección` Enlace de servicio nulo al activar el complemento en el centro de complementos
* `Mejora` Nombre, descripción y documentación de usuario más claros y concisos

# v1.0.0

###### 2026/08/02

* `Función` Plugin File Inspector con ID de plugin `file-inspector`, ID de acción `inspect-file`, motor `explorer-action` y variante `default`
* `Función` Acción de Explorador de solo lectura para un único archivo normal legible, con límite de entrada de 8 TiB y sin permisos de almacenamiento ni de red
* `Función` Cálculo de CRC32, MD5, SHA-1, SHA-256 y SHA-512 en una sola lectura con progreso y cancelación
* `Función` Normalización y verificación estrictas del resumen esperado con deducción del algoritmo, prefijos explícitos y comparación en tiempo constante de bytes de igual longitud
* `Función` Captura de cabecera de 64 bytes en hexadecimal y ASCII, detección de BOM y reconocimiento de firmas ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX y SQLite 3
* `Función` Metadatos, texto de interfaz, instrucciones de uso, archivos README e historiales localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán
* `Dependencia` Añadido AndroidX Lifecycle ViewModel versión 2.9.4

##### Para consultar más versiones

* [CHANGELOG-es.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

******

### Compilación

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilación Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Los parámetros de compilación proceden de `version.properties`. El SDK mínimo actual es 24 y el SDK de destino es 36.

******

### Estructura de recursos

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localiza los metadatos del plugin y el texto de la interfaz. `plugin_instruction.md` proporciona instrucciones visibles desde el host. `.python/generate_markdown.py` genera archivos README y de cambios localizados a partir de fuentes JSON.

******

### Enlaces

******

- Documentación de AutoJs6: https://docs.autojs6.com
- Uso compartido seguro de archivos en Android: https://developer.android.com/training/secure-file-sharing
