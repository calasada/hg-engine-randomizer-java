package app;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RandomizerUI {

    public static final String STARTERS_INPUT_PATH = "starters_ORIGINAL.s";
    public static final String STARTERS_OUTPUT_PATH = "armips/data/starters.s";

    public static final String ENCOUNTERS_INPUT_PATH = "encounters_ORIGINAL.s";
    public static final String ENCOUNTERS_OUTPUT_PATH = "armips/data/encounters.s";

    public static final String TRAINERS_INPUT_PATH = "trainers_ORIGINAL.s";
    public static final String TRAINERS_OUTPUT_PATH = "armips/data/trainers/trainers.s";

    public static final String DEXAREAS_INPUT_PATH = "areadata_ORIGINAL.s";
    public static final String DEXAREAS_OUTPUT_PATH = "armips/data/pokedex/areadata.s";

    public static final String EVODATA_ORIGINAL_INPUT_PATH = "evodata_ORIGINAL.s";
    public static final String EVODATA_CLAMPED_INPUT_PATH = "evodata_CLAMPED.s";
    public static final String EVODATA_OUTPUT_PATH = "armips/data/evodata.s";

    public static final String TMDATA_INPUT_PATH = "mart_ORIGINAL.c";
    public static final String TMDATA_OUTPUT_PATH = "src/field/mart.c";

    public static final List<Move> STATIC_MOVES = initMoves();
    public static final List<Area> STATIC_AREAS = initAreas();
    public static final List<Pokemon> STATIC_MONS = initPokemon();
    public static final List<Trainer> STATIC_TRAINERS = initTrainers();

    public static final int TM_AMOUNT = 20;

    public static Map<Integer, Type> static_gymTypeMap;

    public static List<Move> initMoves() {
        // Load list of moves from movedata.json using the JsonLoader
        List<Move> tempMoves = JsonLoader.loadMoves("/movedata.json");
        System.out.println("Loaded " + tempMoves.size() + " Moves");
        //if(!tempMoves.isEmpty()) { for(Move move : tempMoves) {System.out.println(move.toString()); }} // print all moves loaded
        return tempMoves;
    }

    public static List<Area> initAreas() {
        // Load list of areas from areadata.json using the JsonLoader
        List<Area> tempAreas = JsonLoader.loadAreas("/areadata.json");
        System.out.println("Loaded " + tempAreas.size() + " Areas");
        //if(!tempAreas.isEmpty()) { for(Area area : tempAreas) {System.out.println(area.toString()); }} // print all areas loaded
        return tempAreas;
    }
        
    public static List<Pokemon> initPokemon() {
        // Load list of pokemon from pokemondata.json using the JsonLoader and the list of moves
        List<Pokemon> tempPokemon = JsonLoader.loadPokemon("/pokemondata.json", STATIC_MOVES);
        System.out.println("Loaded " + tempPokemon.size() + " Pokémon");
        //if (!tempPokemon.isEmpty()) { for (Pokemon mon : tempPokemon) { System.out.println(mon.toString()); }} // print all mons loaded
        return tempPokemon;
    }

    public static List<Trainer> initTrainers() {
        // Load list of pokemon from pokemondata.json using the JsonLoader and the list of moves
        List<Trainer> tempTrainers = JsonLoader.loadTrainers("/trainerdata.json");
        System.out.println("Loaded " + tempTrainers.size() + " Pokémon");
       // if (!tempTrainers.isEmpty()) { for (Trainer trainer : tempTrainers) { System.out.println(trainer.toString()); }} // print all trainers loaded
        return tempTrainers;
    }

    public static Map<Integer, Type> initGymTypeMap(boolean random) {
        Map<Integer, Type> tempMap = new HashMap<>();
        if(random) {
            List<Type> availableTypes = new ArrayList<>(Arrays.asList(Type.values()));
            tempMap.put(1, Area.takeRandom(availableTypes));
            tempMap.put(2, Area.takeRandom(availableTypes));
            tempMap.put(3, Area.takeRandom(availableTypes));
            tempMap.put(4, Area.takeRandom(availableTypes));
            tempMap.put(5, Area.takeRandom(availableTypes));
            tempMap.put(6, Area.takeRandom(availableTypes));
            tempMap.put(7, Area.takeRandom(availableTypes));
            tempMap.put(8, Area.takeRandom(availableTypes));
            tempMap.put(9, Area.takeRandom(availableTypes));
            tempMap.put(10, Area.takeRandom(availableTypes));
            tempMap.put(11, Area.takeRandom(availableTypes));
            tempMap.put(12, Area.takeRandom(availableTypes));
            availableTypes = new ArrayList<>(Arrays.asList(Type.values()));
            tempMap.put(13, Area.takeRandom(availableTypes));
            tempMap.put(14, Area.takeRandom(availableTypes));
            tempMap.put(15, Area.takeRandom(availableTypes));
            tempMap.put(16, Area.takeRandom(availableTypes));
            tempMap.put(17, Area.takeRandom(availableTypes));
            tempMap.put(18, Area.takeRandom(availableTypes));
            tempMap.put(19, Area.takeRandom(availableTypes));
        } else {
            tempMap.put(1, Type.FLYING);
            tempMap.put(2, Type.BUG);
            tempMap.put(3, Type.NORMAL);
            tempMap.put(4, Type.GHOST);
            tempMap.put(5, Type.FIGHTING);
            tempMap.put(6, Type.STEEL);
            tempMap.put(7, Type.ICE);
            tempMap.put(8, Type.DRAGON);
            tempMap.put(9, Type.PSYCHIC);
            tempMap.put(10, Type.POISON);
            tempMap.put(11, Type.FIGHTING);
            tempMap.put(12, Type.DARK);
            tempMap.put(13, Type.ROCK);
            tempMap.put(14, Type.WATER);
            tempMap.put(15, Type.ELECTRIC);
            tempMap.put(16, Type.GRASS);
            tempMap.put(17, Type.POISON);
            tempMap.put(18, Type.PSYCHIC);
            tempMap.put(19, Type.FIRE);
        }
        
        return tempMap;
    }

    public static void main(String[] args) {

        

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); // Windows look and feel
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }

        SwingUtilities.invokeLater(() -> new RandomizerUI().createAndShowGUI());
        
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Pokémon Randomizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setResizable(false);

        //Title
        JLabel titleLabel = new JLabel("Choose options:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH); //Added to top middle

        // Option checkboxes
        JCheckBox randomStarters = new JCheckBox("Random Starters");
        JCheckBox randomEncounters = new JCheckBox("Random Encounters");
        JCheckBox randomTrainers = new JCheckBox("Random Trainers");
        JCheckBox keepGymTypes = new JCheckBox("Keep Original Gym/E4 Types");
        JCheckBox clampEvolution = new JCheckBox("Clamp Evolution Levels (25, 40)");
        JCheckBox randomizeMarts = new JCheckBox("Randomize Mart Items/TMs");

        // JPanel of checkboxes
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.add(randomStarters);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(randomEncounters);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(randomTrainers);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(keepGymTypes);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(clampEvolution);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(randomizeMarts);

        frame.add(checkBoxPanel, BorderLayout.CENTER);

        JButton goButton = new JButton("Go");
        goButton.addActionListener(e -> {

            // --- RANDOM STARTERS ---
            if (randomStarters.isSelected()) {
                System.out.println("Randomizing Starters...");

                try {
                    randomizeStarters();
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }

            } else {
                try {
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(STARTERS_INPUT_PATH), Paths.get(STARTERS_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }
            }

            // --- RANDOM ENCOUNTERS ---
            if (randomEncounters.isSelected()) {
                System.out.println("Randomizing Encounters...");

                try {
                    randomizeEncounters();
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }

            } else {
                try {
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(ENCOUNTERS_INPUT_PATH), Paths.get(ENCOUNTERS_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(DEXAREAS_INPUT_PATH), Paths.get(DEXAREAS_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }
            }

            // --- RANDOM TRAINERS ---
            if (randomTrainers.isSelected()) {
                System.out.println("Randomizing Trainers...");

                if(keepGymTypes.isSelected()) {
                    static_gymTypeMap = initGymTypeMap(false);
                } else {
                    static_gymTypeMap = initGymTypeMap(true);
                }


                try {
                    randomizeTrainers();
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }

            } else {
                try {
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(TRAINERS_INPUT_PATH), Paths.get(TRAINERS_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }
            }

            // --- CLAMP EVOLUTION ---
            if (clampEvolution.isSelected()) {
                System.out.println("Clamping Evolution Levels...");

                try {
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(EVODATA_CLAMPED_INPUT_PATH), Paths.get(EVODATA_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }

            } else {
                try {
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(EVODATA_ORIGINAL_INPUT_PATH), Paths.get(EVODATA_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }
            }

            // --- RANDOM MARTS ---
            if (randomizeMarts.isSelected()) {
                System.out.println("Randomizing Marts...");

                try {
                    randomizeMarts();
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }

            } else {
                try {
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(TMDATA_INPUT_PATH), Paths.get(TMDATA_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }
            }


            frame.dispose();

        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(goButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void randomizeStarters() throws IOException {
        List<Pokemon> availableMons = new ArrayList<>(STATIC_MONS);

        availableMons.removeIf(p -> !p.encounter_valid || !p.has_all_sprites || !p.starter);
        
        final Pattern BLOCK_START = Pattern.compile(".macro\\b.*"); // top of block delimiter
        final Pattern BLOCK_END = Pattern.compile(".endmacro\\b.*"); // bottom of block delimiter

        // Resolve the input from resources on the runtime classpath
        InputStream raw = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(STARTERS_INPUT_PATH);

        if (raw == null) {
            throw new FileNotFoundException(
                "Resource not found on classpath: " + STARTERS_INPUT_PATH +
                " (put the file under src/main/resources, and use the classpath name only)"
            );
        }

        Path outPath = Paths.get(STARTERS_OUTPUT_PATH).toAbsolutePath().normalize();
        Files.createDirectories(outPath.getParent());
        System.out.println("[starters] writing to: " + outPath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(raw, StandardCharsets.UTF_8));
             BufferedWriter bw = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            boolean inBlock = false;

            String line;
            while ((line = br.readLine()) != null) {

                boolean startsBlock = BLOCK_START.matcher(line).matches();
                boolean endsBlock = BLOCK_END.matcher(line).matches();
                if (startsBlock) {
                    inBlock = true;
                }
                if (endsBlock) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(".macro STARTER_CHOICES").append(System.lineSeparator());
                    builder.append("   .word ").append(Area.takeRandom(availableMons).species_name).append(System.lineSeparator());
                    builder.append("   .word ").append(Area.takeRandom(availableMons).species_name).append(System.lineSeparator());
                    builder.append("   .word ").append(Area.takeRandom(availableMons).species_name).append(System.lineSeparator());
                    bw.append(builder.toString());
                    inBlock = false;
                }
                
                if(!inBlock) {
                    bw.append(line).append(System.lineSeparator());
                }
                
            }
        }

    }


    public static void randomizeEncounters() throws IOException {
        List<Pokemon> availableMons = new ArrayList<>(STATIC_MONS);
        List<Pokemon> monsWithAreas = new ArrayList<>(STATIC_MONS);

        availableMons.removeIf(p -> !p.encounter_valid || !p.has_all_sprites);
        monsWithAreas.removeIf(p -> !p.encounter_valid || !p.has_all_sprites || p.form);
        
        final Pattern ENCOUNTER_BLOCK_START = Pattern.compile(".*\\bencounterdata\\b.*"); // top of block delimiter
        final Pattern DEXAREA_BLOCK_START = Pattern.compile("^\\s*(routesandcities|specialareas)\\b.*\\bSPECIES_(?!NONE\\b)[A-Z0-9_]+\\b.*",Pattern.CASE_INSENSITIVE); // top of block delimiter

        // Resolve the input from resources on the runtime classpath
        InputStream encountersRaw = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(ENCOUNTERS_INPUT_PATH);

        if (encountersRaw == null) {
            throw new FileNotFoundException(
                "Resource not found on classpath: " + ENCOUNTERS_INPUT_PATH +
                " (put the file under src/main/resources, and use the classpath name only)"
            );
        }

        Path encountersOutPath = Paths.get(ENCOUNTERS_OUTPUT_PATH).toAbsolutePath().normalize();
        Files.createDirectories(encountersOutPath.getParent());
        System.out.println("[encounters] writing to: " + encountersOutPath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(encountersRaw, StandardCharsets.UTF_8));
             BufferedWriter bw = Files.newBufferedWriter(encountersOutPath, StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            StringBuilder blockBuf = new StringBuilder();
            boolean inBlock = false;
            int blockIndex = -1;

            String line;
            while ((line = br.readLine()) != null) {
                boolean startsBlock = ENCOUNTER_BLOCK_START.matcher(line).matches();

                if (startsBlock) {
                    // Finish and flush the previous block (if any)
                    if (inBlock) {
                        String processed = Area.processAreaEncounterBlock(blockBuf.toString(), blockIndex, availableMons, monsWithAreas);
                        bw.write(processed);
                        // Optional: ensure a newline separation between blocks if your processor strips trailing newline
                        // bw.write(System.lineSeparator());
                        blockBuf.setLength(0);
                    }
                    // Start a new block and include the delimiter line in it
                    inBlock = true;
                    blockIndex++;
                    blockBuf.append(line).append(System.lineSeparator());
                } else {
                    if (inBlock) {
                        // Accumulate content for the current block
                        blockBuf.append(line).append(System.lineSeparator());
                    } else {
                        // Preamble before the first block: pass through unchanged
                        bw.write(line);
                        bw.write(System.lineSeparator());
                    }
                }
            }

            // Flush the final block if we were in one
            if (inBlock && blockBuf.length() > 0) {
                String processed = Area.processAreaEncounterBlock(blockBuf.toString(), blockIndex, availableMons, monsWithAreas);
                bw.write(processed);
            }
        }

        InputStream dexAreasRaw = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(DEXAREAS_INPUT_PATH);

        if (dexAreasRaw == null) {
            throw new FileNotFoundException(
                "Resource not found on classpath: " + DEXAREAS_INPUT_PATH +
                " (put the file under src/main/resources, and use the classpath name only)"
            );
        }

        Path dexAreasOutPath = Paths.get(DEXAREAS_OUTPUT_PATH).toAbsolutePath().normalize();
        Files.createDirectories(dexAreasOutPath.getParent());
        System.out.println("[dex entries] writing to: " + dexAreasOutPath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(dexAreasRaw, StandardCharsets.UTF_8));
             BufferedWriter bw = Files.newBufferedWriter(dexAreasOutPath, StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            String line;
            while ((line = br.readLine()) != null) {
                // If this line is the first match, stop here (exclude this line and everything after)
                if (DEXAREA_BLOCK_START.matcher(line).matches()) {
                    break;
                }
                // Otherwise, keep the line
                bw.append(line).append(System.lineSeparator());
            }

            for(Pokemon p : monsWithAreas) {
                bw.append(p.buildDexData());
            }

            bw.flush();

        }

    }


    public static void randomizeTrainers() throws IOException{

        final Pattern TRAINER_BLOCK_START = Pattern.compile(".*\\btrainerdata\\b.*"); // top of block delimiter

        Trainer.generateRivalTeam();

        // Resolve the input from resources on the runtime classpath
        InputStream trainersRaw = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(TRAINERS_INPUT_PATH);

        if (trainersRaw == null) {
            throw new FileNotFoundException(
                "Resource not found on classpath: " + TRAINERS_INPUT_PATH +
                " (put the file under src/main/resources, and use the classpath name only)"
            );
        }

        Path trainersOutPath = Paths.get(TRAINERS_OUTPUT_PATH).toAbsolutePath().normalize();
        Files.createDirectories(trainersOutPath.getParent());
        System.out.println("[trainers] writing to: " + trainersOutPath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(trainersRaw, StandardCharsets.UTF_8));
             BufferedWriter bw = Files.newBufferedWriter(trainersOutPath, StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {


            boolean inBlock = false;
            int blockIndex = -1;
            StringBuilder blockBuf = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                boolean startsBlock = TRAINER_BLOCK_START.matcher(line).matches();

                if (startsBlock) {
                    // Finish and flush the previous block (if any)
                    if (inBlock) {
                        String processed = Trainer.processTrainerBlock(blockBuf.toString(), blockIndex);
                        bw.write(processed);
                        // Optional: ensure a newline separation between blocks if your processor strips trailing newline
                        // bw.write(System.lineSeparator());
                        blockBuf.setLength(0);
                    }
                    // Start a new block and include the delimiter line in it
                    inBlock = true;
                    blockIndex++;
                    blockBuf.append(line).append(System.lineSeparator());
                } else {
                    if (inBlock) {
                        // Accumulate content for the current block
                        blockBuf.append(line).append(System.lineSeparator());
                    } else {
                        // Preamble before the first block: pass through unchanged
                        bw.write(line);
                        bw.write(System.lineSeparator());
                    }
                }
            }

            // Flush the final block if we were in one
            if (inBlock && blockBuf.length() > 0) {
                String processed = Trainer.processTrainerBlock(blockBuf.toString(), blockIndex);
                bw.write(processed);
            }

        }

    }

    public static void randomizeMarts() throws IOException{

        final Pattern DEPTSTORE_BLOCK_START = Pattern.compile("^u16\\s+sGoldenrodDepartment5F\\[\\]\\s*=\\s*\\{$"); 
        final Pattern CHERRYGROVE_BLOCK_START = Pattern.compile("^u16\\s+sCherrygroveCityMart\\[\\]\\s*=\\s*\\{$"); 
        final Pattern VIOLET_BLOCK_START = Pattern.compile("^u16\\s+sVioletCityMart\\[\\]\\s*=\\s*\\{$"); 
        final Pattern AZALEA_BLOCK_START = Pattern.compile("^u16\\s+sAzaleaCityMart\\[\\]\\s*=\\s*\\{$"); 
        final Pattern ECRUTEAK_BLOCK_START = Pattern.compile("^u16\\s+sEcruteakMart\\[\\]\\s*=\\s*\\{$"); 
        final Pattern OLIVINE_BLOCK_START = Pattern.compile("^u16\\s+sOlivineMart\\[\\]\\s*=\\s*\\{$"); 
        final Pattern SAFFRON_BLOCK_START = Pattern.compile("^u16\\s+sSaffronMart\\[\\]\\s*=\\s*\\{$"); 
        final Pattern LAVENDER_BLOCK_START = Pattern.compile("^u16\\s+sLavenderMart\\[\\]\\s*=\\s*\\{$"); 
        final Pattern CERULEAN_BLOCK_START = Pattern.compile("^u16\\s+sCeruleanMart\\[\\]\\s*=\\s*\\{$"); 

        final Pattern MART_BLOCK_END = Pattern.compile("^\\};$");

        // load tms from tmlist.json
        List<String> tmList = new ObjectMapper().readValue(
            Thread.currentThread().getContextClassLoader().getResourceAsStream("tmdata.json"),
            new TypeReference<List<String>>() {}
        );
        
        // load items from itemlist.json
        List<String> itemList = new ObjectMapper().readValue(
            Thread.currentThread().getContextClassLoader().getResourceAsStream("itemdata.json"),
            new TypeReference<List<String>>() {}
        );

        // Resolve the input from resources on the runtime classpath
        InputStream raw = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(TMDATA_INPUT_PATH);

        if (raw == null) {
            throw new FileNotFoundException(
                "Resource not found on classpath: " + TMDATA_INPUT_PATH +
                " (put the file under src/main/resources, and use the classpath name only)"
            );
        }

        Path outPath = Paths.get(TMDATA_OUTPUT_PATH).toAbsolutePath().normalize();
        Files.createDirectories(outPath.getParent());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(raw, StandardCharsets.UTF_8));
             BufferedWriter bw = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            boolean inBlock = false;

            String line;
            while ((line = br.readLine()) != null) {

                boolean startsDepartmentBlock = DEPTSTORE_BLOCK_START.matcher(line).matches(); // department store matcher
                boolean startsCityBlock = CHERRYGROVE_BLOCK_START.matcher(line).matches() || // any city matcher
                                          VIOLET_BLOCK_START.matcher(line).matches() || 
                                          AZALEA_BLOCK_START.matcher(line).matches() || 
                                          ECRUTEAK_BLOCK_START.matcher(line).matches() || 
                                          OLIVINE_BLOCK_START.matcher(line).matches() || 
                                          SAFFRON_BLOCK_START.matcher(line).matches() || 
                                          LAVENDER_BLOCK_START.matcher(line).matches() || 
                                          CERULEAN_BLOCK_START.matcher(line).matches();
                boolean endsBlock = MART_BLOCK_END.matcher(line).matches(); // end of block matcher
                if (startsDepartmentBlock) {
                    bw.append(line).append(System.lineSeparator());
                    bw.append(Move.buildDepartmentTMList(tmList, TM_AMOUNT));
                    inBlock = true;
                }
                if (startsCityBlock) {
                    bw.append(line).append(System.lineSeparator());
                    bw.append(Move.buildCityTMList(tmList, itemList));
                    inBlock = true;
                }
                if (endsBlock && inBlock) {
                    inBlock = false;
                }
                
                if(!inBlock) {
                    bw.append(line).append(System.lineSeparator());
                }
                
            }
        }

    }

}