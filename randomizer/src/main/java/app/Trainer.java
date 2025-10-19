package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static app.Trainer.TrainerType.TRAINERCLASS_RIVAL;

public class Trainer {

    public int id;
    public String trainer_name;
    public TrainerType type;
    public int original_gym;
    public int ace_level;
    public boolean moves_defined;

    public static Pokemon[] rivalTeam_stage1 = new Pokemon[6];
    public static Pokemon[] rivalTeam_stage2 = new Pokemon[6];
    public static Pokemon[] rivalTeam_stage3 = new Pokemon[6];

    public static Pokemon[] rivalTeam_stage1_noforms = new Pokemon[6];
    public static Pokemon[] rivalTeam_stage2_noforms = new Pokemon[6];
    public static Pokemon[] rivalTeam_stage3_noforms = new Pokemon[6];

    public enum TrainerType {

        TRAINERCLASS_UNIMPORTANT (60, 35, 5, false, false, false, false),
        TRAINERCLASS_RIVAL       (10, 40, 50, true, false, true, false),
        TRAINERCLASS_LEADER      (40, 60, 0, true, false, false, false),
        TRAINERCLASS_ELITE_FOUR  (15, 50, 35, true, false, false, false),
        TRAINERCLASS_CHAMPION    (0,  30, 70, true, false, false, false),
        TRAINERCLASS_TEAM_ROCKET (60, 35, 5, false, true, false, false),
        TRAINERCLASS_EXECUTIVE   (15, 50, 35, true, true, false, false),
        TRAINERCLASS_ROCKET_BOSS (15, 35, 50, true, true, false, true);

        int cWeight;
        int bWeight;
        int aWeight;
        boolean goodMoves;
        boolean evil;
        boolean legendary;
        boolean ultrabeast;

        TrainerType(int cWeight, int bWeight, int aWeight, boolean goodMoves, boolean evil, boolean legendary, boolean ultrabeast) {
            this.cWeight = cWeight;
            this.bWeight = bWeight;
            this.aWeight = aWeight;
            this.goodMoves = goodMoves;
            this.evil = evil;
            this.legendary = legendary;
            this.ultrabeast = ultrabeast;
        }

        public Tier getTierWeighted() {
            int totalWeight = aWeight + bWeight + cWeight;
            int random = new Random().nextInt(totalWeight);
            if (random < aWeight) {
                return Tier.A;
            } else if (random < aWeight + bWeight) {
                return Tier.B;
            } else  {
                return Tier.C;
            }
        }

    }

