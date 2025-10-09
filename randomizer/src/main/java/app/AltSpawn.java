package app;

public class AltSpawn {

    public Pokemon pokemon;
    public int rate;

    public AltSpawn(Pokemon pokemon, int rate) {
        this.pokemon = pokemon;
        this.rate = rate;
    }

    @Override public String toString() {
        return "id: " + pokemon.toString() + ", rate: " + rate;
    }

}
