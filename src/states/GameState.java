package states;

public enum GameState {
//    FIRST_LAUNCH,
    START,
    MENU,
    WORLD,
    SETTINGS,
    INFOS;

    public static GameState currentState = GameState.START;
    private static GameState lastState = GameState.START;

    public static void setState( GameState state ){
        lastState = currentState;
        currentState = state;
    }
    public GameState getLastState(GameState state){
        return lastState;
    }

}
