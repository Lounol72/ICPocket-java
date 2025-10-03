package entities;

public enum PlayerStateEnum {
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

    public static PlayerStateEnum state = IDLE;
}
