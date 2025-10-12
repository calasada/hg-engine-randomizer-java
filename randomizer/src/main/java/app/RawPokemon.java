package app;

import java.util.List;

public class RawPokemon {

    public int id;
    public String species_name;
    public String species_withform;
    public Tier tier;
    public boolean form;
    public boolean encounter_valid;
    public boolean trainer_valid;
    public boolean has_all_sprites;
    public boolean has_front_sprite;
    public boolean starter;
    public boolean legendary;
    public boolean pseudolegendary;
    public boolean ultrabeast;
    public boolean paradox;
    public boolean evil;
    public Type typeA;
    public Type typeB;
    public List<AltSpawnRef> alt_spawns;
    public List<EvolutionRef> evolution_tree;
    public List<String> moveset;

}


