package app;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class RandomizerUI {
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
        frame.setSize(300, 250);

        //Title
        JLabel titleLabel = new JLabel("Choose options:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH); //Added to top middle

        // Option checkboxes
        JCheckBox randomEncounters = new JCheckBox("Random Encounters");
        JCheckBox randomTrainers = new JCheckBox("Random Trainers");

        // JPanel of checkboxes
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.add(randomEncounters);
        checkBoxPanel.add(Box.createVerticalStrut(-2)); // small gap
        checkBoxPanel.add(randomTrainers);

        frame.add(checkBoxPanel, BorderLayout.CENTER);

        JButton goButton = new JButton("Go");
        goButton.addActionListener(e -> {

            System.out.println("Hello, World!");
        
                if (randomEncounters.isSelected()) {
                    System.out.println("Randomizing Encounters...");

                    try {
                        // Load from classpath: src/main/resources/data/pokemondata.json
                        List<Pokemon> mons = JsonLoader.loadResolvedPokemon("/pokemondata.json");
                        System.out.println("Loaded " + mons.size() + " Pokémon");
                        if (!mons.isEmpty()) {
                            for (Pokemon mon : mons) {
                                if(mon.id == 34) {
                                    System.out.println(mon.toString());
                                }
                            }
                        }
                        System.out.println("Hello, World!");
                        frame.dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Failed to load data: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
            }

            if (randomTrainers.isSelected()) {
                System.out.println("Randomizing Trainers...");
            }

            frame.dispose();

        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(goButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}