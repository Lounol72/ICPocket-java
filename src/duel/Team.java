package duel;

import icmon.ICMon;

import static utilz.Constants.ICMONS.MULTIPLIERS.*;
import static utilz.Constants.ICMONS.STATS.PV;

public class Team {
    private int id;
    private String name;
    private ICMon[] team;
    private int nbPoke;

    public Team(int id){
        //
        this.id = id;
    }

    public ICMon[] getTeam() {
        return team;
    }

    public boolean isTeamAlive() {
        for(int i=0;i<nbPoke;i++){
            if(team[i].isExisting() && team[i].isAlive()) return true;
        }
        return false;
    }

    public void gainExp( ICMon source ) {
        long expAmount;
        int pvBeforeLevelUp;
        for(int i = 0; i < nbPoke; i++){
            //* formula is IsTrainerPoke? * EXPBoost? * FaintedLvl * BaseEXP / 7
            //* isTrainerPoke and EXPBoost are always True by technical reasons therefore their values are 1.5
            //* BaseEXP has no real formula so I defined it with BasePV * 2
            // Calculate exp gain using constants for clarity


            expAmount = (long) ((TRAINER_BONUS * EXP_BOOST * source.getLvl() * BASE_EXP_MULTIPLIER * (source.getBaseStats()[PV])) / EXP_DIVISOR);
            team[i].addExp(expAmount);

            pvBeforeLevelUp = team[i].getInitial_pv();
            while(team[i].reachedNextLvl());
            // Update HP if pokemon was alive
            if(pvBeforeLevelUp != 0) {
                int hp_increase = team[i].calcStatFrom( PV) - pvBeforeLevelUp;
                team[i].setCurrent_pv( team[i].getCurrent_pv() + hp_increase);
                team[i].setInitial_pv( team[i].getInitial_pv() + hp_increase);
            }
        }
    }
}
