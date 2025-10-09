/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app;

import java.util.List;

// same fields as Pokemon EXCEPT alt_spawns are refs by id+rate
public class RawPokemon {
    public int id;
    public String species_name;
    public String species_withform;
    public boolean form;
    public boolean encounter_valid;
    public boolean trainer_valid;
    public boolean starter;
    public boolean legendary;
    public boolean pseudolegendary;
    public boolean ultrabeast;
    public boolean paradox;
    public Type typeA;
    public Type typeB;
    public List<AltSpawnRef> alt_spawns;
}


