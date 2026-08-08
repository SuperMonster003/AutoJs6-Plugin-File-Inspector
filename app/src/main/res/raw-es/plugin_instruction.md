# Inspector de archivos

El Inspector de archivos examina un archivo con una sola lectura en flujo. Calcula las sumas de comprobación CRC32, MD5, SHA-1, SHA-256 y SHA-512, identifica firmas de archivo comunes e informa de los metadatos básicos.

MD5 y SHA-1 se marcan como algoritmos heredados. Pega una suma de comprobación esperada para verificarla. También puedes copiar una suma de comprobación individual, o copiar y compartir el informe de inspección completo.

Se requiere la compilación 5268+ del host.

Límites de seguridad y privacidad:

- Se rechazan los archivos de más de 8 TiB.
- El contenido del archivo se abre una sola vez y se procesa secuencialmente en modo de solo lectura.
- Solo se inspeccionan los primeros 64 bytes como cabecera del archivo.
- El complemento no accede a la red ni solicita permiso de almacenamiento.
