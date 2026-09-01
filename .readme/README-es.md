<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="File Inspector" width="128" />
  </p>

  <h1>File Inspector</h1>

  <p>Plugin del gestor de archivos de AutoJs6: siete sumas de comprobación en una sola lectura, pega el valor publicado para verificar la integridad al instante y descubre el formato real del archivo de un vistazo</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml/badge.svg"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

### Idiomas (Languages)

Este README está disponible en los siguientes idiomas:

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

### Presentación

File Inspector es un plugin complementario del gestor de archivos de AutoJs6. Elige «Inspeccionar archivo» sobre cualquier archivo: el plugin lo lee una sola vez y calcula a la vez las sumas CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384 y SHA-512, mostrando además en una sola pantalla la firma de formato real del archivo, su tamaño y sus primeros bytes. Todo es de solo lectura: el plugin no solicita permisos de almacenamiento ni de red y nunca modifica el archivo original.

La página del informe incluye una «Comprobación de integridad»: pega la suma publicada por el proveedor del archivo y el plugin detecta el algoritmo automáticamente y responde con un veredicto claro de coincide / no coincide, sin volver a comparar largas cadenas hexadecimales a ojo. Cada suma y la instantánea de la cabecera pueden copiarse por separado; elige Markdown o JSON antes de copiar el informe completo o enviarlo por el panel de compartir del sistema.

### Puntos destacados

- Una lectura, todos los resultados: el archivo se lee secuencialmente una única vez mientras las siete sumas se calculan a la vez; los archivos grandes nunca esperan varias pasadas.
- Pegar para verificar: admite hexadecimal y huellas, Base64, SRI y líneas completas de `md5sum` / `sha256sum`; detecta el algoritmo automáticamente y avisa si el nombre de archivo pegado es distinto.
- Detección del formato real: reconoce 26 firmas comunes en una cabecera acotada o en posiciones fijas, añade una pista de uso para documentos APK/JAR/Office basados en ZIP y estima texto o binario mediante la proporción imprimible y la entropía.
- Cabecera de un vistazo: muestra los primeros 64 bytes del archivo en vista hexadecimal + ASCII y detecta las marcas de orden de bytes (BOM) UTF-8 / UTF-16 / UTF-32.
- Doble comprobación de tamaño: muestra el tamaño declarado por el gestor de archivos y el tamaño realmente leído; si el archivo cambia durante la lectura, la inspección falla de inmediato, delatando descargas incompletas y archivos aún en escritura.
- Progreso bajo control: los archivos grandes muestran bytes leídos, velocidad y tiempo restante estimado; la lectura puede cancelarse en cualquier momento y una inspección fallida se reintenta con un toque.
- Resultados listos para usar: copia por separado cualquier suma o la instantánea de la cabecera y exporta el resultado completo en Markdown o JSON al portapapeles o al panel de compartir, sin crear archivos.
- Algoritmos antiguos señalados: MD5 y SHA-1 llevan una insignia Legacy como recordatorio de que ya no sirven como prueba de seguridad.

### Cómo usarlo

1. Descarga el APK y su archivo de control homónimo `.apk.sha256` desde la misma Release oficial, instala el APK y actívalo en el centro de plugins de AutoJs6 (se requiere AutoJs6 con código de versión 5268 o superior).
2. Antes de actualizar, inspecciona el APK descargado con File Inspector ya instalado; en una primera instalación, conserva el APK e inspecciónalo después de activar el plugin. Pega el SHA-256 de 64 caracteres del archivo de control y exige una coincidencia verde; obtén ambos archivos de la misma página Release HTTPS de confianza.
3. Abre el gestor de archivos de AutoJs6 y localiza el archivo que quieras inspeccionar; vale cualquier archivo normal.
4. Elige «Inspeccionar archivo» en el menú del archivo; la lectura empieza al momento con progreso en vivo, y al terminar aparecen las sumas, la firma de formato y los bytes de cabecera.
5. Para verificar la integridad, pega la suma publicada en el campo «Comprobación de integridad» y toca «Verificar»: verde significa que coincide, rojo que no.
6. Toca el botón de copia junto a una suma o bajo la instantánea de la cabecera para obtener un valor; elige Markdown o JSON antes de copiar o compartir el informe completo y pulsa atrás para volver al gestor de archivos.

