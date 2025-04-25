package duel;


import icmon.ICMon;
import icmon.Move;
import ui.ScrollingText;

import java.awt.*;

import static duel.TurnState.turnState;
import static utilz.Constants.ICMONS.Combat.*;
import static utilz.Constants.ICMONS.STATS.SPE;
import static utilz.Constants.ICMONS.StatVariations.getStatVariation;


public class BattleStateManager {
    private Team rouge;
    private Team bleu;
    private boolean hasAttacked;
    private ScrollingText text;
    private boolean textInitialised = false;
    private int moveRouge;
    private int moveBleu;
    private boolean first;

    public BattleStateManager(Team rouge,Team bleu){
        this.rouge = rouge;
        this.bleu = bleu;
    }



    private boolean resolveSpeedDuel(int speed1, int speed2){
        if (speed1>speed2) return true;
        if (speed1<speed2) return false;
        return Math.random()%2 == 0; //speed tie : choose winner randomly
    }

    private boolean PriorityForFirstPoke( ICMon rouge, int IdxmoveRouge, ICMon bleu, int IdxmoveBleu ) {
        Move moveRouge = IdxmoveRouge == STRUGGLE_MOVE_INDEX ? STRUGGLE_MOVE : rouge.getMoveList()[IdxmoveRouge];
        Move moveBleu =  IdxmoveBleu  == STRUGGLE_MOVE_INDEX ? STRUGGLE_MOVE : bleu.getMoveList()[IdxmoveBleu];

        int SpeedRouge = (int) (rouge.calcStatFrom(SPE) * getStatVariation( rouge.getStatChanges()[SPE]));
        int SpeedBleu = (int) (bleu.calcStatFrom(SPE) * getStatVariation( bleu.getStatChanges()[SPE]));

        if (rouge.isAttacking(IdxmoveRouge) && bleu.isAttacking(IdxmoveBleu)){
            if(moveRouge.getPriority_lvl() > moveBleu.getPriority_lvl()) return true;
            if(moveRouge.getPriority_lvl() < moveBleu.getPriority_lvl()) return false;
            return resolveSpeedDuel(SpeedRouge, SpeedBleu);
        }
        if(rouge.isSwitching(IdxmoveRouge) && bleu.isSwitching(IdxmoveBleu)){
            return resolveSpeedDuel(SpeedRouge,SpeedBleu);
        }
        if (rouge.isAttacking(IdxmoveRouge)) return false;
        return true;
    }

    public void startBattleTurn(int indexMoveRed, int indexMoveBlue){
        moveRouge = indexMoveRed;
        moveBleu = indexMoveBlue;

        if (!rouge.getTeam()[0].hasMoveLeft() && rouge.getTeam()[0].isAttacking(moveRouge)) moveRouge=STRUGGLE_MOVE_INDEX;
        if (!bleu.getTeam()[0].hasMoveLeft() && bleu.getTeam()[0].isAttacking(moveBleu)) moveBleu=STRUGGLE_MOVE_INDEX;

        first = PriorityForFirstPoke(rouge.getTeam()[0],moveRouge,bleu.getTeam()[0],moveBleu);

        turnState = turnState.TURN_INIT;
    }

    private void turnInit(){
        if (!textInitialised){
            text = new ScrollingText(0,0,100,10);
        }
        if(text.isComplete()){
            turnState = turnState.TURN_ACTION1;
            hasAttacked = false;
        }
    }

    private void turnPlayer(){
        if (!hasAttacked)
            if (rouge.isTeamAlive() && rouge.getTeam()[0].isAlive()){
                if(rouge.getTeam()[0].isAttacking(moveRouge))
                    //Mix_PlayChannel(2, game.battleState.rouge.team[0].img->ICMonSound[game.battleState.moveRouge], 0);
                executeAction();
                if (!bleu.getTeam()[0].isAlive())rouge.gainExp(bleu.getTeam()[0]);
            }
        if (rouge.getTeam()[0].isAttacking(moveRouge)){

        }
    }



    private void turnAI(){

    }

    public void update(){
        switch(turnState){
            case TURN_INIT -> {
                turnInit();
            }
            case TURN_ACTION1 -> {
                if(first)
                    turnPlayer();
                else
                    turnAI();
            }
            case TURN_ACTION2 -> {
                if(first)
                    turnAI();
                else
                    turnPlayer();
            }
            case TURN_FINISHED -> {
            }
            case TURN_NONE -> {
            }
            default -> {
            }
        }
    }

    public void draw( Graphics g){

    }


    private void executeAction() {
    }
}