package app;

import java.util.List;

public class Pokemon {
    
    public int id;
    public String species_name;
    public String species_withform;
    public boolean form;
    public boolean encounter_valid;
    public boolean trainer_valid;
    public boolean starter;
    public boolean legendary;
    public boolean pseudolegendary;
    public boolean ultrabeast;
    public boolean paradox;
    public Type typeA;
    public Type typeB;
    public List<AltSpawn> alt_spawns;

    @Override public String toString() {
        return id + ", " + species_name + ", " + species_withform + " (" + typeA + (typeB != null ? "/" + typeB : "") + ") "
               + (alt_spawns == null ? "" : alt_spawns.toString());
    }

}
