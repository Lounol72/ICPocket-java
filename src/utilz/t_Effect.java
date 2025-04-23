package utilz;

public enum t_Effect {
    noEffect(0),
    burn(1),
    poison(2),
    paralyze(3),
    flinch(4),
    confusion(5);


    private final int value;
    t_Effect(int value) {
        this.value = value;
    }

    public t_Effect fromValue(int value) {
        for (t_Effect effect : t_Effect.values()) {
            if (effect.getValue() == value) {
                return effect;
            }
        }
        return noEffect;
    }
    public int getValue() {
        return value;
    }
    public static String getName( t_Effect effect ){
        return effect.name();
    }
}
