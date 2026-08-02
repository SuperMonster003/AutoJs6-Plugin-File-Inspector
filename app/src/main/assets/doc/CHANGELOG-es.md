******

### Historial de versiones

******

# v1.0.0

###### 2026/08/02

* `Función` Plugin File Inspector con ID de plugin `file-inspector`, ID de acción `inspect-file`, motor `explorer-action` y variante `default`
* `Función` Acción de Explorador de solo lectura para un único archivo normal legible, con límite de entrada de 8 TiB y sin permisos de almacenamiento ni de red
* `Función` Cálculo de CRC32, MD5, SHA-1, SHA-256 y SHA-512 en una sola lectura con progreso y cancelación
* `Función` Normalización y verificación estrictas del resumen esperado con deducción del algoritmo, prefijos explícitos y comparación en tiempo constante de bytes de igual longitud
* `Función` Captura de cabecera de 64 bytes en hexadecimal y ASCII, detección de BOM y reconocimiento de firmas ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX y SQLite 3
* `Función` Implementación JVM pura sin bibliotecas nativas, ABI sin restricciones mediante `supportedAbis = emptyArray()`, un APK independiente de ABI y compilación de host AutoJs6 5268 requerida
* `Función` Metadatos, texto de interfaz, instrucciones de uso, archivos README e historiales localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán
* `Dependencia` Añadido AndroidX Lifecycle ViewModel versión 2.9.4
