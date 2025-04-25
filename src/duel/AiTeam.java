package duel;

import utilz.t_Effect;

import java.util.Random;

import static duel.AiType.boss;
import static utilz.t_Effect.noEffect;

public class AiTeam {

    private Random rnd = new Random();

    private int Ai_Level;
    private AiType type;
    Team aiTeam;



    public int AiMoveChoice(Team player){
        int[] tabDamage = {-1, -1, -1, -1};
        int[] tabMove = {-1, -1, -1, -1};
        int nbMoveValides = 0;

        int nbMoves = aiTeam.getTeam()[0].getNb_move();

        for (int i = 0; i < nbMoves; i++){
            if (aiTeam.getTeam()[0].getMoveList()[i].getCurrent_pp() > 0){
                tabDamage[nbMoveValides] = aiTeam.getTeam()[0].calcDamage(player.getTeam()[0],i);
                tabMove[nbMoveValides++] = i;
            }
        }
        if (nbMoveValides == 0) {
            //printf("Aucune attaque valide\n");
            return 0;  // Optionnel : retourner STRUGGLE ou un autre code d'action si nécessaire
        }
        // trier par ordre croissant les dégats estimés
        insertionSort(tabDamage,tabMove,nbMoveValides);

        int chosenIndex = 0;
        if (type.getValue() %2 == 0){
            int has_move_with_effects=0;
            for(int i=0;i<nbMoves;i++){
                /*try to do a main effect applier attack*/
                if ((aiTeam.getTeam()[0].getMoveList()[tabMove[i]].getInd_secEffect() == 1) &&
                        (aiTeam.getTeam()[0].getMoveList()[tabMove[i]].getPower() ==0) &&
                        (player.getTeam()[0].getMain_effect() == t_Effect.noEffect) &&
                                ((aiTeam.getTeam()[0].getMoveList()[tabMove[i]].getValue_effect() < 4)||
                                ((aiTeam.getTeam()[0].getMoveList()[tabMove[i]].getValue_effect() > 3))))
                    if (Ai_Level >= rnd.nextInt(21)){
                        chosenIndex=i;
                        has_move_with_effects=1;
                        break;
                    }
            }
            if(has_move_with_effects == 0 && type!=boss)
                chosenIndex = rnd.nextInt(0,Ai_Level) % nbMoveValides;

        }
        else if (type.getValue() % 3 == 0) { // damage_first AI
            while (chosenIndex < nbMoveValides - 1 && ( rnd.nextInt(0,20) > Ai_Level))
                chosenIndex++;
        }
        else { // none type AI
            chosenIndex = rnd.nextInt(0,Ai_Level) % nbMoveValides;
        }
        return tabMove[chosenIndex];
    }


    private void insertionSort(int[] tabDegats, int[] tabMove, int n) {
        int i, keyDeg, keyMove, j;
        for (i = 1; i < n; i++) {
            keyDeg = tabDegats[i];
            keyMove = tabMove[i];
            j = i - 1;

            // Move elements of arr[0..i-1], that are greater than key, to one position ahead of their current position
            while (j >= 0 && tabDegats[j] < keyDeg) {
                tabDegats[j + 1] = tabDegats[j];
                tabMove[j + 1] = tabMove[j];
                j = j - 1;
            }
            tabDegats[j + 1] = keyDeg;
            tabMove[j + 1] = keyMove;
        }
    }


    public int getAi_Level() {
        return Ai_Level;
    }

    public void setAi_Level( int ai_Level ) {
        Ai_Level = ai_Level;
    }

    public Team getAiTeam() {
        return aiTeam;
    }

    public void setAiTeam( Team aiTeam ) {
        this.aiTeam = aiTeam;
    }

    public AiType getType() {
        return type;
    }

    public void setType( AiType type ) {
        this.type = type;
    }
}
