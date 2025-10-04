# ICPocket Java

Un jeu de type Pokémon développé en Java avec une interface graphique moderne et un système de combat tactique.

## 🎮 Description du projet

ICPocket est un jeu de capture et de combat de créatures inspiré des jeux Pokémon, développé entièrement en Java. Le jeu propose :

- **Système de capture** : Capturez et collectionnez des ICMons uniques
- **Combat tactique** : Système de combat au tour par tour avec différents types d'attaques
- **Interface graphique** : Interface utilisateur moderne avec animations fluides
- **Système de sauvegarde** : Sauvegarde automatique de votre progression
- **Multilingue** : Support de plusieurs langues (Français, Anglais, Allemand)

## 🚀 Prérequis

- **Java 20** ou version supérieure
- **Maven 3.6+** pour la gestion des dépendances
- **IDE** (IntelliJ IDEA, Eclipse, VS Code) recommandé

## 📦 Installation

### 1. Cloner le projet
```bash
git clone <url-du-repo>
cd ICPocket-Java
```

### 2. Vérifier la configuration Maven
Le projet utilise Maven pour la gestion des dépendances. Vérifiez que Maven est installé :
```bash
mvn --version
```

### 3. Compiler le projet
```bash
mvn clean compile
```

## 🎯 Lancement du projet

### Méthode 1 : Avec Maven (Recommandée)
```bash
# Compiler et exécuter
mvn clean compile
mvn package
mvn exec:java
```

### Méthode 2 : Créer un JAR exécutable
```bash
# Créer le JAR avec toutes les dépendances
mvn clean package

# Exécuter le JAR
java -jar target/icpocket-java-1.0.0.jar
```

### Méthode 3 : Exécution directe
```bash
# Compiler seulement
mvn compile

# Exécuter la classe principale
java -cp "target/classes:lib/*" main.Main
```

## 📁 Structure du projet

```
ICPocket-Java/
├── src/                    # Code source Java
│   ├── main/              # Point d'entrée du jeu
│   ├── entities/          # Entités du jeu (Player, ICMon)
│   ├── game/              # Logique principale du jeu
│   ├── states/            # États du jeu (Menu, Battle, World)
│   ├── duel/              # Système de combat
│   ├── icmon/            # Classes des ICMons
│   ├── ui/               # Interface utilisateur
│   ├── levels/           # Gestion des niveaux
│   ├── inputs/           # Gestion des entrées clavier/souris
│   └── utilz/            # Utilitaires et constantes
├── res/                   # Ressources du jeu
│   ├── assets/           # Images, sprites, sons
│   │   ├── ICMONS/       # Sprites des ICMons
│   │   ├── Levels/       # Cartes et niveaux
│   │   ├── UI/           # Interface utilisateur
│   │   └── tileset/      # Tiles pour les cartes
│   └── data/             # Données JSON et sauvegardes
├── lib/                   # Librairies externes
│   └── gson-2.10.1.jar   # Sérialisation JSON
├── pom.xml               # Configuration Maven
└── README.md             # Documentation
```

## 🎮 Contrôles du jeu

### Navigation
- **Q/D/Espace** : Déplacement du personnage
- **Entrer** : Interaction / Confirmation
- **Échap** : Retour / Annulation


## 🔧 Développement

### Ajouter de nouveaux ICMons
1. Ajoutez le sprite dans `res/assets/ICMONS/`
2. Créez la classe dans `src/icmon/`
3. Ajoutez les données dans `res/data/data.json`

### Ajouter de nouveaux niveaux
1. Créez le fichier JSON dans `res/assets/Levels/`
2. Ajoutez l'image du niveau
3. Configurez les collisions et interactions

### Modifier l'interface
1. Éditez les fichiers dans `src/ui/`
2. Modifiez les sprites dans `res/assets/UI/`
3. Ajustez les constantes dans `src/utilz/Constants.java`

## 🌐 Internationalisation

Le jeu supporte plusieurs langues. Les fichiers de traduction se trouvent dans `res/data/` :
- `langue_fr.properties` : Français
- `langue_en.properties` : Anglais  
- `langue_ger.properties` : Allemand

## 🐛 Dépannage

### Problèmes courants

**Erreur "Java version"**
```bash
# Vérifiez votre version Java
java --version
# Doit afficher Java 20 ou supérieur
```

**Erreur Maven**
```bash
# Nettoyez le cache Maven
mvn clean
# Recompilez
mvn compile
```

**Ressources non trouvées**
- Vérifiez que le dossier `res/` contient tous les assets
- Assurez-vous que les chemins dans le code correspondent à la structure

## 📝 Contribution

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Committez vos changements (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Ouvrez une Pull Request

## 📄 Licence

Ce projet est sous licence [MIT](LICENSE).

## 👥 Équipe

- **Développement** : Équipe ICPocket
- **Art** : Sprites et assets originaux
- **Musique** : Composition originale

## 🔗 Liens utiles

- [Documentation Java 20](https://docs.oracle.com/en/java/javase/20/)
- [Guide Maven](https://maven.apache.org/guides/)
- [Documentation du projet](docs/)

---

**Bon jeu ! 🎮**