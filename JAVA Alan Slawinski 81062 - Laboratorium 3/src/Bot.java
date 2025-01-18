import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Bot extends Player {
    private Map<String, Integer> botAttempts;
    private Map<String, Integer> mixedBotScores;

    public Bot(String username) {
        super(username);
        this.botAttempts = new HashMap<>();
        this.mixedBotScores = new HashMap<>();

        // Initialize scores for all difficulties
        for (String level : new String[]{"Easy", "Normal", "Hard"}) {
            this.botAttempts.put(level, 0);
            this.mixedBotScores.put(level, 0);
        }
    }

    public int getBotAttemptsForDifficulty(String difficulty) {
        return botAttempts.getOrDefault(difficulty, 0);
    }

    public void setBotAttemptsForDifficulty(String difficulty, int attempts) {
        botAttempts.put(difficulty, attempts);
    }

    public int getMixedBotScoreForDifficulty(String difficulty) {
        return mixedBotScores.getOrDefault(difficulty, 0);
    }

    public void setMixedBotScoreForDifficulty(String difficulty, int score) {
        mixedBotScores.put(difficulty, score);
    }

    public int makeGuess(int low, int high) {
        return (low + high) / 2;
    }

@Override
    public void saveToFile() {

        super.saveToFile();

        Path path = Paths.get("./" + getUsername() + "_bot.txt");
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
                for (String level : botAttempts.keySet()) {
                    writer.println("Attempts (" + level + "): " + botAttempts.get(level));
                    writer.println("Mixed Bot Score (" + level + "): " + mixedBotScores.get(level));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving player data: " + e.getMessage());
        }
    }

    public static Bot loadFromFile(String username) {
        Player player = Player.loadFromFile(username);

        Bot bot = new Bot(player.getUsername());
        bot.setDifficulty(player.getDifficulty());

        Path path = Paths.get("./" + username + "_bot.txt");
        File file = new File(path.toString());

        if (!file.exists()) {
            System.out.println("No existing bot data for " + username + ". Starting a new bot game.");
            return bot;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.startsWith("Attempts")) {
                    String level = key.replace("Attempts (", "").replace(")", "").trim();
                    bot.botAttempts.put(level, Integer.parseInt(value));
                } else if (key.startsWith("Mixed Bot Score")) {
                    String level = key.replace("Mixed Bot Score (", "").replace(")", "").trim();
                    bot.mixedBotScores.put(level, Integer.parseInt(value));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading bot data: " + e.getMessage());
        }

        return bot;
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

}
