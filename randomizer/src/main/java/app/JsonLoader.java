package app;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

public final class JsonLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

    static {
        // bind non-public fields if you change visibility later
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // "TRUE"/"FALSE" -> boolean
        MAPPER.coercionConfigFor(LogicalType.Boolean)
              .setCoercion(CoercionInputShape.String, CoercionAction.TryConvert);

        // "" -> null for enums (typeB can be "")
        MAPPER.coercionConfigFor(LogicalType.Enum)
              .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
    }

    private JsonLoader() {}

    public static List<Move> loadMoves(String resourcePath) {
        List<Move> raws = null;
        try {
            raws = readRawMoves(resourcePath);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return raws;
    }

    public static List<Area> loadAreas(String resourcePath) {
        List<Area> raws = null;
        try {
            raws = readRawAreas(resourcePath);
        } catch (Exception ex) {
            System.out.println("it fked up");
        }
        return raws;
    }

    public static List<Pokemon> loadPokemon(String resourcePath, List<Move> moves) {
        // create list of RawPokemon with basic data
        List<RawPokemon> raws = null;
        try {
            raws = readRawPokemon(resourcePath);
        } catch (Exception ex) {
            System.out.println("it fked up");
            System.exit(3);
        }

        // map moves to their move_name
        Map<String, Move> moveById = new HashMap<>(moves.size());
        for (Move m : moves) {
            moveById.put(m.move_name, m);
        }

        // Pass 1: build id -> Pokemon WITHOUT alt_spawns,evolution_tree,moveset
        Map<String, Pokemon> pokeById = new HashMap<>(raws.size());
        for (RawPokemon r : raws) {
            Pokemon p = new Pokemon();
            p.id = r.id;
            p.species_name = r.species_name;
            p.species_withform = r.species_withform;
            p.tier = r.tier;
            p.form = r.form;
            p.encounter_valid = r.encounter_valid;
            p.trainer_valid = r.trainer_valid;
            p.has_all_sprites = r.has_all_sprites;
            p.has_front_sprite = r.has_front_sprite;
            p.stage_1 = r.stage_1;
            p.starter = r.starter;
            p.legendary = r.legendary;
            p.pseudolegendary = r.pseudolegendary;
            p.ultrabeast = r.ultrabeast;
            p.paradox = r.paradox;
            p.evil = r.evil;
            p.typeA = r.typeA;
            p.typeB = r.typeB;
            p.attack_type = r.attack_type;
            // alt_spawns,evolution_tree set in Pass 2
            pokeById.put(p.species_name, p);
        }

        // Pass 2: resolve alt_spawns and evolution_tree, moveset
        for (RawPokemon r : raws) {
            Pokemon p = pokeById.get(r.species_name);
            if (r.alt_spawns == null || r.alt_spawns.isEmpty()) {
                p.alt_spawns = List.of();
            }
            if(r.evolution_tree == null || r.evolution_tree.isEmpty()) {
                p.evolution_tree = List.of();
            }
            if(r.moveset == null || r.moveset.isEmpty()) {
                p.moveset = List.of();
            }

            List<AltSpawn> resolvedAltSpawns = new ArrayList<>(r.alt_spawns.size());
            List<Evolution> resolvedEvolutionTree = new ArrayList<>(r.alt_spawns.size());
            List<Move> resolvedMoveset = new ArrayList<>(r.moveset.size());

            // Add each AltSpawn into the temp resolvedAltSpawns List using the species name of the ref      
            for (AltSpawnRef refSpawns : r.alt_spawns) {
                Pokemon target = pokeById.get(refSpawns.species_name);
                resolvedAltSpawns.add(new AltSpawn(target, refSpawns.rate));
            }
            // Add each Evolution into the temp resolvedEvolution List using the species name of the ref            
            for (EvolutionRef refEvolutions : r.evolution_tree) {
                Pokemon target = pokeById.get(refEvolutions.species_name);
                resolvedEvolutionTree.add(new Evolution(target, refEvolutions.level));
            }

            for (String s : r.moveset) {
                Move target = moveById.get(s);
                resolvedMoveset.add(target);
            }


            p.alt_spawns = Collections.unmodifiableList(resolvedAltSpawns);
            p.evolution_tree = Collections.unmodifiableList(resolvedEvolutionTree);
            p.moveset = Collections.unmodifiableList(resolvedMoveset);
        }

        // Return stable order matching the input (optional)
        return raws.stream().map(r -> pokeById.get(r.species_name)).collect(Collectors.toList());
    }

    public static List<Trainer> loadTrainers(String resourcePath) {
        List<Trainer> raws = null;
        try {
            raws = readRawTrainers(resourcePath);
        } catch (Exception ex) {
            System.out.println("it fked up");
        }
        return raws;
    }

    // --- internal: read raw list from classpath ---
    private static List<Move> readRawMoves(String resourcePath) throws Exception {
        try (InputStream in = JsonLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + resourcePath);
            return MAPPER.readValue(in, new TypeReference<List<Move>>() {});
        }
    }

    // --- internal: read raw list from classpath ---
    private static List<Area> readRawAreas(String resourcePath) throws Exception {
        try (InputStream in = JsonLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + resourcePath);
            return MAPPER.readValue(in, new TypeReference<List<Area>>() {});
        }
    }

    // --- internal: read raw list from classpath ---
    private static List<RawPokemon> readRawPokemon(String resourcePath) throws Exception {
        try (InputStream in = JsonLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + resourcePath);
            return MAPPER.readValue(in, new TypeReference<List<RawPokemon>>() {});
        }
    }

    // --- internal: read raw list from classpath ---
    private static List<Trainer> readRawTrainers(String resourcePath) throws Exception {
        try (InputStream in = JsonLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + resourcePath);
            return MAPPER.readValue(in, new TypeReference<List<Trainer>>() {});
        }
    }
}
