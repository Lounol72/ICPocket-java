package utilz;

import java.util.ArrayList;
import java.util.List;

// Classe principale de sauvegarde
public class SaveData {
    private int nbICMons;  // Nombre total d'ICMons
    private List<ICMonSaveData> icmons;

    public SaveData() {
        this.icmons = new ArrayList<>();
    }

    // Getters et setters
    public int getNbICMons() {
        return nbICMons;
    }

    public void setNbICMons(int nbICMons) {
        this.nbICMons = nbICMons;
    }

    public List<ICMonSaveData> getIcmons() {
        return icmons;
    }

    public void setIcmons(List<ICMonSaveData> icmons) {
        this.icmons = icmons;
        this.nbICMons = icmons.size();
    }
}

