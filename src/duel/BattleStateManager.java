package duel;


import icmon.ICMon;
import icmon.Move;
import ui.ScrollingText;

import java.awt.*;

import static duel.TurnState.*;
import static utilz.Constants.ICMONS.Combat.*;
import static utilz.Constants.ICMONS.STATS.SPE;
import static utilz.Constants.ICMONS.StatVariations.getStatVariation;
import static utilz.Constants.ICMONS.criticalHitFlag;
import static utilz.Constants.ICMONS.moveEffectivenessFlag;
import static utilz.HelpMethods.GetPhrase;


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
        text = new ScrollingText(0,0,100,10);
    }



    private boolean resolveSpeedDuel(int speed1, int speed2){
        if (speed1>speed2) return true;
        if (speed1<speed2) return false;
        return Math.random() < 0.5; //speed tie : choose winner randomly
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
        if(text.isComplete()){
            turnState = turnState.TURN_ACTION1;
            hasAttacked = false;
        }
    }

    private void turnPlayer(){
        if (!hasAttacked) {
            if ( rouge.isTeamAlive() && rouge.getTeam()[0].isAlive() ) {
                if ( rouge.getTeam()[0].isAttacking(moveRouge) )
                    //Mix_PlayChannel(2, game.battleState.rouge.team[0].img->ICMonSound[game.battleState.moveRouge], 0);
                    executeAction();
                if ( !bleu.getTeam()[0].isAlive() ) rouge.gainExp(bleu.getTeam()[0]);
            }
            hasAttacked = true;
        }
        if (rouge.getTeam()[0].isAttacking(moveRouge) && text.isComplete()){
            if(criticalHitFlag){
                criticalHitFlag = false;
                text.reset(GetPhrase("crit"));
            }
            else if (moveEffectivenessFlag < -1.0f){
                String msg = moveEffectivenessFlag == 0 ? GetPhrase("no_effect") :(
                        (moveEffectivenessFlag<=0.9) ? GetPhrase("no_very_eff"):
                                (moveEffectivenessFlag>1.1) ? GetPhrase("very_eff"): "");
                text.reset(msg);
            }
        }
        if (text.isComplete()){
            turnState = TURN_ACTION2;
            hasAttacked = false;
        }

    }



    private void turnAI(){
        if (!hasAttacked) {
            if ( bleu.isTeamAlive()) {
                if ( !bleu.getTeam()[0].isAlive()){
                    int nb_valide = 0;
                    int[] liste_valide = new int[bleu.getNbPoke()];
                    for (int i = 0; i < liste_valide.length; i++) {
                        if (bleu.getTeam()[i].isAlive())
                            liste_valide[nb_valide++] = i + 10;
                    }
                    int x = (int) (Math.random() * nb_valide);
                    bleu.swapActualAttacker(liste_valide[x]);
                }
                else if ( bleu.getTeam()[0].isAttacking(moveBleu) ) {
                    //Mix_PlayChannel(2, game.battleState.rouge.team[0].img->ICMonSound[game.battleState.moveRouge], 0);
                    executeAction();
                }
            }
            hasAttacked = true;
        }
        if (bleu.getTeam()[0].isAttacking(moveBleu) && text.isComplete()){
            if(criticalHitFlag){
                criticalHitFlag = false;
                text.reset(GetPhrase("crit"));
            }
            else if (moveEffectivenessFlag < -1){
                String msg = moveEffectivenessFlag == 0 ? GetPhrase("no_effect") :(
                        (moveEffectivenessFlag<=0.9) ? GetPhrase("no_very_eff"):
                                (moveEffectivenessFlag>1.1) ? GetPhrase("very_eff"): "");
                text.reset(msg);
            }
        }
        if (text.isComplete()){
            turnState = TURN_FINISHED;
            hasAttacked = false;
        }
    }
    private void turnFinished(){
        if (!hasAttacked) {finishApplyEffectDamage();hasAttacked = true;}

        turnState = TURN_NONE;
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
                turnFinished();
            }
            default -> {
            }
        }
        text.update();
        rouge.update();
        bleu.update();
    }

    public void draw( Graphics g){
        if(turnState != TURN_NONE)
            text.draw(g);
        if(rouge.isTeamAlive())
            rouge.draw(g);
        if(bleu.isTeamAlive())
            bleu.draw(g);
    }


    private void executeAction() {
    }

    private void attackHasEffects(){
    }

    private void finishApplyEffectDamage() {
    }
}