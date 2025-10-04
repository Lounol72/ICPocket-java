package main;

import game.*;


import static utilz.Constants.SetLanguage;
import static utilz.HelpMethods.GetPhrase;

public class Main {
    // Main method to start the game
    private static Game game;
    public static void main(String[] args) {
        Init();
    }

    private static void Init() {
        game = new Game();
    }


}