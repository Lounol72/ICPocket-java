package entities;

public enum PlayerState {
    IDLE,
    WALK,
    RUN,
    DASH,
    FALL,
    JUMP,
    ROLL,
    CLIMB,
    KNEEL,
    KNEEL_WALK,
    DIE,
    DAMAGE,
    ATTACK;

    public static PlayerState state = IDLE;
}