    public String buildParty(int numMons, int ivs, List<Integer> levels, List<String> items) {

        StringBuilder builder = new StringBuilder();
        builder.append("    party ").append(id)           .append(System.lineSeparator());

        if (ivs < 0 || ivs > 255) {
            System.out.println("Tried to build a party with an invalid iv value " + ivs);
            System.exit(3);
        }

        if (numMons < 0 || numMons > 6) {
            System.out.println("Tried to build a party with an invalid number of pokemon " + numMons);
            System.exit(3);
        } else if (numMons == 0) { // used in weird invalid trainers
            builder.append("        // mon 0")                .append(System.lineSeparator());
            builder.append("        ivs 0")                   .append(System.lineSeparator());
            builder.append("        abilityslot 0")           .append(System.lineSeparator());
            builder.append("        level 0")                 .append(System.lineSeparator());
            builder.append("        pokemon SPECIES_NONE")    .append(System.lineSeparator());
            builder.append("        ballseal 0")              .append(System.lineSeparator());
        } else {

            // if you're dealing with the rival
            if(type == TrainerType.TRAINERCLASS_RIVAL) {

                if (levels.size() != numMons) {
                    System.out.println("Tried to build a party with an amount of levels not equal to the number of mons (" + numMons + "," + levels.size() + ")");
                    System.exit(3);
                }
                
                for (int mon = 0; mon < numMons; mon++) {

                    Pokemon newPokemon = getRivalPokemonFromLevel(mon, levels.get(mon));

                    builder.append("        // mon ").append(mon)                                                                         .append(System.lineSeparator());
                    builder.append("        ivs ").append(ivs)                                                                            .append(System.lineSeparator());
                    builder.append("        abilityslot 0")                                                                               .append(System.lineSeparator());
                    builder.append("        level ").append(levels.get(mon))                                                              .append(System.lineSeparator());
                    builder.append("        ").append(newPokemon.form ? "monwithform " : "pokemon ").append(newPokemon.species_withform)  .append(System.lineSeparator());
                    if(!items.isEmpty()) {builder.append("        item ").append(items.get(mon))                                          .append(System.lineSeparator());}
                    if(moves_defined) {builder.append(newPokemon.buildMoveset(this))                                                      .append(System.lineSeparator());}
                    builder.append("        ballseal 0")                                                                                  .append(System.lineSeparator());
                    if(mon != numMons - 1) {builder                                                                                       .append(System.lineSeparator());}

                }

            } else { // non-rival trainers

                if (levels.size() != numMons) {
                    System.out.println("Tried to build a party with an amount of levels not equal to the number of mons (" + numMons + "," + levels.size() + ")");
                    System.exit(3);
                }

                boolean ace;
                boolean aceReached = false;
                boolean pseudolegendary_picked = false;
                
                for (int mon = 0; mon < numMons; mon++) {

                    ace = (levels.get(mon) == ace_level);
                    
                    //Pokemon newPokemon = generatePokemon(levels.get(mon), ace && !aceReached, pseudolegendary_picked, mon, numMons);
                    Pokemon newPokemon = Pokemon.getPokemonByName("SPECIES_508");
                    pseudolegendary_picked = pseudolegendary_picked || newPokemon.pseudolegendary; // once a pseudolegendary is chosen, all following mons must be non-pseudolegendary

                    builder.append("        // mon ").append(mon)                                                                         .append(System.lineSeparator());
                    builder.append("        ivs ").append(ivs)                                                                            .append(System.lineSeparator());
                    builder.append("        abilityslot 0")                                                                               .append(System.lineSeparator());
                    builder.append("        level ").append(levels.get(mon))                                                              .append(System.lineSeparator());
                    builder.append("        ").append(newPokemon.form ? "monwithform " : "pokemon ").append(newPokemon.species_withform)  .append(System.lineSeparator());
                    if(!items.isEmpty()) {builder.append("        item ").append(items.get(mon))                                          .append(System.lineSeparator());}
                    if(moves_defined) {builder.append(newPokemon.buildMoveset(this))                                                      .append(System.lineSeparator());}
                    builder.append("        ballseal 0")                                                                                  .append(System.lineSeparator());
                    if(mon != numMons - 1) {builder                                                                                       .append(System.lineSeparator());}

                    aceReached = ace;

                }
            }

            
            
        }

        builder.append("    endparty")                    .append(System.lineSeparator());
        builder                                           .append(System.lineSeparator());
        return builder.toString();
    }

    public Pokemon generatePokemon(int level, boolean ace, boolean pseudolegendary_picked, int index, int nummons) {

        List<Pokemon> validMons = new ArrayList<>(RandomizerUI.STATIC_MONS);

        Tier tier;
        if (ace && type.goodMoves) {
            tier = Tier.A;
        } else {
            tier = type.getTierWeighted();
        }

        // evolve all valid mons
        validMons.removeIf(p -> !p.stage_1);
        Pokemon.evolveByLevelInPlace(validMons, level);
        // Remove duplicates
        Set<Pokemon> seen = new HashSet<>();
        validMons.removeIf(p -> !seen.add(p));

        if(ace && type.legendary) { 
            validMons.removeIf(p -> !p.legendary || !p.has_front_sprite);
        } if(ace && type.ultrabeast) {
            validMons.removeIf(p -> !p.ultrabeast || !p.has_front_sprite);
        } else {

            if(type.evil) { // filter out non-evil pokemon (70% chance) if the trainer is evil
                validMons.removeIf(p -> !p.evil && ThreadLocalRandom.current().nextDouble() < 0.7);
            }
            if(original_gym > 0 && !(type == TrainerType.TRAINERCLASS_LEADER && nummons > 4 && index == 2)) { // filter out non-type matches if trainer is part of a gym, special case for leaders to splash into any type with one pokemon
                validMons.removeIf(p -> (p.typeA != RandomizerUI.static_gymTypeMap.get(original_gym)) && (p.typeB != RandomizerUI.static_gymTypeMap.get(original_gym)));
            }

            if(pseudolegendary_picked) { // filter out non-pseudolegendary pokemon if needed
                validMons.removeIf(p -> !p.pseudolegendary);
            }

            validMons.removeIf(p -> !p.trainer_valid || !p.has_front_sprite);

            // try picking a mon from the desired tier, if none exist keep all mons
            List<Pokemon> tryTier = new ArrayList<>(validMons);
            tryTier.removeIf(p -> p.tier != tier);
            if(!tryTier.isEmpty()) {
                validMons = new ArrayList<>(tryTier);
            }
            
        }

        if(validMons.isEmpty()) {
            System.out.println("no validMons for trainer " + trainer_name);
            System.exit(4);
        }

        return validMons.get(new Random().nextInt(validMons.size())).chooseAltForm();
    }


