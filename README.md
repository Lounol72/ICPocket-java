# ICPocket Java

Un jeu de plateforme 2D développé en Java avec un système de physique avancé, des animations fluides et une interface graphique moderne.

## 🎮 Description du projet

ICPocket est un jeu de plateforme 2D développé entièrement en Java. Le jeu propose :

- **Système de physique avancé** : Moteur de physique personnalisé avec forces, gravité et collisions
- **Mécaniques de plateforme** : Saut, double saut, chute rapide, plateformes one-way
- **Système d'états** : Gestion des états du joueur (Idle, Run, Jump, Attack) et des ennemis
- **Système de niveaux** : Gestion de plusieurs niveaux avec support de cartes Tiled
- **Interface graphique** : Interface utilisateur moderne avec animations fluides
- **Système d'animations** : Gestionnaire d'animations pour les entités
- **Multilingue** : Support de plusieurs langues (Français, Anglais, Allemand)
- **Système de sauvegarde** : Sauvegarde de la configuration et de la progression

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
│   ├── main/              # Point d'entrée du jeu (Main.java)
│   ├── game/              # Logique principale du jeu
│   │   ├── Game.java      # Boucle principale du jeu (FPS/UPS)
│   │   ├── GamePanel.java # Panneau de rendu
│   │   └── GameWindow.java # Fenêtre du jeu
│   ├── entities/          # Entités du jeu
│   │   ├── Player.java    # Joueur avec système d'états
│   │   ├── Ennemy.java    # Ennemis de base
│   │   ├── Mushroom.java  # Ennemi spécifique
│   │   └── AnimationManager.java # Gestionnaire d'animations
│   ├── states/            # États du jeu
│   │   ├── Menu.java      # Menu principal
│   │   ├── World.java     # État de jeu principal
│   │   ├── LevelSelect.java # Sélection de niveau
│   │   ├── Settings.java  # Paramètres
│   │   └── PlayerStates/  # États du joueur (Idle, Run, Jump, Attack)
│   ├── physics/           # Système de physique
│   │   ├── PhysicsBody.java # Corps physique
│   │   ├── Vector2D.java  # Vecteurs 2D
│   │   ├── Force.java     # Forces appliquées
│   │   └── ForceType.java # Types de forces
│   ├── services/          # Services du jeu
│   │   ├── PhysicsService.java # Service de physique
│   │   ├── AnimationService.java # Service d'animations
│   │   └── InputService.java # Service d'entrées
│   ├── levels/            # Gestion des niveaux
│   │   ├── Level.java     # Classe de niveau
│   │   └── LevelManager.java # Gestionnaire de niveaux
│   ├── ui/                # Interface utilisateur
│   │   ├── Button.java    # Boutons génériques
│   │   ├── MenuButtons.java # Boutons du menu
│   │   └── settings/      # Composants des paramètres
│   ├── inputs/            # Gestion des entrées
│   │   ├── KeyboardInputs.java # Entrées clavier
│   │   └── MouseInputs.java # Entrées souris
│   ├── config/            # Configuration
│   │   ├── PlayerConfig.java # Configuration du joueur
│   │   └── EnnemyConfig.java # Configuration des ennemis
│   └── utilz/             # Utilitaires et constantes
│       ├── Constants.java # Constantes du jeu
│       ├── HelpMethods.java # Méthodes utilitaires
│       └── LoadSave.java  # Chargement/sauvegarde
├── res/                   # Ressources du jeu
│   ├── assets/            # Assets du jeu
│   │   ├── ICMONS/        # Sprites des personnages
│   │   ├── Levels/        # Données et images des niveaux
│   │   ├── Monsters/      # Sprites des ennemis
│   │   ├── UI/            # Interface utilisateur
│   │   └── tileset/       # Tilesets et cartes Tiled
│   └── data/              # Données JSON et sauvegardes
│       ├── data.json      # Données du jeu
│       ├── langue_*.properties # Fichiers de traduction
│       └── save_config.json # Configuration sauvegardée
├── lib/                   # Librairies externes
│   └── gson-2.10.1.jar   # Sérialisation JSON
├── pom.xml               # Configuration Maven
└── README.md             # Documentation
```

## 🎮 Contrôles du jeu

### Contrôles du joueur
- **Q/D** : Déplacement gauche/droite
- **Espace** : Saut
- **Bas** : Passer à travers les plateformes one-way
- **E** : Attaque

### Navigation dans les menus
- **Entrer** : Confirmation / Sélection
- **Échap** : Retour / Annulation
- **Souris** : Navigation dans les menus


## 🔧 Développement

### Architecture du projet

Le projet utilise une architecture modulaire avec :
- **Système d'états** : Gestion des différents états du jeu (Menu, World, Settings, etc.)
- **Système de physique** : Moteur de physique personnalisé avec forces et collisions
- **Pattern State** : États du joueur gérés par `PlayerStateManager`
- **Services** : Services pour la physique, les animations et les entrées

### Ajouter de nouveaux ennemis
1. Créez une classe héritant de `Ennemy` dans `src/entities/`
2. Ajoutez les sprites dans `res/assets/Monsters/`
3. Configurez les paramètres dans `src/config/EnnemyConfig.java`
4. Ajoutez les animations dans `AnimationManager`

### Ajouter de nouveaux niveaux
1. Créez le fichier JSON dans `res/assets/Levels/levelsData/`
2. Ajoutez les images du niveau dans `res/assets/Levels/LevelOne/`
3. Configurez les collisions et les plateformes one-way
4. Ajoutez le niveau dans `LevelManager`

### Modifier les constantes physiques
1. Éditez `src/utilz/Constants.java`
2. Ajustez les valeurs dans les classes internes (`PLAYER`, `ENNEMY`, etc.)
3. Les constantes incluent : gravité, vitesse, forces de saut, etc.

### Modifier l'interface
1. Éditez les fichiers dans `src/ui/`
2. Modifiez les sprites dans `res/assets/UI/`
3. Ajustez les constantes dans `src/utilz/Constants.java`

## 🌐 Internationalisation

Le jeu supporte plusieurs langues. Les fichiers de traduction se trouvent dans `res/data/` :
- `langue_fr.properties` : Français
- `langue_en.properties` : Anglais  
- `langue_de.properties` : Allemand

Le changement de langue se fait dans les paramètres du jeu et met à jour toutes les chaînes de caractères dynamiquement.

## 🐛 Dépannage

### Problèmes courants

**Erreur "Java version"**
```bash
# Vérifiez votre version Java
java --version
# Doit afficher Java 20 ou supérieur
```

**Erreur Maven - Clean échoue**
Si `mvn clean` échoue à cause de fichiers verrouillés (surtout sur Windows) :
```bash
# Fermez l'application si elle est en cours d'exécution
# Puis réessayez
mvn clean
```

**Erreur Maven - Paramètre inconnu**
Si vous voyez des avertissements sur des paramètres inconnus :
- Vérifiez que vous utilisez la bonne version de Maven (3.6+)
- Les paramètres `target` dans `maven-javadoc-plugin` ont été supprimés (non supportés)

**Ressources non trouvées**
- Vérifiez que le dossier `res/` contient tous les assets
- Assurez-vous que les chemins dans le code correspondent à la structure
- Les chemins sont relatifs à `res/` dans le code

**Problèmes de compilation**
```bash
# Nettoyez et recompilez
mvn clean compile
# Ou pour créer le JAR
mvn clean package
```

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

## 📊 Performance

Le jeu est optimisé pour :
- **120 FPS** : Taux de rafraîchissement cible
- **200 UPS** : Taux de mise à jour de la logique du jeu
- Synchronisation indépendante FPS/UPS pour une expérience fluide

Les performances peuvent être surveillées via les constantes de debug dans `Constants.java`.

## 🛠️ Technologies utilisées

- **Java 20** : Langage de programmation
- **Maven** : Gestion des dépendances et build
- **Gson 2.10.1** : Sérialisation/désérialisation JSON
- **Tiled** : Éditeur de cartes (fichiers .tmx)
- **Aseprite** : Édition de sprites (fichiers .aseprite)

## 🔗 Liens utiles

- [Documentation Java 20](https://docs.oracle.com/en/java/javase/20/)
- [Guide Maven](https://maven.apache.org/guides/)
- [Documentation Gson](https://github.com/google/gson)
- [Tiled Map Editor](https://www.mapeditor.org/)

## 📝 Notes de développement

- Le projet utilise un système de physique personnalisé inspiré de jeux comme Hollow Knight
- Les plateformes one-way permettent de passer à travers en montant ou en appuyant sur "Bas"
- Le système d'états du joueur permet des transitions fluides entre les animations
- Les constantes physiques sont centralisées dans `Constants.java` pour faciliter le réglage

---

**Bon jeu ! 🎮**