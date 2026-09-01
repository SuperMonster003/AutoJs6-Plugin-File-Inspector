# File Inspector

## Presentación

File Inspector es un plugin complementario del gestor de archivos de AutoJs6. Elige «Inspeccionar archivo» sobre cualquier archivo: el plugin lo lee una sola vez y calcula a la vez las sumas CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384 y SHA-512, mostrando además en una sola pantalla la firma de formato real del archivo, su tamaño y sus primeros bytes. Todo es de solo lectura: el plugin no solicita permisos de almacenamiento ni de red y nunca modifica el archivo original.

La página del informe incluye una «Comprobación de integridad»: pega la suma publicada por el proveedor del archivo y el plugin detecta el algoritmo automáticamente y responde con un veredicto claro de coincide / no coincide, sin volver a comparar largas cadenas hexadecimales a ojo. Cada suma y la instantánea de la cabecera pueden copiarse por separado; elige Markdown o JSON antes de copiar el informe completo o enviarlo por el panel de compartir del sistema.

## Puntos destacados

- Una lectura, todos los resultados: el archivo se lee secuencialmente una única vez mientras las siete sumas se calculan a la vez; los archivos grandes nunca esperan varias pasadas.
- Pegar para verificar: admite hexadecimal y huellas, Base64, SRI y líneas completas de `md5sum` / `sha256sum`; detecta el algoritmo automáticamente y avisa si el nombre de archivo pegado es distinto.
- Detección del formato real: reconoce 26 firmas comunes en una cabecera acotada o en posiciones fijas, añade una pista de uso para documentos APK/JAR/Office basados en ZIP y estima texto o binario mediante la proporción imprimible y la entropía.
- Cabecera de un vistazo: muestra los primeros 64 bytes del archivo en vista hexadecimal + ASCII y detecta las marcas de orden de bytes (BOM) UTF-8 / UTF-16 / UTF-32.
- Doble comprobación de tamaño: muestra el tamaño declarado por el gestor de archivos y el tamaño realmente leído; si el archivo cambia durante la lectura, la inspección falla de inmediato, delatando descargas incompletas y archivos aún en escritura.
- Progreso bajo control: los archivos grandes muestran bytes leídos, velocidad y tiempo restante estimado; la lectura puede cancelarse en cualquier momento y una inspección fallida se reintenta con un toque.
- Resultados listos para usar: copia por separado cualquier suma o la instantánea de la cabecera y exporta el resultado completo en Markdown o JSON al portapapeles o al panel de compartir, sin crear archivos.
- Algoritmos antiguos señalados: MD5 y SHA-1 llevan una insignia Legacy como recordatorio de que ya no sirven como prueba de seguridad.

## Cómo usarlo

1. Descarga el APK y su archivo de control homónimo `.apk.sha256` desde la misma Release oficial, instala el APK y actívalo en el centro de plugins de AutoJs6 (se requiere AutoJs6 con código de versión 5268 o superior).
2. Antes de actualizar, inspecciona el APK descargado con File Inspector ya instalado; en una primera instalación, conserva el APK e inspecciónalo después de activar el plugin. Pega el SHA-256 de 64 caracteres del archivo de control y exige una coincidencia verde; obtén ambos archivos de la misma página Release HTTPS de confianza.
3. Abre el gestor de archivos de AutoJs6 y localiza el archivo que quieras inspeccionar; vale cualquier archivo normal.
4. Elige «Inspeccionar archivo» en el menú del archivo; la lectura empieza al momento con progreso en vivo, y al terminar aparecen las sumas, la firma de formato y los bytes de cabecera.
5. Para verificar la integridad, pega la suma publicada en el campo «Comprobación de integridad» y toca «Verificar»: verde significa que coincide, rojo que no.
6. Toca el botón de copia junto a una suma o bajo la instantánea de la cabecera para obtener un valor; elige Markdown o JSON antes de copiar o compartir el informe completo y pulsa atrás para volver al gestor de archivos.

> La entrada admite hexadecimal simple, prefijos como `sha256: <valor>` o `MD5=<valor>`, huellas `AB:CD:EF`, CRC32 con `0x`, Base64 estándar, SRI como `sha256-<base64>` y líneas completas de coreutils `<hex>  <archivo>` o `<hex> *<archivo>`. Se ignoran mayúsculas y espacios exteriores; el algoritmo se infiere cuando la longitud es única y un nombre de archivo distinto genera un aviso.

## Permisos y seguridad

El plugin no solicita permisos de almacenamiento ni de red y solo puede acceder al único archivo elegido por el usuario, a través de una URI content temporal de solo lectura concedida por el anfitrión; la concesión caduca al terminar la inspección y ningún otro archivo queda al alcance. Las solicitudes del gestor de archivos se validan campo a campo (identificador de acción, versión de protocolo, forma de la URI content, nombre de archivo, tipo MIME, tamaño declarado y concesiones de solo lectura) y cualquier solicitud con concesiones de escritura o persistentes se rechaza de plano. El archivo se procesa como flujo de solo lectura; la memoria solo retiene búferes acotados, los primeros 4096 bytes para análisis, los primeros 64 para mostrar y una ventana de cuatro bytes para la firma PE, y el original nunca se modifica.

Para que cada inspección sea predecible, el plugin aplica los siguientes límites:

- Un archivo puede ocupar como máximo 8 TiB, y cada acción procesa exactamente un archivo objetivo.
- El análisis toma como máximo los primeros 4096 bytes más una ventana de cuatro bytes para la firma PE, mientras la cabecera mostrada sigue limitada a 64 bytes; la entrada admite hasta 512 caracteres y solo acepta caracteres no ASCII en el nombre de archivo de coreutils.
- Si el tamaño realmente leído difiere del declarado, el archivo se considera modificado y la inspección falla con un mensaje.
- La comparación de sumas usa comparación en tiempo constante de valores de igual longitud; MD5 y SHA-1 son solo para cotejar datos antiguos y no deben considerarse resistentes a colisiones.
