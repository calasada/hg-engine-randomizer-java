#!/usr/bin/env python3
import argparse
import json
from pathlib import Path
import pandas as pd

# ---------------- Defaults ----------------
BASE_DIR = Path(__file__).resolve().parent

POKEMON_INPUT_DEFAULT = BASE_DIR / "PokemonData.xlsx"
POKEMON_OUTPUT_DEFAULT = BASE_DIR / "pokemondata.json"

MOVES_INPUT_DEFAULT = BASE_DIR / "MoveData.xlsx"
MOVES_OUTPUT_DEFAULT = BASE_DIR / "movedata.json"

AREAS_INPUT_DEFAULT = BASE_DIR / "AreaData.xlsx"
AREAS_OUTPUT_DEFAULT = BASE_DIR / "areadata.json"

TRAINERS_INPUT_DEFAULT = BASE_DIR / "TrainerData.xlsx"
TRAINERS_OUTPUT_DEFAULT = BASE_DIR / "trainerdata.json"

# Boolean-like columns for Pokémon data
POKEMON_BOOL_COLS = [
    "form",
    "encounter_valid",
    "trainer_valid",
    "has_all_sprites",
    "has_front_sprite",
    "starter",
    "legendary",
    "pseudolegendary",
    "ultrabeast",
    "paradox",
    "evil",
]

# Pokémon special columns
ALT_SPAWNS_COL   = "alt_spawns"     # "SPECIES_X:33,SPECIES_Y:50"
EVOLUTION_COL    = "evolution_tree" # "SPECIES_A:16,SPECIES_B:36" or JSON
MOVESET_COL      = "moveset"        # "MOVE_A,MOVE_B,MOVE_C"

# Boolean-like columns for Moves data
MOVES_BOOL_COLS = ["implemented", "status"]
AREAS_BOOL_COLS = ["special"]
TRAINERS_BOOL_COLS = ["moves_defined"]

TRUE_SET  = {"TRUE","T","YES","Y","1","true","True"}
FALSE_SET = {"FALSE","F","NO","N","0","","false","False"}

# ---------------- Helpers ----------------
def coerce_bool(val):
    s = str(val).strip()
    if s in TRUE_SET: return True
    if s in FALSE_SET: return False
    return bool(s)  # fallback: any other non-empty -> True

def read_sheet_as_df(path, sheet=None):
    """Read Excel as strings, auto-picking first sheet if not specified.
       Always return a DataFrame and preserve blanks as ""."""
    data = pd.read_excel(path, sheet_name=(sheet if sheet is not None else 0), dtype=str)
    if isinstance(data, dict):
        # Multiple sheets returned
        first_key = list(data.keys())[0]
        df = data[first_key]
    else:
        df = data
    return df.fillna("")

def parse_alt_spawns(cell, item_sep=",", kv_sep=":"):
    s = str(cell).strip()
    if not s:
        return []
    out = []
    for chunk in s.split(item_sep):
        chunk = chunk.strip()
        if not chunk:
            continue
        if kv_sep in chunk:
            name, rate_str = chunk.split(kv_sep, 1)
            name = name.strip()
            try:
                rate = float(str(rate_str).strip())
            except ValueError:
                rate = 100.0
        else:
            name = chunk
            rate = 100.0
        if name:
            out.append({"species_name": name, "rate": rate})
    return out

def parse_moveset(cell, delim=","):
    s = str(cell).strip()
    if not s:
        return []
    return [part.strip() for part in s.split(delim) if part.strip()]

def parse_evolution(cell, item_sep=",", kv_sep=":"):
    """Return a list of {"species_name": name, "level": int?}."""
    s = str(cell).strip()
    if not s:
        return []
    # Try JSON first
    if (s.startswith("[") and s.endswith("]")) or (s.startswith("{") and s.endswith("}")):
        try:
            data = json.loads(s)
            if isinstance(data, dict):
                data = [data]
            norm = []
            for item in data:
                if isinstance(item, dict):
                    pokemon = item.get("species_name")
                    level = item.get("level")
                    try:
                        level = int(level) if level is not None else None
                    except Exception:
                        level = None
                    if pokemon:
                        obj = {"species_name": str(pokemon).strip()}
                        if level is not None:
                            obj["level"] = level
                        norm.append(obj)
            return norm
        except Exception:
            pass

    # Fallback: "SPECIES_A:16,SPECIES_B:36"
    out = []
    for chunk in s.split(item_sep):
        chunk = chunk.strip()
        if not chunk:
            continue
        if kv_sep in chunk:
            name, lvl = chunk.split(kv_sep, 1)
            name = name.strip()
            try:
                level = int(str(lvl).strip())
            except ValueError:
                level = None
            if name:
                obj = {"species_name": name}
                if level is not None:
                    obj["level"] = level
                out.append(obj)
        else:
            # No level provided
            name = chunk
            if name:
                out.append({"species_name": name})
    return out

# ---------------- Converters ----------------
def convert_pokemon_excel_to_json(input_xlsx, output_json, sheet=None):
    in_path = Path(input_xlsx)
    if not in_path.exists():
        raise FileNotFoundError(f"Pokemon Excel not found: {in_path.resolve()}")

    df = read_sheet_as_df(in_path, sheet=sheet)

    records = []
    for _, row in df.iterrows():
        rec = {col: ("" if str(row[col]) == "" else str(row[col])) for col in df.columns}

        # Booleans
        for b in POKEMON_BOOL_COLS:
            if b in rec:
                rec[b] = coerce_bool(rec[b])

        # Specials
        if ALT_SPAWNS_COL in rec:
            rec[ALT_SPAWNS_COL] = parse_alt_spawns(row[ALT_SPAWNS_COL])

        if EVOLUTION_COL in rec:
            rec[EVOLUTION_COL] = parse_evolution(row[EVOLUTION_COL])

        if MOVESET_COL in rec:
            rec[MOVESET_COL] = parse_moveset(row[MOVESET_COL])

        records.append(rec)

    out_path = Path(output_json)
    out_path.write_text(json.dumps(records, indent=2, ensure_ascii=False), encoding="utf-8")
    return len(records), out_path

