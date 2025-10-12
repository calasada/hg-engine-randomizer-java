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
import java.util.List;
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

    public static final List<Move> STATIC_MOVES = initMoves();
    public static final List<Area> STATIC_AREAS = initAreas();
    public static final List<Pokemon> STATIC_MONS = initPokemon();

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
        JCheckBox clampEvolution = new JCheckBox("Clamp Evolution Levels (25, 40)");

        // JPanel of checkboxes
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.add(randomStarters);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(randomEncounters);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(randomTrainers);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(clampEvolution);

        frame.add(checkBoxPanel, BorderLayout.CENTER);

        JButton goButton = new JButton("Go");
        goButton.addActionListener(e -> {

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


            if (randomTrainers.isSelected()) {
                System.out.println("Randomizing Trainers...");

            } else {
                try {
                    Files.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(TRAINERS_INPUT_PATH), Paths.get(TRAINERS_OUTPUT_PATH), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    System.out.println("it fked up");
                }
            }


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

}