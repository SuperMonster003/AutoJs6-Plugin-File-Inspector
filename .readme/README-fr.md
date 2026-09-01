<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="File Inspector" width="128" />
  </p>

  <h1>File Inspector</h1>

  <p>Plugin du gestionnaire de fichiers AutoJs6 : sept sommes de contrôle en une seule lecture, collez la valeur publiée pour vérifier l'intégrité aussitôt, et découvrez le vrai format du fichier d'un coup d'œil</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml/badge.svg"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

### Langues (Languages)

Ce README est disponible dans les langues suivantes:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- Français [fr] # actuel
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

### Présentation

File Inspector est un plugin compagnon du gestionnaire de fichiers AutoJs6. Choisissez « Inspecter le fichier » sur n'importe quel fichier : le plugin ne le lit qu'une seule fois et calcule simultanément les sommes de contrôle CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384 et SHA-512, tout en affichant sur un seul écran la signature de format réelle du fichier, sa taille et ses premiers octets. Tout est en lecture seule : le plugin ne demande aucune autorisation de stockage ni de réseau et ne modifie jamais le fichier d'origine.

La page de rapport intègre un « Contrôle d'intégrité » : collez la somme de contrôle publiée par le fournisseur du fichier, le plugin détecte l'algorithme automatiquement et rend un verdict clair de correspondance ou non, sans plus jamais comparer de longues chaînes hexadécimales à l'œil nu. Chaque somme de contrôle et l'instantané de l'en-tête peuvent être copiés séparément ; choisissez Markdown ou JSON avant de copier le rapport complet ou de l'envoyer via le panneau de partage du système.

### Points forts

- Une lecture, tous les résultats : le fichier est lu séquentiellement une seule fois pendant que les sept sommes de contrôle sont calculées ensemble ; les gros fichiers n'attendent jamais plusieurs passes.
- Coller pour vérifier : accepte l'hexadécimal et les empreintes, Base64, SRI et les lignes complètes de `md5sum` / `sha256sum` ; l'algorithme est détecté automatiquement et un nom de fichier différent déclenche un avertissement.
- Détection du vrai format : reconnaît 26 signatures courantes dans un en-tête borné ou à des positions fixes, précise l'usage des documents APK/JAR/Office fondés sur ZIP et estime texte ou binaire d'après le taux de caractères imprimables et l'entropie.
- En-tête d'un coup d'œil : affiche les 64 premiers octets du fichier en vis-à-vis hexadécimal + ASCII et détecte les marques d'ordre des octets (BOM) UTF-8 / UTF-16 / UTF-32.
- Double contrôle de taille : montre à la fois la taille déclarée par le gestionnaire de fichiers et la taille réellement lue ; si le fichier change pendant la lecture, l'inspection échoue immédiatement, ce qui révèle les téléchargements incomplets et les fichiers en cours d'écriture.
- Progression maîtrisée : les gros fichiers affichent les octets lus, le débit et le temps restant estimé ; la lecture peut être annulée à tout moment et une inspection échouée se relance d'une pression.
- Résultats prêts à l'emploi : copiez séparément une somme de contrôle ou l'instantané de l'en-tête, puis exportez le résultat complet affiché en Markdown ou JSON vers le presse-papiers ou le panneau de partage, sans créer de fichier.
- Algorithmes anciens signalés : MD5 et SHA-1 portent un badge Legacy pour rappeler qu'ils ne conviennent plus comme preuve de sécurité.

### Mode d'emploi

