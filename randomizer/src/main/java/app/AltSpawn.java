package app;

public class AltSpawn {

    public Pokemon pokemon;
    public double rate;

    public AltSpawn(Pokemon pokemon, double rate) {
        this.pokemon = pokemon;
        this.rate = rate;
    }

    @Override public String toString() {
        return "id: " + (this.pokemon == null ? "" : pokemon.toString()) + ", rate: " + rate;
    }

}
