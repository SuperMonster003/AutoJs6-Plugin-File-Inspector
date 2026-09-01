# Historial de versiones

## v1.0.1

_2026/08/08_

- `Corrección` Corregido el fallo por el que el anfitrión no podía enlazar el servicio del plugin tras activarlo en el centro de plugins; la acción «Inspeccionar archivo» queda disponible inmediatamente después de activarlo
- `Mejora` Nombre y descripción del plugin simplificados para que la documentación de usuario se lea con más naturalidad

## v1.0.0

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
