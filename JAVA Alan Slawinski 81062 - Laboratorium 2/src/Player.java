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
    private Map<String, Integer> botAttempts;
    private Map<String, Integer> mixedPlayerScores;
    private Map<String, Integer> mixedBotScores;
    private String difficulty;

    public Player(String username) {
        this.username = username;
        this.bestScores = new HashMap<>();
        this.botAttempts = new HashMap<>();
        this.mixedPlayerScores = new HashMap<>();
        this.mixedBotScores = new HashMap<>();
        this.difficulty = "Easy";

        // Initialize scores for all difficulties
        for (String level : new String[]{"Easy", "Normal", "Hard"}) {
            this.bestScores.put(level, 0);
            this.botAttempts.put(level, 0);
            this.mixedPlayerScores.put(level, 0);
            this.mixedBotScores.put(level, 0);
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

    public int getBotAttemptsForDifficulty(String difficulty) {
        return botAttempts.getOrDefault(difficulty, 0);
    }

    public void setBotAttemptsForDifficulty(String difficulty, int attempts) {
        botAttempts.put(difficulty, attempts);
    }

    public int getMixedPlayerScoreForDifficulty(String difficulty) {
        return mixedPlayerScores.getOrDefault(difficulty, 0);
    }

    public void setMixedPlayerScoreForDifficulty(String difficulty, int score) {
        mixedPlayerScores.put(difficulty, score);
    }

    public int getMixedBotScoreForDifficulty(String difficulty) {
        return mixedBotScores.getOrDefault(difficulty, 0);
    }

    public void setMixedBotScoreForDifficulty(String difficulty, int score) {
        mixedBotScores.put(difficulty, score);
    }

    public static Player loadFromFile(String username) {
        Path path = Paths.get("./" + username + ".txt");
        File file = new File(path.toString());

        Player player = new Player(username);

        if (!file.exists()) {
            System.out.println("No existing data for " + username + ". Starting a new game.");
            return player;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.startsWith("Best Score")) {
                    String level = key.replace("Best Score (", "").replace(")", "").trim();
                    player.bestScores.put(level, Integer.parseInt(value));
                } else if (key.startsWith("Attempts")) {
                    String level = key.replace("Attempts (", "").replace(")", "").trim();
                    player.botAttempts.put(level, Integer.parseInt(value));
                } else if (key.startsWith("Mixed Player Score")) {
                    String level = key.replace("Mixed Player Score (", "").replace(")", "").trim();
                    player.mixedPlayerScores.put(level, Integer.parseInt(value));
                } else if (key.startsWith("Mixed Bot Score")) {
                    String level = key.replace("Mixed Bot Score (", "").replace(")", "").trim();
                    player.mixedBotScores.put(level, Integer.parseInt(value));
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
                for (String level : bestScores.keySet()) {
                    writer.println("Best Score (" + level + "): " + bestScores.get(level));
                    writer.println("Attempts (" + level + "): " + botAttempts.get(level));
                    writer.println("Mixed Player Score (" + level + "): " + mixedPlayerScores.get(level));
                    writer.println("Mixed Bot Score (" + level + "): " + mixedBotScores.get(level));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving player data: " + e.getMessage());
        }
    }
}
