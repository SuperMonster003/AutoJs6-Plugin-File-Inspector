******

### Historique des versions

******

# v1.0.1

###### 2026/08/08

* `Correctif` Liaison de service nulle lors de l'activation du plugin dans le centre des plugins
* `Amélioration` Nom, description et documentation utilisateur plus clairs et concis

# v1.0.0

###### 2026/08/02

* `Fonctionnalité` Plugin File Inspector avec l'ID de plugin `file-inspector`, l'ID d'action `inspect-file`, le moteur `explorer-action` et la variante `default`
* `Fonctionnalité` Action de l'explorateur en lecture seule pour un seul fichier ordinaire lisible, avec une limite d'entrée de 8 TiB et sans autorisation de stockage ou de réseau
* `Fonctionnalité` Calcul de CRC32, MD5, SHA-1, SHA-256 et SHA-512 en une seule lecture avec progression et annulation
* `Fonctionnalité` Normalisation et vérification strictes de l'empreinte attendue avec déduction de l'algorithme, préfixes explicites et comparaison en temps constant des octets de même longueur
* `Fonctionnalité` Instantané d'en-tête de 64 octets en hexadécimal et ASCII, détection du BOM et reconnaissance des signatures ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX et SQLite 3
* `Fonctionnalité` Métadonnées, textes de l'interface, instructions, fichiers README et historiques localisés en espagnol, français, russe, arabe, japonais, coréen, anglais, chinois simplifié, chinois traditionnel de Hong Kong et chinois traditionnel de Taïwan
* `Dépendance` Ajout de AndroidX Lifecycle ViewModel version 2.9.4
