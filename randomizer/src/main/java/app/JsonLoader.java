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

    /** Load, then resolve alt_spawns (ids -> Pokemon references). */
    public static List<Pokemon> loadResolvedPokemon(String resourcePath) throws Exception {
        List<RawPokemon> raws = readRaw(resourcePath);

        // Pass 1: build id -> Pokemon WITHOUT alt_spawns
        Map<String, Pokemon> byId = new HashMap<>(raws.size());
        for (RawPokemon r : raws) {
            Pokemon p = new Pokemon();
            p.id = r.id;
            p.species_name = r.species_name;
            p.species_withform = r.species_withform;
            p.form = r.form;
            p.encounter_valid = r.encounter_valid;
            p.trainer_valid = r.trainer_valid;
            p.starter = r.starter;
            p.legendary = r.legendary;
            p.pseudolegendary = r.pseudolegendary;
            p.ultrabeast = r.ultrabeast;
            p.paradox = r.paradox;
            p.typeA = r.typeA;
            p.typeB = r.typeB;
            // alt_spawns set in Pass 2
            byId.put(p.species_name, p);
        }

        // Pass 2: resolve alt_spawns
        for (RawPokemon r : raws) {
            Pokemon p = byId.get(r.species_name);
            if (r.alt_spawns == null || r.alt_spawns.isEmpty()) {
                p.alt_spawns = List.of();
                continue;
            }
            List<AltSpawn> resolved = new ArrayList<>(r.alt_spawns.size());
            for (AltSpawnRef ref : r.alt_spawns) {
                Pokemon target = byId.get(ref.species_name);
                if (target == null) {
                    // Optional: log or throw if a reference is broken
                    // System.err.println("Missing alt spawn id " + ref.id + " for mon " + p.id);
                    continue;
                }
                resolved.add(new AltSpawn(target, ref.rate));
            }
            p.alt_spawns = Collections.unmodifiableList(resolved);
        }

        // Return stable order matching the input (optional)
        return raws.stream().map(r -> byId.get(r.species_name)).collect(Collectors.toList());
    }

    // --- internal: read raw list from classpath ---
    private static List<RawPokemon> readRaw(String resourcePath) throws Exception {
        try (InputStream in = JsonLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("Resource not found: " + resourcePath);
            return MAPPER.readValue(in, new TypeReference<List<RawPokemon>>() {});
        }
    }
}
