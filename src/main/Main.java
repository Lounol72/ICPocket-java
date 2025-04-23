package main;

import game.*;
import icmon.ICMon;

import static utilz.Constants.SetLanguage;
import static utilz.HelpMethods.GetPhrase;

public class Main {
    // Main method to start the game
    private static Game game;
    private static ICMon icMon;
    private static ICMon icMon2;
    public static void main(String[] args) {
        Init();
    }

    private static void Init() {
        game = new Game();
    }


}