package duel;

import icmon.ICMon;
import ui.ScrollingText;
import utilz.t_Effect;

public class BattleStateManager {
    private Team rouge;
    private Team bleu;
    private boolean hasAttacked;
    private TurnState turnState;
    private ScrollingText text;
    private int moveRouge;
    private int moveBleu;
    private boolean first;

    public BattleStateManager(Team rouge,Team bleu){
        this.rouge = rouge;
        this.bleu = bleu;
        initClasses();
    }

    private void initClasses() {
        text = new ScrollingText(0,0,100,10);
    }

    public static enum TurnState {
        TURN_NONE,
        TURN_INIT,
        TURN_ACTION1,
        TURN_ACTION2,
        TURN_FINISHED
    }

    public void finishApplyEffectDamage() {
        // Appliquer les effets de statut à la fin du tour pour l'équipe rouge
        applyStatusEffects(rouge.getTeam()[0]);
        // Appliquer les effets de statut à la fin du tour pour l'équipe bleue
        applyStatusEffects(bleu.getTeam()[0]);

        // Réinitialisation des effets de peur
        resetFlinchEffects();
    }

    private void applyStatusEffects( ICMon icmon) {
        if (!icmon.isAlive()) {
            return;
        }

        if (icmon.getMain_effect() == t_Effect.burn) {
            String msg = String.format("%s souffre de sa brûlure !", icmon.getName());
            text.reset(msg);
            icmon.recoilDamage(100, 6);
        }

        if (icmon.getMain_effect() == t_Effect.poison) {
            String msg = String.format("%s souffre du poison !", icmon.getName());
            text.reset(msg);
            icmon.recoilDamage(100, 12);
        }
    }

    private void resetFlinchEffects() {
        ICMon rougeICMon = rouge.getTeam()[0];
        ICMon bleuICMon = bleu.getTeam()[0];

        if (rougeICMon.getMain_effect() == t_Effect.flinch) {
            rougeICMon.setMain_effect(t_Effect.noEffect);
        }
        if (bleuICMon.getMain_effect() == t_Effect.flinch) {
            bleuICMon.setMain_effect(t_Effect.noEffect);
        }
    }
}