def convert_moves_excel_to_json(input_xlsx, output_json, sheet=None):
    in_path = Path(input_xlsx)
    if not in_path.exists():
        raise FileNotFoundError(f"Moves Excel not found: {in_path.resolve()}")

    df = read_sheet_as_df(in_path, sheet=sheet)

    records = []
    for _, row in df.iterrows():
        rec = {col: ("" if str(row[col]) == "" else str(row[col])) for col in df.columns}
        for b in MOVES_BOOL_COLS:
            if b in rec:
                rec[b] = coerce_bool(rec[b])
        records.append(rec)

    out_path = Path(output_json)
    out_path.write_text(json.dumps(records, indent=2, ensure_ascii=False), encoding="utf-8")
    return len(records), out_path

def convert_areas_excel_to_json(input_xlsx, output_json, sheet=None):
    in_path = Path(input_xlsx)
    if not in_path.exists():
        raise FileNotFoundError(f"Areas Excel not found: {in_path.resolve()}")

    df = read_sheet_as_df(in_path, sheet=sheet)

    records = []
    for _, row in df.iterrows():
        rec = {col: ("" if str(row[col]) == "" else str(row[col])) for col in df.columns}

        # Convert booleans
        for b in AREAS_BOOL_COLS:
            if b in rec:
                rec[b] = coerce_bool(rec[b])

        # Parse encounter_ids as list of ints
        if "encounter_ids" in rec:
            raw = str(row["encounter_ids"]).strip()
            if raw:
                try:
                    rec["encounter_ids"] = [int(x.strip()) for x in raw.split(",") if x.strip()]
                except ValueError:
                    # If not valid integers, fall back to empty list
                    rec["encounter_ids"] = []
            else:
                rec["encounter_ids"] = []

        records.append(rec)

    out_path = Path(output_json)
    out_path.write_text(json.dumps(records, indent=2, ensure_ascii=False), encoding="utf-8")
    return len(records), out_path

def convert_trainers_excel_to_json(input_xlsx, output_json, sheet=None):
    in_path = Path(input_xlsx)
    if not in_path.exists():
        raise FileNotFoundError(f"Trainers Excel not found: {in_path.resolve()}")

    df = read_sheet_as_df(in_path, sheet=sheet)

    records = []
    for _, row in df.iterrows():
        rec = {col: ("" if str(row[col]) == "" else str(row[col])) for col in df.columns}

        for b in TRAINERS_BOOL_COLS:
            if b in rec:
                rec[b] = coerce_bool(rec[b])

        records.append(rec)

    out_path = Path(output_json)
    out_path.write_text(json.dumps(records, indent=2, ensure_ascii=False), encoding="utf-8")
    return len(records), out_path

# ---------------- CLI ----------------
def main():
    ap = argparse.ArgumentParser(description="Convert PokemonData.xlsx and MoveData.xlsx to JSON in one go.")
    ap.add_argument("--pokemon-xlsx", default=POKEMON_INPUT_DEFAULT, help="Path to PokemonData.xlsx")
    ap.add_argument("--pokemon-json", default=POKEMON_OUTPUT_DEFAULT, help="Output pokemondata.json")
    ap.add_argument("--pokemon-sheet", default=None, help="Pokemon sheet name/index (default: first)")

    ap.add_argument("--moves-xlsx", default=MOVES_INPUT_DEFAULT, help="Path to MoveData.xlsx")
    ap.add_argument("--moves-json", default=MOVES_OUTPUT_DEFAULT, help="Output movedata.json")
    ap.add_argument("--moves-sheet", default=None, help="Moves sheet name/index (default: first)")

    ap.add_argument("--areas-xlsx", default=AREAS_INPUT_DEFAULT, help="Path to AreaData.xlsx")
    ap.add_argument("--areas-json", default=AREAS_OUTPUT_DEFAULT, help="Output areadata.json")
    ap.add_argument("--areas-sheet", default=None, help="Areas sheet name/index (default: first)")
    
    ap.add_argument("--trainers-xlsx", default=TRAINERS_INPUT_DEFAULT, help="Path to TrainerData.xlsx")
    ap.add_argument("--trainers-json", default=TRAINERS_OUTPUT_DEFAULT, help="Output trainerdata.json")
    ap.add_argument("--trainers-sheet", default=None, help="Trainers sheet name/index (default: first)")

    args = ap.parse_args()

    poke_count, poke_out = convert_pokemon_excel_to_json(args.pokemon_xlsx, args.pokemon_json, sheet=args.pokemon_sheet)
    moves_count, moves_out = convert_moves_excel_to_json(args.moves_xlsx, args.moves_json, sheet=args.moves_sheet)
    areas_count, areas_out = convert_areas_excel_to_json(args.areas_xlsx, args.areas_json, sheet=args.areas_sheet)
    trainers_count, trainers_out = convert_trainers_excel_to_json(args.trainers_xlsx, args.trainers_json, sheet=args.trainers_sheet)

    print(f"Wrote {poke_count} Pokémon -> {poke_out.resolve()}")
    print(f"Wrote {moves_count} Moves -> {moves_out.resolve()}")
    print(f"Wrote {areas_count} Areas -> {areas_out.resolve()}")
    print(f"Wrote {trainers_count} Trainers -> {trainers_out.resolve()}")

if __name__ == "__main__":
    main()
