package states;

public enum GameState {
//    FIRST_LAUNCH,
    SPLASH,
    START,
    MENU,
    LEVEL_SELECT,
    WORLD,
    SETTINGS,
    INFOS,
    QUIT;

    public static GameState currentState = GameState.SPLASH;
    private static GameState lastState = GameState.SPLASH;

    public static void setState( GameState state ){
        lastState = currentState;
        currentState = state;
    }
    public GameState getLastState(GameState state){
        return lastState;
    }

}
