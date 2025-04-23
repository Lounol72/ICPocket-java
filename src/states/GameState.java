package states;

public enum GameState {
//    FIRST_LAUNCH,
    MENU,
    BATTLE,
    WORLD,
    SETTINGS,
    TEAM,
    INFOS;

    public static GameState currentState = GameState.MENU;
    private static GameState lastState = GameState.MENU;

    public static void setState( GameState state ){
        lastState = currentState;
        currentState = state;
    }
    public GameState getLastState(GameState state){
        return lastState;
    }

}
