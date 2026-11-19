File Inspector examine n'importe quel fichier du gestionnaire de fichiers d'AutoJs6. Il lit le fichier une seule fois pour calculer sept sommes de contrôle, dont CRC32, MD5, SHA-1 et SHA-256, et affiche le format, la taille et l'en-tête du fichier. Collez la somme publiée par le fournisseur pour vérifier que le fichier est intact. Le fichier est uniquement lu, jamais modifié.

### Prise en main

1. Vérifiez que ce plugin est activé dans le "Centre des plugins" d'AutoJs6.
2. Ouvrez le gestionnaire de fichiers d'AutoJs6 et repérez le fichier à vérifier.
3. Choisissez "Inspecter le fichier" dans le menu du fichier (dans AutoJs6 6.8.1 et versions ultérieures, il se trouve dans le menu de premier niveau "File Inspector").
4. Une fois la lecture terminée, consultez "Informations sur le fichier", "Sommes de contrôle" et "En-tête du fichier" dans le rapport.

### Fonctionnalités principales

- **Sommes de contrôle**: calcule CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384 et SHA-512 en une seule lecture; MD5 et SHA-1 sont marqués "Hérité" et ne constituent pas une preuve de sécurité.
- **Contrôle d'intégrité**: collez la somme publiée dans "Somme attendue" puis touchez "Vérifier"; le vert indique une correspondance et le rouge une différence. Une ligne complète produite par `sha256sum` est aussi acceptée.
- **Informations sur le fichier**: affiche le format détecté, l'extension et le type MIME, et estime si le contenu est du texte ou du binaire; pour un fichier ZIP, indique aussi s'il s'agit d'un APK, d'un JAR ou d'un document Office.
- **En-tête du fichier**: affiche les 64 premiers octets du fichier en hexadécimal et en ASCII côte à côte, et détecte les indicateurs d'ordre des octets (BOM) UTF-8, UTF-16 et UTF-32.
- **Contrôle de la taille**: affiche la taille déclarée et la taille réelle; si le fichier change pendant la lecture, une erreur s'affiche, ce qui aide à repérer un téléchargement incomplet ou un fichier en cours d'écriture.
- **Copie et partage**: copiez chaque somme ou l'en-tête séparément; après avoir choisi Markdown ou JSON dans "Format du rapport", utilisez "Copier le rapport" ou "Partager" pour envoyer le rapport complet sans créer de fichier.
- **Progression de la lecture**: pour les gros fichiers, la progression, la vitesse et le temps restant s'affichent, et vous pouvez "Annuler" à tout moment; en cas d'échec, touchez "Réessayer".

### Prérequis

- Android 7.0 ou version ultérieure.
- AutoJs6 6.8.0 ou version ultérieure.
- Aucune autorisation de stockage ou de réseau n'est nécessaire; le plugin lit uniquement le fichier sélectionné dans le gestionnaire de fichiers.

### Questions fréquentes

**Que faire si la somme de contrôle ne correspond pas**

Vérifiez d'abord que la somme collée provient de la page de publication de ce fichier précis. Si elle ne correspond toujours pas, le fichier diffère de l'original publié: le téléchargement est peut-être incomplet ou le fichier a été modifié. Téléchargez-le de nouveau depuis la source officielle, puis vérifiez-le encore.

**Ce que signifie une correspondance**

Une correspondance indique seulement que le fichier a le même contenu que celui pour lequel la somme a été publiée; la confiance accordée au fichier dépend de la provenance de la somme. Privilégiez les valeurs SHA-256 ou SHA-512 publiées en HTTPS par la source officielle.

**Pourquoi un avertissement sur le nom de fichier apparaît après le collage d'une ligne complète**

Quand vous collez une ligne complète produite par un outil comme `sha256sum`, le plugin compare le nom de fichier de cette ligne. S'il diffère du nom du fichier actuel, un avertissement vous invite à confirmer que la somme concerne bien ce fichier.

**Pourquoi certains fichiers ne peuvent pas être inspectés**

Les causes courantes sont un fichier de plus de 8 TiB, un fichier modifié par une autre application pendant la lecture, ou une autorisation de lecture du gestionnaire de fichiers qui a expiré. Le message d'erreur indique la cause; touchez "Réessayer" pour relancer la vérification.

Plus de détails sur la [page du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector).
