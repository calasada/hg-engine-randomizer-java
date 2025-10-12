package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.Pokemon.DexMap;

public class Area {

    public String area_name;
    public ArrayList<Integer> encounter_ids;
    public boolean special;

    enum EncounterFlag { NONE, MORNING, DAY, NIGHT, SPECIAL }

    private static final Pattern HEADER = Pattern.compile("^encounterdata\\s+(\\d+)");
    private static final Pattern RATE = Pattern.compile("^(walkrate|surfrate)\\s+(\\d+)(.*)$");
    private static final Pattern SPECIES_LINE = Pattern.compile("^(pokemon|encounter)(\\s+)([A-Z0-9_]+)(.*)$");
    private static final Pattern COMMENT = Pattern.compile("^\\s*//\\s*(.*)$");

    public static String processAreaEncounterBlock(String blockText, int blockIndex, List<Pokemon> availableMons, List<Pokemon> monsWithAreas) {

        String[] lines = blockText.split("\\R", -1); // keep empty trailing lines
        StringBuilder out = new StringBuilder(blockText.length() + 64);

        // Track the current "flag" derived from the most recent // comment line
        String currentDexFlag = null;

        // Ensure consistent replacement within this block:
        // original species -> chosen Pokemon (removed from pool on first use)
        Map<String, Pokemon> chosenByOriginal = new HashMap<>();

        // Get area id
        int areaId = -1;
        String firstline = lines[0].trim();
        Matcher m = HEADER.matcher(firstline);
        if(m.find()) {
            areaId = Integer.parseInt(m.group(1));
        }

        // Return the Area in dexAreas with the found id
        Area currentArea = null;
        for (Area a : RandomizerUI.STATIC_AREAS) {
            if (a.encounter_ids.contains(areaId)) {
                if(currentArea != null) {
                    System.out.println("Multiple areas found with areaId " + areaId);
                    System.exit(3);
                }
                currentArea = a;
            }
        }
        if (currentArea != null) {
            System.out.println("Found area: " + currentArea.area_name);
        } else {
            System.out.println("No area contains id " + areaId);
            System.exit(2);
        }

        for (String line : lines) {

            // 1) Comment -> update flag
            Matcher cm = COMMENT.matcher(line);
            if (cm.find()) {
                currentDexFlag = mapCommentToFlag(cm.group(1));
                out.append(line).append(System.lineSeparator());
                continue;
            }

            // 2) Clamp walkrate/surfrate to 15
            Matcher rm = RATE.matcher(line);
            if (rm.matches()) {
                String key = rm.group(1);
                int n = Integer.parseInt(rm.group(2));
                String tail = rm.group(3); // keep anything after the number intact (commas, comments, etc.)
                int clamped = Math.min(n, 15);
                out.append(key).append(' ').append(clamped).append(tail).append(System.lineSeparator());
                continue;
            }

            // 3) Replace species on pokemon/encounter lines
            Matcher sm = SPECIES_LINE.matcher(line);
            if (sm.matches()) {

                String headToken = sm.group(1);   // "pokemon" | "encounter"
                String ws        = sm.group(2);   // original spacing
                String original  = sm.group(3);   // e.g., SPECIES_PIKACHU
                String rest      = sm.group(4);   // keep the rest intact

                // skip SPECIES_NONE
                if (original.equals("SPECIES_NONE")) {
                    out.append(line).append(System.lineSeparator());
                    continue;
                } 

                // REFILL AVAILABLEMONS IF EMPTY
                if(availableMons.isEmpty()) {
                    availableMons.addAll(RandomizerUI.STATIC_MONS);
                    availableMons.removeIf(p -> !p.encounter_valid || !p.has_all_sprites);
                    System.out.println("Refilling available mons on area " + areaId + "...");
                }

                Pokemon chosen = chosenByOriginal.get(original); // find current line's species in the chosen already list
                if (chosen == null) { // if its not chosen this group yet....
                    
                    chosen = takeRandom(availableMons).chooseAltForm(); // replace it with a random pokemon, choosing randomly between all alt forms

                    if (chosen == null) {
                        System.out.println("Error: no pokemon chosen in area " + areaId);
                        System.exit(1);
                    }

                    if(chosen.species_name.equals("SPECIES_RATICATE_ALOLAN") || chosen.species_name.equals("SPECIES_RATICATE")) {
                        System.out.println("lsas");
                    }

                    chosenByOriginal.put(original, chosen); // add the random pokemon to the chosen list
                }

                if(!currentDexFlag.equals("-1")) {
                    for (Pokemon p : monsWithAreas) {
                        if(p.species_withform.equals(chosen.species_withform.replaceFirst(",\\s*\\d+$", ""))) { // ONLY ADD TO THE POKEMON THAT IT'S A FORM OF
                            
                            DexMap<String, Area> newMap = new DexMap<>(currentDexFlag, currentArea);

                            if(!p.dex_areas.contains(newMap)) {
                                p.dex_areas.add(newMap);  // add the current area to the pre-form-check pokemon
                            }
                            
                            break;

                        }
                    }
                }

                

                // Replace "pokemon" with "monwithform" or "encounter" with "encounterwithform" 
                String newHead = headToken;
                if (chosen.form) {
                    newHead = headToken.equals("pokemon") ? "monwithform" : "encounterwithform";
                }

                // Replace only the species token; keep spacing and the tail as-is
                String replaced = newHead + ws + chosen.species_withform + rest;
                out.append(replaced).append(System.lineSeparator());
                continue;
            }

            // 4) Default: passthrough, add original line
            out.append(line).append(System.lineSeparator());
        } 

        return out.toString();

    }

    // Map comment text (after the leading //) to a flag
    private static String mapCommentToFlag(String commentText) {
        String s = commentText.trim().toLowerCase(Locale.ROOT);

        if (s.startsWith("morning encounter slots")) return "DEX_MORNING";
        if (s.startsWith("day encounter slots"))     return "DEX_DAY";
        if (s.startsWith("night encounter slots"))   return "DEX_NIGHT";

        // Everything else listed in your table maps to SPECIAL
        if (s.startsWith("hoenn encounter slots"))   return "-1";
        if (s.startsWith("sinnoh encounter slots"))  return "-1";
        if (s.startsWith("surf encounters"))         return "DEX_SPECIAL";
        if (s.startsWith("rock smash encounters"))   return "DEX_SPECIAL";
        if (s.startsWith("old rod encounters"))      return "DEX_SPECIAL";
        if (s.startsWith("good rod encounters"))     return "DEX_SPECIAL";
        if (s.startsWith("super rod encounters"))    return "DEX_SPECIAL";
        if (s.startsWith("swarm grass"))             return "-1";
        if (s.startsWith("swarm surf"))              return "-1";
        if (s.startsWith("swarm good rod"))          return "-1";
        if (s.startsWith("swarm super rod"))         return "-1";

        return "-1";
    }

    // Remove-and-return a random item from the list (O(1) swap-remove)
    public static <T> T takeRandom(List<T> list) {
        if (list.isEmpty()) return null;
        int i = ThreadLocalRandom.current().nextInt(list.size());
        Collections.swap(list, i, list.size() - 1);
        return list.remove(list.size() - 1);
    }

}
