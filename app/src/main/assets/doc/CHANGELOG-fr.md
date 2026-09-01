# Historique des versions

## v1.0.1

_2026/08/08_

- `Correctif` Correction de l'échec de liaison du service par l'hôte après activation dans le centre de plugins ; l'action « Inspecter le fichier » est désormais disponible dès l'activation
- `Amélioration` Nom et description du plugin allégés pour une documentation utilisateur plus naturelle à lire

## v1.0.0

_2026/08/02_

- `Note` Première version publique ; requiert AutoJs6 avec un code de version 5268 ou ultérieur
- `Fonctionnalité` Ajout de l'action en lecture seule « Inspecter le fichier » au menu des fichiers du gestionnaire AutoJs6, pour les fichiers ordinaires de tout type (ID de plugin `file-inspector`, ID d'action `inspect-file`)
- `Fonctionnalité` Une seule lecture séquentielle calcule ensemble les sommes de contrôle CRC32, MD5, SHA-1, SHA-256, SHA-512, avec progression en direct, annulation et nouvelle tentative
- `Fonctionnalité` Collage d'une somme de contrôle attendue pour vérifier l'intégrité : algorithme détecté par la longueur ou par des préfixes comme `sha256:`, notation d'empreinte `AB:CD:EF` et préfixe `0x` de CRC32 acceptés, comparaison en temps constant sur valeurs de même longueur
- `Fonctionnalité` Le rapport affiche le nom du fichier, le type MIME, l'extension, les tailles déclarée et réelle, un instantané hex + ASCII des 64 premiers octets et la détection des BOM UTF
- `Fonctionnalité` Reconnaissance de 10 formats courants d'après les octets de signature en tête : ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
- `Fonctionnalité` Chaque somme de contrôle peut être copiée individuellement, le rapport complet copié ou partagé via le panneau système, et MD5 et SHA-1 portent un badge Legacy
- `Fonctionnalité` Le plugin ne demande aucune autorisation de stockage ni de réseau et lit les fichiers uniquement via l'URI content temporaire en lecture seule accordée par l'hôte, jusqu'à 8 TiB par fichier
- `Fonctionnalité` Textes d'interface, instructions, README et CHANGELOG fournis en 10 langues : chinois simplifié, chinois traditionnel (Hong Kong), chinois traditionnel (Taïwan), anglais, français, espagnol, japonais, coréen, russe et arabe
- `Dépendance` Introduction d'AndroidX Lifecycle ViewModel 2.9.4
