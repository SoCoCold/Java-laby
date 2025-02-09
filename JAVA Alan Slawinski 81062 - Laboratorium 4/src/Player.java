import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Player {
    private String username;
    private Map<String, Integer> bestScores;
    private Map<String, Integer> mixedPlayerScores;
    private String difficulty;
    private Map<String, Integer> multiplayerScores = new HashMap<>();



    public Player(String username) {
        this.username = username;
        this.bestScores = new HashMap<>();

        this.mixedPlayerScores = new HashMap<>();

        this.difficulty = "Easy";

        // Initialize scores for all difficulties
        for (String level : new String[]{"Easy", "Normal", "Hard", "Custom"}) {
            this.bestScores.put(level, 0);
            this.mixedPlayerScores.put(level, 0);
            this.multiplayerScores.put(level, 0);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getBestScoreForDifficulty(String difficulty) {
        return bestScores.getOrDefault(difficulty, 0);
    }

    public void setBestScoreForDifficulty(String difficulty, int score) {
        bestScores.put(difficulty, score);
    }



    public int getMixedPlayerScoreForDifficulty(String difficulty) {
        return mixedPlayerScores.getOrDefault(difficulty, 0);
    }

    public void setMixedPlayerScoreForDifficulty(String difficulty, int score) {
        mixedPlayerScores.put(difficulty, score);
    }

    // Getter for multiplayer scores
    public int getMultiplayerScoreForDifficulty(String difficulty) {
        return multiplayerScores.getOrDefault(difficulty, 0);
    }

    // Setter for multiplayer scores
    public void setMultiplayerScoreForDifficulty(String difficulty, int score) {
        multiplayerScores.put(difficulty, score);
    }



    public static Player loadFromFile(String username) {
        Path path = Paths.get("./" + username + ".txt");
        Player player = new Player(username);

        if (!Files.exists(path)) {
            System.out.println("No existing data for " + username + ". Starting fresh.");
            return player;
        }

        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("Best Score (")) {
                    String level = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    int score = Integer.parseInt(line.split(": ")[1]);
                    player.bestScores.put(level, score);
                } else if (line.startsWith("Mixed Player Score (")) {
                    String level = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    int score = Integer.parseInt(line.split(": ")[1]);
                    player.mixedPlayerScores.put(level, score);
                } else if (line.startsWith("Multiplayer (")) {
                    String level = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    int score = Integer.parseInt(line.split(": ")[1]);
                    player.multiplayerScores.put(level, score);
                } else if (line.startsWith("Champion: ")) {
                    player.setChampion(Boolean.parseBoolean(line.split(": ")[1]));
                }

            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading player data: " + e.getMessage());
        }

        return player;
    }

    public void saveToFile() {
        Path path = Paths.get("./" + username + ".txt");
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
                // Iterate through all difficulty levels and save data
                for (String level : bestScores.keySet()) {
                    writer.println("Best Score (" + level + "): " + bestScores.get(level));
                    writer.println("Mixed Player Score (" + level + "): " + mixedPlayerScores.get(level));
                    writer.println("Multiplayer (" + level + "): " + multiplayerScores.get(level));
                }
                writer.println("Champion: " + isChampion);
            }
            System.out.println("Player data saved to " + path.toString());
        } catch (IOException e) {
            System.out.println("Error saving player data: " + e.getMessage());
        }
    }

    private int customMinRange;
    private int customMaxRange;

    // Set the custom range
    public void setCustomRange(int min, int max) {
        this.customMinRange = min;
        this.customMaxRange = max;
    }
    // Get the maximum value of the custom range
    public int getCustomMaxRange() {
        return customMaxRange;
    }
    // Get the minimum value of the custom range
    public int getCustomMinRange() {
        return customMinRange;
    }

    private boolean isLeader = false;

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        this.isLeader = leader;
    }

    private boolean isChampion = false;

    public boolean isChampion() {
        return isChampion;
    }

    public void setChampion(boolean champion) {
        this.isChampion = champion;
    }
}
