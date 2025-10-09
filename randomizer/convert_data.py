# GENERATED ENTIRELY BY CHATGPT

import json
import math
import pandas as pd
from pathlib import Path

INPUT_XLSX = Path("PokemonData.xlsx")          # your Excel file (same dir as this script)
OUTPUT_JSON = Path("pokemondata.json")         # output file
ALT_COL = "alt_spawns"                         # column containing "SPECIES_X:rate,SPECIES_Y:rate"

# Columns that should be treated as booleans if they come in as strings TRUE/FALSE
BOOL_COLS = [
    "form","encounter_valid","trainer_valid","starter",
    "legendary","pseudolegendary","ultrabeast","paradox",
]

def to_bool(v):
    if isinstance(v, bool):
        return v
    if v is None or (isinstance(v, float) and math.isnan(v)):
        return False
    s = str(v).strip().upper()
    if s in {"TRUE","T","YES","Y","1"}:
        return True
    if s in {"FALSE","F","NO","N","0",""}:
        return False
    # fallback: keep original (or cast to bool?) — we’ll default to False to be safe
    return False

def parse_alt_spawns(cell: str):
    """
    Parse a string like:
      "SPECIES_PIKACHU_ROCK_STAR:33,SPECIES_PIKACHU_BELLE:33"
    into:
      [{"species":"SPECIES_PIKACHU_ROCK_STAR","rate":33}, {"species":"SPECIES_PIKACHU_BELLE","rate":33}]
    """
    if cell is None or (isinstance(cell, float) and math.isnan(cell)):
        return []
    s = str(cell).strip()
    if not s:
        return []
    out = []
    # split by commas, then each into name:rate
    for chunk in s.split(","):
        chunk = chunk.strip()
        if not chunk:
            continue
        if ":" in chunk:
            name, rate_str = chunk.split(":", 1)
            name = name.strip()
            try:
                rate = int(rate_str.strip())
            except ValueError:
                rate = 100  # default if malformed
        else:
            # no rate provided → default 100
            name = chunk
            rate = 100
        if name:
            out.append({"species_name": name, "rate": rate})
    return out

def main():
    if not INPUT_XLSX.exists():
        raise FileNotFoundError(f"Excel file not found: {INPUT_XLSX.resolve()}")

    df = pd.read_excel(INPUT_XLSX)

    # Normalize boolean-like columns
    for col in BOOL_COLS:
        if col in df.columns:
            df[col] = df[col].map(to_bool)

    # Build list of dicts row-by-row
    records = []
    cols = list(df.columns)

    for _, row in df.iterrows():
        rec = {}
        for col in cols:
            val = row[col]
            # Convert NaN to empty string or None based on field name preference
            if isinstance(val, float) and math.isnan(val):
                # For typeB (often empty), keep "" to match your existing JSON
                rec[col] = "" if col.lower() in {"typeb"} else None
            else:
                rec[col] = val

        # Parse alt spawns column into nested list of objects
        if ALT_COL in rec:
            rec["alt_spawns"] = parse_alt_spawns(rec[ALT_COL])
            # Drop the original text form if you don’t want it duplicated:
            # del rec[ALT_COL]

        records.append(rec)

    # Optional: clean up None values in non-boolean/string columns:
    # (Comment out if you want to keep explicit nulls.)
    for r in records:
        # Example: ensure typeB is "" not None
        if "typeB" in r and r["typeB"] is None:
            r["typeB"] = ""

    with OUTPUT_JSON.open("w", encoding="utf-8") as f:
        json.dump(records, f, indent=2, ensure_ascii=False)

    print(f"Wrote {len(records)} Pokémon → {OUTPUT_JSON.resolve()}")

if __name__ == "__main__":
    main()