    private static final Pattern HEADER = Pattern.compile("^trainerdata\\s+(\\d+)");
    private static final Pattern NUMMONS = Pattern.compile("^\\s*nummons\\s+(\\d+)\\s*$");
    private static final Pattern PARTY = Pattern.compile("^\\s*party\\s+(\\d+)\\s*$");
    private static final Pattern IVS = Pattern.compile("^\\s*ivs\\s+(\\d+)\\s*$");
    private static final Pattern LEVEL = Pattern.compile("^\\s*level\\s+(\\d+)\\s*$");
    private static final Pattern ITEMS = Pattern.compile("^\\s*item\\s+(\\S+)\\s*$");

    public static String processTrainerBlock(String blockText, int blockIndex) {

        String[] lines = blockText.split("\\R", -1); // keep empty trailing lines
        StringBuilder out = new StringBuilder(blockText.length() + 64);

        // Get trainer id
        int trainerId = -1;
        String firstline = lines[0].trim();
        Matcher m = HEADER.matcher(firstline);
        if(m.find()) {
            trainerId = Integer.parseInt(m.group(1));
        }

        // Return the Area in dexAreas with the found id
        Trainer currentTrainer = null;
        for (Trainer t : RandomizerUI.STATIC_TRAINERS) {
            if (t.id == trainerId) {
                currentTrainer = t;
            }
        }
        if(currentTrainer == null) {
            System.out.println("No Trainer found with Trainer Id " + trainerId);
            System.exit(3);
        } else {
            //System.out.println("Trainer Id " + trainerId + " matched to trainer " + currentTrainer.trainer_name);
        }

        int numMons = -1;
        int ivs = -1;
        ArrayList<Integer> levels = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        boolean partyReached = false;

        for (String line : lines) {

            // 1) "nummons" -> update nummons
            Matcher nm = NUMMONS.matcher(line);
            if (nm.find()) {
                numMons = Integer.parseInt(nm.group(1));
                //System.out.println("Number of Pokemon: " + numMons);
                out.append(line).append(System.lineSeparator());
                continue;
            }

            // 2) "party" -> generate trainer party
            Matcher pt = PARTY.matcher(line);
            if (pt.find()) {
                partyReached = true;
                continue;
            }

            // 3) "ivs" -> generate trainer party
            Matcher iv = IVS.matcher(line);
            if (iv.find()) {
                ivs = Integer.parseInt(iv.group(1));
                //System.out.println("IV's: " + ivs);
                continue;
            }

            // 4) "levels" -> generate trainer party
            Matcher l = LEVEL.matcher(line);
            if (l.find()) {
                levels.add(Integer.valueOf(l.group(1)));
                continue;
            }

            // 5) "items" -> generate trainer party
            Matcher it = ITEMS.matcher(line);
            if (it.find() && partyReached) {
                items.add(it.group(1));
                continue;
            }
                
            if (partyReached) {

            } else {
                out.append(line).append(System.lineSeparator());
            }
            
        }


        //System.out.println("Level: " + levels);

        out.append(currentTrainer.buildParty(numMons, ivs, levels, items));

        return out.toString();
    }

    @Override public String toString() {
        return trainer_name + "= id:" + id + ", type:" + type + ", gym:" + original_gym;
    }

