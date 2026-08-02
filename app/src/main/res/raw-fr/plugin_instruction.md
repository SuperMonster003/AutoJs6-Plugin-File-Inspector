# Inspecteur de fichiers

L'inspecteur de fichiers examine un fichier avec une seule lecture en continu. Il calcule les sommes de contrôle CRC32, MD5, SHA-1, SHA-256 et SHA-512, identifie les signatures de fichiers courantes et présente les métadonnées de base.

MD5 et SHA-1 sont signalés comme algorithmes hérités. Collez une somme de contrôle attendue pour la vérifier. Vous pouvez aussi copier une somme de contrôle individuelle, ou copier et partager le rapport d'inspection complet.

Le plugin nécessite AutoJs6 build 5268+. Il est entièrement implémenté sur la JVM et ne dépend pas de l'ABI de l'appareil.

Limites de sécurité et de confidentialité:

- Les fichiers de plus de 8 TiB sont rejetés.
- Le contenu du fichier n'est ouvert qu'une fois et traité séquentiellement en mode lecture seule.
- Seuls les 64 premiers octets sont inspectés en tant qu'en-tête du fichier.
- Le plugin n'accède pas au réseau et ne demande aucune autorisation de stockage.
