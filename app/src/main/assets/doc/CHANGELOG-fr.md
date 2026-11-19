# Historique des versions

## v1.1.5

###### 2026/11/20

* `Amélioration` L'historique des versions est désormais une page à part au lieu d'une boîte de dialogue affichant le texte brut, et se développe ou se réduit par version: une version réduite affiche sa date et le nombre d'entrées par catégorie, une version développée ne nomme chaque catégorie qu'une fois; toutes les versions peuvent être développées ou réduites d'un coup, la plus récente s'ouvre développée et le sous-titre affiche le nom du plugin
* `Amélioration` Réécriture des instructions affichées dans le Centre des plugins en quatre parties: prise en main, fonctionnalités principales, prérequis et questions fréquentes
* `Dépendance` Ajout de Jetpack Compose BOM 2026.09.00 (page Historique des versions)
* `Dépendance` Mise à niveau des plugins de compilation partagés version 1.9.0 -> 1.9.1

## v1.1.4

###### 2026/11/18

* `Amélioration` Icône du plugin redessinée en un motif monochrome sur fond transparent, bien lisible avec les thèmes clair et sombre

## v1.1.3

###### 2026/11/07

* `Amélioration` Dans le menu de fichier d'AutoJs6 6.8.1 et versions ultérieures, "Inspecter le fichier" se trouve dans le menu de premier niveau "File Inspector" (inchangé dans les versions antérieures d'AutoJs6)

## v1.1.2

###### 2026/11/04

* `Amélioration` Les icônes des informations d'application Android utilisent les illustrations et les fonds clairs et sombres d'Icon Studio, en conservant les images transparentes du centre de plugins et les choix du lanceur

## v1.1.1

###### 2026/09/19

* `Correctif` Avertissements de lecture SDK XML v4 avec AGP 9.1 et contrôles d'alignement natif des APK déclenchés par erreur lors de l'assemblage des tests unitaires JVM, avec les plugins de compilation partagés 1.8.3
* `Amélioration` compileSdk et targetSdk passent à 37 (Android 17) ; le comportement du plugin ne dépend pas de la nouvelle cible

## v1.1.0

###### 2026/09/13

* `Fonctionnalité` Historique local accessible depuis l'interface, avec traductions et repli en anglais
* `Amélioration` Vérification de la signature complète, des APK attendus et de la reproductibilité de la documentation

## v1.0.1

###### 2026/09/11

* `Correctif` Correction de l'échec de liaison du service par l'hôte après activation dans le centre de plugins ; l'action « Inspecter le fichier » est désormais disponible dès l'activation
* `Amélioration` Nom et description du plugin allégés pour une documentation utilisateur plus naturelle à lire
* `Amélioration` La vérification de compilation rejette les dépendances natives involontaires et produit un rapport JSON

## v1.0.0

###### 2026/08/02

* `Note` Première version publique ; requiert AutoJs6 avec un code de version 5268 ou ultérieur
* `Fonctionnalité` Ajout de l'action en lecture seule « Inspecter le fichier » au menu des fichiers du gestionnaire AutoJs6, pour les fichiers ordinaires de tout type (ID de plugin `file-inspector`, ID d'action `inspect-file`)
* `Fonctionnalité` Une seule lecture séquentielle calcule ensemble les sommes de contrôle CRC32, MD5, SHA-1, SHA-256, SHA-512, avec progression en direct, annulation et nouvelle tentative
* `Fonctionnalité` Collage d'une somme de contrôle attendue pour vérifier l'intégrité : algorithme détecté par la longueur ou par des préfixes comme `sha256:`, notation d'empreinte `AB:CD:EF` et préfixe `0x` de CRC32 acceptés, comparaison en temps constant sur valeurs de même longueur
* `Fonctionnalité` Le rapport affiche le nom du fichier, le type MIME, l'extension, les tailles déclarée et réelle, un instantané hex + ASCII des 64 premiers octets et la détection des BOM UTF
* `Fonctionnalité` Reconnaissance de 10 formats courants d'après les octets de signature en tête : ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
* `Fonctionnalité` Chaque somme de contrôle peut être copiée individuellement, le rapport complet copié ou partagé via le panneau système, et MD5 et SHA-1 portent un badge Legacy
* `Fonctionnalité` Le plugin ne demande aucune autorisation de stockage ni de réseau et lit les fichiers uniquement via l'URI content temporaire en lecture seule accordée par l'hôte, jusqu'à 8 TiB par fichier
* `Fonctionnalité` Textes d'interface, instructions, README et CHANGELOG fournis en 10 langues : chinois simplifié, chinois traditionnel (Hong Kong), chinois traditionnel (Taïwan), anglais, français, espagnol, japonais, coréen, russe et arabe
* `Dépendance` Introduction d'AndroidX Lifecycle ViewModel 2.9.4
