package entities;

public enum PlayerState {
    IDLE,
    KNEEL,
    RUN,
    JUMP,
    FALL,
    ATTACK;

    public static PlayerState state = IDLE;
}
