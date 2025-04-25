package duel;

public enum AiType {
    none(1),
    statusFirst(2),
    damageOnly(3),
    boss(6);

    private final int value;

    AiType( int i ) {
        this.value = i;
    }
    public int getValue(){
        return value;
    }
}