1. Téléchargez l'APK et son fichier de contrôle homonyme `.apk.sha256` depuis la même Release officielle, installez l'APK, puis activez-le dans le centre de plugins d'AutoJs6 (code de version AutoJs6 5268 ou ultérieur requis).
2. Avant une mise à niveau, inspectez l'APK téléchargé avec la version déjà installée de File Inspector ; lors d'une première installation, conservez l'APK et inspectez-le après l'activation du plugin. Collez le SHA-256 de 64 caractères du fichier de contrôle et exigez une correspondance verte ; les deux fichiers doivent provenir de la même page Release HTTPS de confiance.
3. Ouvrez le gestionnaire de fichiers d'AutoJs6 et repérez le fichier à inspecter ; tout fichier ordinaire convient.
4. Choisissez « Inspecter le fichier » dans le menu du fichier ; la lecture démarre aussitôt avec une progression en direct, puis les sommes de contrôle, la signature de format et les octets d'en-tête s'affichent.
5. Pour vérifier l'intégrité, collez la somme de contrôle publiée dans le champ « Contrôle d'intégrité » et touchez « Vérifier » : vert signifie correspondance, rouge signifie écart.
6. Touchez le bouton de copie près d'une somme de contrôle ou sous l'instantané de l'en-tête pour obtenir une valeur ; choisissez Markdown ou JSON avant de copier ou partager le rapport complet, puis revenez au gestionnaire de fichiers.

> Le champ accepte l'hexadécimal brut, les préfixes comme `sha256: <valeur>` ou `MD5=<valeur>`, les empreintes `AB:CD:EF`, CRC32 avec `0x`, Base64 standard, SRI comme `sha256-<base64>`, et les lignes coreutils complètes `<hex>  <fichier>` ou `<hex> *<fichier>`. La casse et les espaces environnants sont ignorés ; l'algorithme est déduit lorsque la longueur est unique, et un nom de fichier différent déclenche un avertissement.

### Signatures de format reconnues

Les sommes de contrôle fonctionnent sur tout fichier ordinaire lisible ; en complément, la version actuelle reconnaît les signatures suivantes dans un en-tête borné ou à des positions fixes:

```text
ZIP, 7z, RAR 4, RAR 5, GZIP, XZ, BZIP2, Zstandard, LZ4, TAR, PDF, PNG, JPEG, GIF87a, GIF89a, WebP, MP4 / ISO-BMFF, EBML / Matroska, ELF, DEX, Java Class, Mach-O, PE, SQLite 3, WOFF, WOFF2
```

La détection utilise un échantillon borné et des champs structuraux, tels que le marqueur TAR à l'offset 257, `ftyp` d'ISO-BMFF à l'offset 4 et le pointeur d'en-tête PE. Il s'agit d'un indice rapide, pas d'une validation complète ; le taux de caractères imprimables et l'entropie sont également des estimations heuristiques. Les fichiers sans signature correspondante affichent « Inconnu » et leurs sommes sont calculées normalement.

### FAQ

#### Quand ce plugin est-il utile ?

Le scénario classique est la vérification des téléchargements : après avoir récupéré un installateur, un micrologiciel ou un document, collez la somme de contrôle SHA-256 (ou autre) publiée par le fournisseur et sachez immédiatement si le fichier est complet et intact. Il sert aussi à révéler le vrai format de fichiers à l'extension trompeuse, ou à produire rapidement des sommes de contrôle de n'importe quel fichier pour archivage et comparaison.

#### Une correspondance de somme de contrôle prouve-t-elle que le fichier est sûr ?

Une correspondance prouve seulement que le fichier est identique octet pour octet à celui pour lequel la somme a été publiée ; la confiance dépend de la provenance de cette somme. Privilégiez les valeurs SHA-256 ou SHA-512 publiées en HTTPS par la source officielle. Des collisions MD5 et SHA-1 peuvent être fabriquées délibérément, c'est pourquoi le plugin les marque Legacy : ne les considérez pas comme une preuve de sécurité.

#### Pourquoi certains fichiers ne peuvent-ils pas être inspectés ?

Raisons courantes : le fichier dépasse la limite de 8 TiB ; le fichier a été modifié par une autre application pendant la lecture, si bien que la taille réelle ne correspond plus à la taille déclarée ; ou l'autorisation en lecture seule accordée par l'hôte a expiré. Le message d'erreur précise la raison, et « Réessayer » relance l'inspection.

#### Les gros fichiers sont-ils longs à inspecter ?

