package utilz;

import java.util.ArrayList;
import java.util.List;

// Classe pour sauvegarder les données d'un ICMon
public class ICMonSaveData {
    private int id;
    private int lvl;
    private int nature;
    private int nb_move;
    private List<Integer> moveIds;
    private int[] iv;
    private String[] types;

    public ICMonSaveData() {
        this.moveIds = new ArrayList<>();
        this.iv = new int[6];
        this.types = new String[2];
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public int[] getIv() {
        return iv;
    }

    public void setIv( int[] iv ) {
        this.iv = iv;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl( int lvl ) {
        this.lvl = lvl;
    }

    public List<Integer> getMoveIds() {
        return moveIds;
    }

    public void setMoveIds( List<Integer> moveIds ) {
        this.moveIds = moveIds;
    }

    public int getNature() {
        return nature;
    }

    public void setNature( int nature ) {
        this.nature = nature;
    }

    public int getNb_move() {
        return nb_move;
    }

    public void setNb_move( int nb_move ) {
        this.nb_move = nb_move;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes( String[] types ) {
        this.types = types;
    }

    // Getters et setters
    // ... (ajoutez tous les getters/setters nécessaires)
}
