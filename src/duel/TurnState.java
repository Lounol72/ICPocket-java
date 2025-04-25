package duel;

public enum TurnState {
    TURN_INIT,
    TURN_ACTION1,
    TURN_ACTION2,
    TURN_FINISHED,
    TURN_NONE;

    public static TurnState turnState = TURN_NONE;


}
