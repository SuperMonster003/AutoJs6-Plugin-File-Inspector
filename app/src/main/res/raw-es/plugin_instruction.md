# Inspector de archivos

El Inspector de archivos examina un archivo con una sola lectura en flujo. Calcula las sumas de comprobación CRC32, MD5, SHA-1, SHA-256 y SHA-512, identifica firmas de archivo comunes e informa de los metadatos básicos.

MD5 y SHA-1 se marcan como algoritmos heredados. Pega una suma de comprobación esperada para verificarla. También puedes copiar una suma de comprobación individual, o copiar y compartir el informe de inspección completo.

El complemento requiere AutoJs6 build 5268+. Está implementado por completo en la JVM y no depende de la ABI del dispositivo.

Límites de seguridad y privacidad:

- Se rechazan los archivos de más de 8 TiB.
- El contenido del archivo se abre una sola vez y se procesa secuencialmente en modo de solo lectura.
- Solo se inspeccionan los primeros 64 bytes como cabecera del archivo.
- El complemento no accede a la red ni solicita permiso de almacenamiento.
