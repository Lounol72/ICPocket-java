package duel;

import icmon.ICMon;

import java.awt.*;

import static utilz.Constants.ICMONS.MULTIPLIERS.*;
import static utilz.Constants.ICMONS.STATS.PV;
import static utilz.HelpMethods.generateTeamFromId;

public class Team {
    private int id;
    private String name;
    private ICMon[] team;
    private int nbPoke;

    public Team() {
        this(1); // Appel du constructeur avec l'ID par défaut
    }

    public Team(int id) {
        Team generatedTeam = generateTeamFromId(id);
        if (generatedTeam == null) {
            throw new IllegalStateException("Impossible de générer l'équipe avec l'ID: " + id);
        }
        copyTeamProperties(generatedTeam);
    }

    private void copyTeamProperties(Team source) {
        this.id = source.id;
        this.team = source.team;
        this.nbPoke = source.nbPoke;
        this.name = source.name;
    }

    public Team(int id,ICMon[] team, int nbPoke, String name){
        this.id = id;
        this.team = team;
        this.nbPoke = nbPoke;
        this.name = name;
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

    public int getNbPoke() {
        return nbPoke;
    }

    public void setNbPoke( int nbPoke ) {
        this.nbPoke = nbPoke;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setTeam( ICMon[] team ) {
        this.team = team;
    }

    public void swapActualAttacker( int i ) {
        ICMon current = team[0];
        team[0] = team[i];
        team[i] = current;
        for (ICMon mons : team)
            mons.setDefaultStatChanges();

    }

    public void update() {
        team[0].update();
    }

    public void draw( Graphics g ) {
        team[0].draw(g);
    }
}