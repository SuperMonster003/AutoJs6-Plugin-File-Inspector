File Inspector examina cualquier archivo del gestor de archivos de AutoJs6. Lee el archivo una sola vez para calcular siete sumas de comprobación, como CRC32, MD5, SHA-1 y SHA-256, y muestra el formato, el tamaño y la cabecera del archivo. Pega la suma publicada por el proveedor para comprobar si el archivo está íntegro. Solo lee el archivo y nunca modifica el original.

### Primeros pasos

1. Comprueba que este plugin esté activado en el "Centro de plugins" de AutoJs6.
2. Abre el gestor de archivos de AutoJs6 y busca el archivo que quieres comprobar.
3. Elige "Inspeccionar archivo" en el menú del archivo (en AutoJs6 6.8.1 y posteriores está en el menú de primer nivel "File Inspector").
4. Cuando termine la lectura, revisa "Información del archivo", "Sumas de comprobación" y "Cabecera del archivo" en el informe.

### Funciones principales

- **Sumas de comprobación**: calcula CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384 y SHA-512 en una sola lectura; MD5 y SHA-1 llevan la marca "Heredado" y no sirven como prueba de seguridad.
- **Comprobación de integridad**: pega la suma publicada en "Suma esperada" y toca "Verificar"; el verde indica que coincide y el rojo que no. También acepta una línea completa de la salida de `sha256sum`.
- **Información del archivo**: muestra el formato detectado, la extensión y el tipo MIME, y estima si el contenido es texto o binario; en los archivos ZIP indica además si se trata de un APK, un JAR o un documento de Office.
- **Cabecera del archivo**: muestra los primeros 64 bytes del archivo en hexadecimal y ASCII lado a lado, y detecta las marcas de orden de bytes (BOM) de UTF-8, UTF-16 y UTF-32.
- **Comprobación del tamaño**: muestra el tamaño declarado y el tamaño real; si el archivo cambia durante la lectura, aparece un error, lo que ayuda a detectar descargas incompletas o archivos que aún se están escribiendo.
- **Copiar y compartir**: copia cada suma o la cabecera por separado; tras elegir Markdown o JSON en "Formato del informe", usa "Copiar informe" o "Compartir" para enviar el informe completo sin crear ningún archivo.
- **Progreso de lectura**: con archivos grandes se muestran el progreso, la velocidad y el tiempo restante, y puedes "Cancelar" en cualquier momento; si la lectura falla, toca "Reintentar".

### Requisitos

- Android 7.0 o posterior.
- AutoJs6 6.8.0 o posterior.
- No necesita permisos de almacenamiento ni de red; el plugin solo lee el archivo que seleccionaste en el gestor de archivos.

### Preguntas frecuentes

**Qué hacer si la suma de comprobación no coincide**

Comprueba primero que la suma pegada proceda de la página de publicación de este mismo archivo. Si sigue sin coincidir, el archivo es distinto del original publicado: puede que la descarga esté incompleta o que el archivo se haya modificado. Descárgalo de nuevo desde la fuente oficial y vuelve a comprobarlo.

**Qué significa que la suma coincida**

Que coincida solo indica que el archivo tiene el mismo contenido que aquel para el que se publicó la suma; que el archivo sea de confianza depende del origen de la suma. Usa preferentemente valores SHA-256 o SHA-512 publicados por HTTPS en la fuente oficial.

**Por qué aparece un aviso sobre el nombre de archivo al pegar una línea completa**

Al pegar una línea completa de una herramienta como `sha256sum`, el plugin compara el nombre de archivo de esa línea. Si no coincide con el nombre del archivo actual, un aviso te recuerda que confirmes que la suma corresponde a este archivo.

**Por qué algunos archivos no se pueden inspeccionar**

Las causas habituales son un archivo de más de 8 TiB, un archivo que otra aplicación modifica durante la lectura o un permiso de lectura del gestor de archivos que ha caducado. El mensaje de error indica la causa; toca "Reintentar" para comprobarlo de nuevo.

Más detalles en la [página del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector).