    public static void generateRivalTeam() {

        Pokemon[] tempTeam = new Pokemon[6];

        boolean pseudolegendary_picked = false;

        for(int i = 0; i < 6; i++) {
            List<Pokemon> validMons = new ArrayList<>(RandomizerUI.STATIC_MONS);
            Tier tier = TRAINERCLASS_RIVAL.getTierWeighted();

            // evolve all valid mons
            validMons.removeIf(p -> !p.stage_1);
            Pokemon.evolveByLevelInPlace(validMons, 100);

            if(i == 5) { // last mon must be legendary
                validMons.removeIf(p -> !p.legendary);
            } else {

                if(i == 0) { // first mon must be evolvable
                    validMons.removeIf(p -> p.stage_1);
                }

                if(pseudolegendary_picked) { // filter out non-pseudolegendary pokemon if needed
                    validMons.removeIf(p -> !p.pseudolegendary);
                }

                // try picking a mon from the desired tier, if none exist keep all mons
                List<Pokemon> tryTier = new ArrayList<>(validMons);
                tryTier.removeIf(p -> p.tier != tier);
                if(!tryTier.isEmpty()) {
                    validMons = new ArrayList<>(tryTier);
                }
            }

            tempTeam[i] = validMons.get(new Random().nextInt(validMons.size()));

            Pokemon stage3;
            Pokemon stage2 = null;
            Pokemon stage1 = null;
            
            stage3 = tempTeam[i];
            if(stage3 != null) { stage2 = tempTeam[i].getPreEvolution(); }
            if(stage2 != null) { stage1 = tempTeam[i].getPreEvolution().getPreEvolution(); }

            if(stage1 != null) { rivalTeam_stage1_noforms[i] = stage1; }
            if(stage2 != null) { rivalTeam_stage2_noforms[i] = stage2; }
            if(stage3 != null) { rivalTeam_stage3_noforms[i] = stage3; }   

            if (stage1 != null) { rivalTeam_stage1[i] = stage1.chooseAltForm(); }
            if (stage2 != null) { rivalTeam_stage2[i] = stage2.chooseAltForm(); }
            if (stage3 != null) { rivalTeam_stage3[i] = stage3.chooseAltForm(); }

        }

        // System.out.println("Rival Teams Generated:");
        // System.out.println(" Stage 1: ");
        // for(Pokemon p : rivalTeam_stage1) {
        //     System.out.println("  " + (p != null ? p.species_name : "  None"));
        // }

        // System.out.println(" Stage 2: ");
        // for(Pokemon p : rivalTeam_stage2) {
        //     System.out.println("  " + (p != null ? p.species_name : "  None"));
        // }

        // System.out.println(" Stage 3: ");
        // for(Pokemon p : rivalTeam_stage3) {
        //     System.out.println("  " + (p != null ? p.species_name : "  None"));
        // }

    }

    public static Pokemon getRivalPokemonFromLevel(int index, int level) {

        Evolution next;

        if (rivalTeam_stage1[index] != null && rivalTeam_stage2[index] != null) { // 3 stage evolution

            next = rivalTeam_stage1_noforms[index].evolution_tree.stream() // find matching stage 2 evolution from stage 1's evolution tree
                .filter(e -> e.pokemon == rivalTeam_stage2_noforms[index])
                .findFirst()
                .orElse(null);

            if (next == null) {
                System.out.println("Evolutions not matching on rival team: " +rivalTeam_stage1[index].species_name + " -> " + (rivalTeam_stage2[index] != null ? rivalTeam_stage2[index].species_name : "null") +
                                                                            " -> " + (rivalTeam_stage3[index] != null ? rivalTeam_stage3[index].species_name : "null"));
                System.exit(1);
            }

            if(level >= next.level) { // if the level is high enough to evolve to stage 2

                if(next.level == -1 && level < 25) { // special case for item evolutions
                    return rivalTeam_stage1[index];
                }

                next = rivalTeam_stage2_noforms[index].evolution_tree.stream() // find matching stage 2 evolution from stage 1's evolution tree
                    .filter(e -> e.pokemon == rivalTeam_stage3_noforms[index])
                    .findFirst()
                    .orElse(null);

                if (next == null) {
                    System.out.println("Evolutions not matching on rival team: " +rivalTeam_stage1[index].species_name + " -> " + (rivalTeam_stage2[index] != null ? rivalTeam_stage2[index].species_name : "null") +
                                                                                " -> " + (rivalTeam_stage3[index] != null ? rivalTeam_stage3[index].species_name : "null"));
                    System.exit(1);
                }

                if (level >= next.level) { // if the level is high enough to evolve to stage 3
                    if(next.level == -1 && level < 40) { // special case for item evolutions
                        return rivalTeam_stage2[index];
                    }
                    return rivalTeam_stage3[index];
                } else {
                    return rivalTeam_stage2[index];
                }

            } else {
                return rivalTeam_stage1[index];
            }
            
        } else if (rivalTeam_stage2[index] != null && rivalTeam_stage1[index] == null) { // 2 stage evolution

            next = rivalTeam_stage2_noforms[index].evolution_tree.stream() // find matching stage 2 evolution from stage 1's evolution tree
                .filter(e -> e.pokemon == rivalTeam_stage3_noforms[index])
                .findFirst()
                .orElse(null);

            if (next == null) {
                System.out.println("Evolutions not matching on rival team: " +rivalTeam_stage1[index].species_name + " -> " + (rivalTeam_stage2[index] != null ? rivalTeam_stage2[index].species_name : "null"));
                System.exit(1);
            }
            
            if(level >= next.level) { // if the level is high enough to evolve to stage 2
                if(next.level == -1 && level < 25) { // special case for item evolutions
                    return rivalTeam_stage2[index];
                }
                return rivalTeam_stage3[index];
            } else {
                return rivalTeam_stage2[index];
            }

        } else { // no evolution
            return rivalTeam_stage3[index];
        }

    }

}
