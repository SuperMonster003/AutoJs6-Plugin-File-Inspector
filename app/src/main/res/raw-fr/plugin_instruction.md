# File Inspector

## Présentation

File Inspector est un plugin compagnon du gestionnaire de fichiers AutoJs6. Choisissez « Inspecter le fichier » sur n'importe quel fichier : le plugin ne le lit qu'une seule fois et calcule simultanément les sommes de contrôle CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384 et SHA-512, tout en affichant sur un seul écran la signature de format réelle du fichier, sa taille et ses premiers octets. Tout est en lecture seule : le plugin ne demande aucune autorisation de stockage ni de réseau et ne modifie jamais le fichier d'origine.

La page de rapport intègre un « Contrôle d'intégrité » : collez la somme de contrôle publiée par le fournisseur du fichier, le plugin détecte l'algorithme automatiquement et rend un verdict clair de correspondance ou non, sans plus jamais comparer de longues chaînes hexadécimales à l'œil nu. Chaque somme de contrôle et l'instantané de l'en-tête peuvent être copiés séparément ; choisissez Markdown ou JSON avant de copier le rapport complet ou de l'envoyer via le panneau de partage du système.

## Points forts

- Une lecture, tous les résultats : le fichier est lu séquentiellement une seule fois pendant que les sept sommes de contrôle sont calculées ensemble ; les gros fichiers n'attendent jamais plusieurs passes.
- Coller pour vérifier : accepte l'hexadécimal et les empreintes, Base64, SRI et les lignes complètes de `md5sum` / `sha256sum` ; l'algorithme est détecté automatiquement et un nom de fichier différent déclenche un avertissement.
- Détection du vrai format : reconnaît 26 signatures courantes dans un en-tête borné ou à des positions fixes, précise l'usage des documents APK/JAR/Office fondés sur ZIP et estime texte ou binaire d'après le taux de caractères imprimables et l'entropie.
- En-tête d'un coup d'œil : affiche les 64 premiers octets du fichier en vis-à-vis hexadécimal + ASCII et détecte les marques d'ordre des octets (BOM) UTF-8 / UTF-16 / UTF-32.
- Double contrôle de taille : montre à la fois la taille déclarée par le gestionnaire de fichiers et la taille réellement lue ; si le fichier change pendant la lecture, l'inspection échoue immédiatement, ce qui révèle les téléchargements incomplets et les fichiers en cours d'écriture.
- Progression maîtrisée : les gros fichiers affichent les octets lus, le débit et le temps restant estimé ; la lecture peut être annulée à tout moment et une inspection échouée se relance d'une pression.
- Résultats prêts à l'emploi : copiez séparément une somme de contrôle ou l'instantané de l'en-tête, puis exportez le résultat complet affiché en Markdown ou JSON vers le presse-papiers ou le panneau de partage, sans créer de fichier.
- Algorithmes anciens signalés : MD5 et SHA-1 portent un badge Legacy pour rappeler qu'ils ne conviennent plus comme preuve de sécurité.

## Mode d'emploi

1. Téléchargez l'APK et son fichier de contrôle homonyme `.apk.sha256` depuis la même Release officielle, installez l'APK, puis activez-le dans le centre de plugins d'AutoJs6 (code de version AutoJs6 5268 ou ultérieur requis).
2. Avant une mise à niveau, inspectez l'APK téléchargé avec la version déjà installée de File Inspector ; lors d'une première installation, conservez l'APK et inspectez-le après l'activation du plugin. Collez le SHA-256 de 64 caractères du fichier de contrôle et exigez une correspondance verte ; les deux fichiers doivent provenir de la même page Release HTTPS de confiance.
3. Ouvrez le gestionnaire de fichiers d'AutoJs6 et repérez le fichier à inspecter ; tout fichier ordinaire convient.
4. Choisissez « Inspecter le fichier » dans le menu du fichier ; la lecture démarre aussitôt avec une progression en direct, puis les sommes de contrôle, la signature de format et les octets d'en-tête s'affichent.
5. Pour vérifier l'intégrité, collez la somme de contrôle publiée dans le champ « Contrôle d'intégrité » et touchez « Vérifier » : vert signifie correspondance, rouge signifie écart.
6. Touchez le bouton de copie près d'une somme de contrôle ou sous l'instantané de l'en-tête pour obtenir une valeur ; choisissez Markdown ou JSON avant de copier ou partager le rapport complet, puis revenez au gestionnaire de fichiers.

> Le champ accepte l'hexadécimal brut, les préfixes comme `sha256: <valeur>` ou `MD5=<valeur>`, les empreintes `AB:CD:EF`, CRC32 avec `0x`, Base64 standard, SRI comme `sha256-<base64>`, et les lignes coreutils complètes `<hex>  <fichier>` ou `<hex> *<fichier>`. La casse et les espaces environnants sont ignorés ; l'algorithme est déduit lorsque la longueur est unique, et un nom de fichier différent déclenche un avertissement.

## Autorisations et sécurité

Le plugin ne demande aucune autorisation de stockage ni de réseau et n'atteint que le seul fichier choisi par l'utilisateur, via une URI content temporaire en lecture seule accordée par l'hôte ; l'autorisation expire à la fin de l'inspection et aucun autre fichier n'est accessible. Les requêtes du gestionnaire de fichiers sont validées champ par champ — identifiant d'action, version de protocole, forme de l'URI content, nom de fichier, type MIME, taille déclarée et autorisations en lecture seule — et toute requête portant une autorisation d'écriture ou persistante est rejetée d'emblée. Le fichier est traité en flux et en lecture seule ; la mémoire ne conserve que des tampons bornés, les 4096 premiers octets pour l'analyse, les 64 premiers pour l'affichage et une fenêtre de quatre octets pour la signature PE, sans jamais modifier le fichier source.

Pour garder chaque inspection prévisible, le plugin applique les bornes suivantes:

- Un fichier ne peut dépasser 8 TiB, et chaque action traite exactement un fichier cible.
- L'analyse échantillonne au plus les 4096 premiers octets, plus une fenêtre de signature PE de quatre octets, tandis que l'en-tête affiché reste limité à 64 octets ; la saisie est limitée à 512 caractères, les caractères non ASCII n'étant admis que dans un nom de fichier coreutils.
- Si la taille réellement lue diffère de la taille déclarée, le fichier est considéré comme modifié et l'inspection échoue avec un message.
- La comparaison des sommes de contrôle s'effectue en temps constant sur des valeurs de même longueur ; MD5 et SHA-1 ne servent qu'à contrôler des données anciennes et ne doivent pas être tenus pour résistants aux collisions.
