package utilz;

public enum t_Categ {

    status(-1),
    physical(1),
    special(3);

    private final int value;

    t_Categ(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
