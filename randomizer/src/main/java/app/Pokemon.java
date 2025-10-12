package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Pokemon {
    
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
    public List<AltSpawn> alt_spawns;
    public List<Evolution> evolution_tree;
    public List<Move> moveset;
    public List<DexMap<String, Area>> dex_areas = new ArrayList<>();

    record DexMap<K, V>(K dexFlag, V area) {}

    public String buildDexData() {

        StringBuilder builder = new StringBuilder();

        builder.append("specialareas ").append(species_name).append(", DEX_MORNING").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_MORNING") && a.area.special && !a.area.area_name.equals("DEX_UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("specialareas ").append(species_name).append(", DEX_DAY").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_DAY") && a.area.special && !a.area.area_name.equals("UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("specialareas ").append(species_name).append(", DEX_NIGHT").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_NIGHT") && a.area.special && !a.area.area_name.equals("UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("routesandcities ").append(species_name).append(", DEX_MORNING").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_MORNING") && !a.area.special && !a.area.area_name.equals("UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("routesandcities ").append(species_name).append(", DEX_DAY").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_DAY") && !a.area.special && !a.area.area_name.equals("UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("routesandcities ").append(species_name).append(", DEX_NIGHT").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_NIGHT") && !a.area.special && !a.area.area_name.equals("UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("specialareas ").append(species_name).append(", DEX_SPECIAL").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_SPECIAL") && a.area.special && !a.area.area_name.equals("UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        builder.append("routesandcities ").append(species_name).append(", DEX_SPECIAL").append(System.lineSeparator());
        for (DexMap<String, Area> a : dex_areas) {
            if (a.dexFlag.equals("DEX_SPECIAL") && !a.area.special && !a.area.area_name.equals("UNDEFINED")) {
                builder.append("    .word ").append(a.area.area_name).append(System.lineSeparator());
            }
        }
        builder.append("    dexendareadata")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        return builder.toString();
    }

    public Pokemon chooseAltForm() {
        // If no alt spawns, always return self
        if (alt_spawns == null || alt_spawns.isEmpty()) {
            return this;
        }

        // Sum up all alt spawn rates
        double totalAltRate = 0.0;
        for (AltSpawn alt : alt_spawns) {
            totalAltRate += alt.rate;
        }

        // Clamp to avoid weirdness if rates exceed 100
        double selfRate = Math.max(0.0, 100.0 - totalAltRate);
        double totalRate = totalAltRate + selfRate;
        if (totalRate <= 0.0) {
            // fallback to self if all rates are 0
            return this;
        }

        // Pick a random value within total range
        double r = ThreadLocalRandom.current().nextDouble(totalRate);
        double cumulative = 0.0;

        // Go through alt spawns
        for (AltSpawn alt : alt_spawns) {
            cumulative += alt.rate;
            if (r < cumulative) {
                //System.out.println("Chose alt form: " + alt.pokemon.species_name);
                return alt.pokemon; // choose this alternate form
            }
        }

        // If we didn’t fall into any alt slot, return self
        //System.out.println("Chose alt form: " + this.species_name);
        return this;
    }

        @Override public String toString() {
            return id + ", " + species_name + ", " + species_withform + " (" + typeA + (typeB != null ? "/" + typeB : "") + ") "
                + (alt_spawns == null ? "" : alt_spawns.toString());
        }

}