> La entrada admite hexadecimal simple, prefijos como `sha256: <valor>` o `MD5=<valor>`, huellas `AB:CD:EF`, CRC32 con `0x`, Base64 estándar, SRI como `sha256-<base64>` y líneas completas de coreutils `<hex>  <archivo>` o `<hex> *<archivo>`. Se ignoran mayúsculas y espacios exteriores; el algoritmo se infiere cuando la longitud es única y un nombre de archivo distinto genera un aviso.

### Firmas de formato reconocidas

Las sumas de comprobación funcionan con cualquier archivo normal legible; además, la versión actual reconoce las siguientes firmas en una cabecera acotada o en posiciones fijas:

```text
ZIP, 7z, RAR 4, RAR 5, GZIP, XZ, BZIP2, Zstandard, LZ4, TAR, PDF, PNG, JPEG, GIF87a, GIF89a, WebP, MP4 / ISO-BMFF, EBML / Matroska, ELF, DEX, Java Class, Mach-O, PE, SQLite 3, WOFF, WOFF2
```

La detección usa una muestra acotada y campos estructurales como la marca TAR en el desplazamiento 257, `ftyp` de ISO-BMFF en el 4 y el puntero a la cabecera PE. Es una pista rápida, no una validación completa; la proporción imprimible y la entropía también son estimaciones heurísticas. Los archivos sin firma coincidente muestran «Desconocido» y sus sumas se calculan con normalidad.

### Preguntas frecuentes

#### ¿Cuándo resulta útil este plugin?

El caso clásico es verificar descargas: tras obtener un instalador, un firmware o un documento, pega la suma SHA-256 (u otra) publicada por el proveedor y sabrás al instante si el archivo está completo y sin manipular. También sirve para revelar el formato real de archivos con extensiones engañosas, o para generar rápidamente sumas de cualquier archivo para archivarlas y compararlas.

#### ¿Que una suma «coincida» significa que el archivo es seguro?

Una coincidencia solo demuestra que el archivo es idéntico byte a byte a aquel para el que se publicó la suma; la confianza depende del origen de esa suma. Da preferencia a valores SHA-256 o SHA-512 publicados por HTTPS desde la fuente oficial. Las colisiones de MD5 y SHA-1 pueden fabricarse a propósito, por eso el plugin las marca como Legacy: no las tomes como prueba de seguridad.

#### ¿Por qué algunos archivos no se pueden inspeccionar?

Motivos habituales: el archivo supera el límite de 8 TiB; otra aplicación modificó el archivo durante la lectura, de modo que el tamaño real ya no coincide con el declarado; o la concesión de solo lectura otorgada por el anfitrión ha caducado. El mensaje de error indica el motivo concreto, y «Reintentar» repite la inspección.

#### ¿Tardan mucho los archivos grandes?

El plugin lee el archivo secuencialmente una sola vez y calcula las siete sumas en esa única pasada: el tiempo depende de la velocidad de lectura del almacenamiento, no del número de algoritmos. El progreso se muestra en vivo y la inspección puede cancelarse en cualquier momento.

### Permisos y seguridad

El plugin no solicita permisos de almacenamiento ni de red y solo puede acceder al único archivo elegido por el usuario, a través de una URI content temporal de solo lectura concedida por el anfitrión; la concesión caduca al terminar la inspección y ningún otro archivo queda al alcance. Las solicitudes del gestor de archivos se validan campo a campo (identificador de acción, versión de protocolo, forma de la URI content, nombre de archivo, tipo MIME, tamaño declarado y concesiones de solo lectura) y cualquier solicitud con concesiones de escritura o persistentes se rechaza de plano. El archivo se procesa como flujo de solo lectura; la memoria solo retiene búferes acotados, los primeros 4096 bytes para análisis, los primeros 64 para mostrar y una ventana de cuatro bytes para la firma PE, y el original nunca se modifica.

Para que cada inspección sea predecible, el plugin aplica los siguientes límites:

- Un archivo puede ocupar como máximo 8 TiB, y cada acción procesa exactamente un archivo objetivo.
- El análisis toma como máximo los primeros 4096 bytes más una ventana de cuatro bytes para la firma PE, mientras la cabecera mostrada sigue limitada a 64 bytes; la entrada admite hasta 512 caracteres y solo acepta caracteres no ASCII en el nombre de archivo de coreutils.
- Si el tamaño realmente leído difiere del declarado, el archivo se considera modificado y la inspección falla con un mensaje.
- La comparación de sumas usa comparación en tiempo constante de valores de igual longitud; MD5 y SHA-1 son solo para cotejar datos antiguos y no deben considerarse resistentes a colisiones.

### Interfaz del plugin

El anfitrión (AutoJs6) descubre e invoca el plugin mediante las siguientes identidades, indicadas aquí para desarrolladores de plugins y de anfitriones:

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

La versión actual aporta al gestor de archivos del anfitrión una acción de menú de solo lectura sobre un único archivo; nunca modifica el archivo original ni enumera directorios. Si el plugin falta o está desactivado, el anfitrión recurre en silencio a su comportamiento predeterminado.

### Roadmap

Las capacidades anteriores y los elementos marcados de la Roadmap reflejan lo implementado; el trabajo futuro, como algoritmos que requieren proveedor, verificación por lotes y extensiones del protocolo del host, se registra allí, y los elementos sin marcar no son capacidades actuales.

- [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/ROADMAP.md)

### Historial de versiones

#### v1.0.1

_2026/08/08_

- `Corrección` Corregido el fallo por el que el anfitrión no podía enlazar el servicio del plugin tras activarlo en el centro de plugins; la acción «Inspeccionar archivo» queda disponible inmediatamente después de activarlo
- `Mejora` Nombre y descripción del plugin simplificados para que la documentación de usuario se lea con más naturalidad

#### v1.0.0

_2026/08/02_

- `Aviso` Primera versión pública; requiere AutoJs6 con código de versión 5268 o superior
- `Función` Añadida la acción de solo lectura «Inspeccionar archivo» al menú de archivos del gestor de AutoJs6, válida para archivos normales de cualquier tipo (ID de plugin `file-inspector`, ID de acción `inspect-file`)
- `Función` Una única lectura secuencial calcula a la vez las sumas CRC32, MD5, SHA-1, SHA-256, SHA-512, con progreso en vivo, cancelación y reintento
- `Función` Pegado de una suma esperada para verificar la integridad: algoritmo detectado por longitud o por prefijos como `sha256:`, se aceptan la notación de huella `AB:CD:EF` y el prefijo `0x` de CRC32, y la comparación es en tiempo constante sobre valores de igual longitud
- `Función` El informe muestra el nombre del archivo, tipo MIME, extensión, tamaños declarado y real, una instantánea hex + ASCII de los primeros 64 bytes y la detección de BOM UTF
- `Función` Reconocimiento de 10 formatos comunes por los bytes de firma iniciales: ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
- `Función` Cada suma puede copiarse por separado, el informe completo puede copiarse o compartirse mediante el panel del sistema, y MD5 y SHA-1 llevan la insignia Legacy
- `Función` El plugin no solicita permisos de almacenamiento ni de red y lee los archivos solo a través de la URI content temporal de solo lectura concedida por el anfitrión, hasta 8 TiB por archivo
- `Función` Incluye textos de interfaz, instrucciones, README y CHANGELOG en 10 idiomas: chino simplificado, chino tradicional (Hong Kong), chino tradicional (Taiwán), inglés, francés, español, japonés, coreano, ruso y árabe
- `Dependencia` Incorporada AndroidX Lifecycle ViewModel 2.9.4

##### Historial completo

- [CHANGELOG-es.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

### Compilación

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilación release:

```powershell
.\gradlew.bat :app:appendDigestToReleasedFiles
.\gradlew.bat :app:verifyReleaseChecksums
```

Los parámetros de compilación y firma provienen de version.properties y sign.properties; el mínimo actual es Android 7.0 (SDK 24) con SDK objetivo 36.

Los README, CHANGELOG y las instrucciones integradas en res/raw*/plugin_instruction.md se generan con .python/generate_markdown.py a partir de las fuentes JSON y plantillas de .readme/ y .changelog/ (10 idiomas). Para cambiar la documentación, edita las fuentes JSON y vuelve a ejecutar el script en lugar de editar el Markdown generado.

### Enlaces

- Documentación de AutoJs6: https://docs.autojs6.com
- Compartición segura de archivos en Android: https://developer.android.com/training/secure-file-sharing