Le plugin lit le fichier séquentiellement une seule fois et calcule les sept sommes de contrôle pendant cette unique passe : la durée dépend de la vitesse de lecture du stockage, pas du nombre d'algorithmes. La progression s'affiche en continu et l'inspection peut être annulée à tout moment.

### Autorisations et sécurité

Le plugin ne demande aucune autorisation de stockage ni de réseau et n'atteint que le seul fichier choisi par l'utilisateur, via une URI content temporaire en lecture seule accordée par l'hôte ; l'autorisation expire à la fin de l'inspection et aucun autre fichier n'est accessible. Les requêtes du gestionnaire de fichiers sont validées champ par champ — identifiant d'action, version de protocole, forme de l'URI content, nom de fichier, type MIME, taille déclarée et autorisations en lecture seule — et toute requête portant une autorisation d'écriture ou persistante est rejetée d'emblée. Le fichier est traité en flux et en lecture seule ; la mémoire ne conserve que des tampons bornés, les 4096 premiers octets pour l'analyse, les 64 premiers pour l'affichage et une fenêtre de quatre octets pour la signature PE, sans jamais modifier le fichier source.

Pour garder chaque inspection prévisible, le plugin applique les bornes suivantes:

- Un fichier ne peut dépasser 8 TiB, et chaque action traite exactement un fichier cible.
- L'analyse échantillonne au plus les 4096 premiers octets, plus une fenêtre de signature PE de quatre octets, tandis que l'en-tête affiché reste limité à 64 octets ; la saisie est limitée à 512 caractères, les caractères non ASCII n'étant admis que dans un nom de fichier coreutils.
- Si la taille réellement lue diffère de la taille déclarée, le fichier est considéré comme modifié et l'inspection échoue avec un message.
- La comparaison des sommes de contrôle s'effectue en temps constant sur des valeurs de même longueur ; MD5 et SHA-1 ne servent qu'à contrôler des données anciennes et ne doivent pas être tenus pour résistants aux collisions.

### Interface du plugin

L'hôte (AutoJs6) découvre et invoque le plugin via les identités suivantes, fournies pour les développeurs de plugins et d'hôtes:

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

La version actuelle ajoute au gestionnaire de fichiers de l'hôte une action de menu en lecture seule portant sur un seul fichier ; elle ne modifie jamais le fichier source et n'énumère jamais de répertoires. Si le plugin est absent ou désactivé, l'hôte revient silencieusement à son comportement par défaut.

### Roadmap

Les capacités ci-dessus et les éléments cochés de la Roadmap reflètent l'existant ; les travaux futurs, tels que les algorithmes nécessitant un fournisseur, la vérification par lots et les extensions du protocole hôte, y sont suivis, et les éléments non cochés ne sont pas des capacités actuelles.

- [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/ROADMAP.md)

### Historique des versions

#### v1.0.1

_2026/08/08_

- `Correctif` Correction de l'échec de liaison du service par l'hôte après activation dans le centre de plugins ; l'action « Inspecter le fichier » est désormais disponible dès l'activation
- `Amélioration` Nom et description du plugin allégés pour une documentation utilisateur plus naturelle à lire

#### v1.0.0

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

##### Historique complet

- [CHANGELOG-fr.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

### Compilation

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilation release:

```powershell
.\gradlew.bat :app:appendDigestToReleasedFiles
.\gradlew.bat :app:verifyReleaseChecksums
```

Les paramètres de compilation et de signature proviennent de version.properties et sign.properties ; le minimum actuel est Android 7.0 (SDK 24) avec un SDK cible 36.

Les README, CHANGELOG et instructions intégrées sous res/raw*/plugin_instruction.md sont générés par .python/generate_markdown.py à partir des sources JSON et des modèles de .readme/ et .changelog/ (10 langues). Pour modifier la documentation, éditez les sources JSON puis relancez le script au lieu de modifier le Markdown généré.

### Liens

- Documentation AutoJs6: https://docs.autojs6.com
- Partage de fichiers sécurisé Android: https://developer.android.com/training/secure-file-sharing
