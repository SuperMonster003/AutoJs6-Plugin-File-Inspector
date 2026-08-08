<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>Plugin de gestionnaire de fichiers. Inspecte les signatures de fichiers et vérifie les sommes de contrôle cryptographiques</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le fichier README.md actuel prend en charge les langues suivantes:

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

******

### Introduction

******

File Inspector inspecte tout fichier ordinaire lisible transmis par le gestionnaire de fichiers via un accès temporaire en lecture seule à un content URI. Il affiche les métadonnées, les 64 premiers octets de l'en-tête et plusieurs empreintes sans modifier le fichier source.

******

### Fonctionnalités

******

- Enregistre une action supplémentaire en lecture seule pour un seul fichier via le protocole partagé `org.autojs.plugin.EXPLORER_ACTION`.
- Lit la source une seule fois tout en calculant CRC32, MD5, SHA-1, SHA-256 et SHA-512 ensemble, avec progression et annulation.
- Affiche la taille déclarée, la taille réelle, le type MIME, l'extension, les 64 premiers octets en hexadécimal et ASCII, le BOM et une signature de fichier reconnue.
- Normalise strictement une empreinte attendue, déduit son algorithme à partir d'une longueur valide ou d'un préfixe explicite et compare les octets de même longueur sans arrêt anticipé à la position différente.
- Copie une somme de contrôle individuelle ou copie et partage le rapport d'inspection complet.

******

### Données inspectées

******

La version 1 calcule les empreintes de tout fichier ordinaire lisible et reconnaît ces signatures d'en-tête fixes au décalage 0:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### Interface du plugin

******

L'hôte découvre et exécute le plugin avec les identités suivantes:

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

La version 1 fournit une action supplémentaire en lecture seule pour un seul fichier dans le gestionnaire de fichiers principal.

La version 5268 ou ultérieure de l'hôte est requise.

******

### Sécurité

******

Le plugin ne demande aucune autorisation de stockage ou de réseau. L'hôte accorde un accès temporaire en lecture seule au content URI cible. Le plugin vérifie précisément l'action Intent, l'URI, ClipData, le nom, le type MIME et la taille déclarée, refuse les droits d'écriture ou persistants et ne modifie jamais la source. Il refuse les différences entre taille déclarée et taille réelle ainsi que les entrées de plus de 8 TiB. Les octets sont traités avec un tampon borné et le rapport ne conserve qu'un instantané d'en-tête de 64 octets.

******

### Limites de sécurité

******

- Taille maximale de l'entrée: `8 TiB`.
- Instantané de l'en-tête: `64 bytes`.
- Texte maximal de l'empreinte attendue: `512 ASCII characters`.
- Un fichier cible par action.
- La détection de signature utilise uniquement des octets fixes au décalage 0 et ne constitue pas une validation complète du format.
- MD5 et SHA-1 sont affichés comme empreintes héritées et ne doivent pas être considérés comme des preuves de sécurité résistantes aux collisions.

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

##### Pour consulter davantage de versions

* [CHANGELOG-fr.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

******

### Compilation

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilation Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Les paramètres de compilation proviennent de `version.properties`. Le SDK minimal actuel est 24 et le SDK cible est 36.

******

### Structure des ressources

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localise les métadonnées du plugin et le texte de l'interface. `plugin_instruction.md` fournit les instructions visibles depuis l'hôte. `.python/generate_markdown.py` génère les fichiers README et les historiques localisés depuis les sources JSON.

******

### Liens

******

- Documentation AutoJs6: https://docs.autojs6.com
- Partage sécurisé de fichiers Android: https://developer.android.com/training/secure-file-sharing
