package utilz;

/**
 * Représente les types des ICmons.
 *
 * Cette énumération définit les différents types de ICmon disponibles dans le jeu.
 */
public enum t_Type {
    noType(0),
    feu(1),
    plante(2),
    eau(3),
    electrique(4),
    malware(5),
    data(6),
    net(7),
    waifu(8);

    private final int value;

    t_Type(int i) {
        this.value = i;
    }

    public static t_Type fromValue(int value) {
        for (t_Type type : t_Type.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return noType; // Valeur par défaut
    }

    public int getValue() {
        return value;
    }
}
