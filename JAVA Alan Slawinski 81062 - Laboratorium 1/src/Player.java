
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Player {
    private String Username;
    private int bestScore;

    public Player(String Username, int bestScore) {
        this.Username = Username;
        this.bestScore = bestScore;

    }

    public String getUsername() {
        return Username;

    }

    public int getbestScore() {

        return bestScore;

    }

    public void setbestScore(int bestScore) {
        this.bestScore = bestScore;

    }


    // Load player data from a file
    public static Player loadFromFile(String playerName){
        Path pathname = Paths.get("./" + playerName + ".txt");
        File file = new File(pathname.toString());

        // File does not exist
        if (!file.exists()) {
            System.out.println("No existing data for " + playerName + ". Starting a new game.");
            return new Player(playerName, 0); // Default bestScore
        }

        // File exists, try reading the best bestScore
        try (Scanner filereader = new Scanner(file)) {
            int bestScore = filereader.nextInt(); // Assume the file contains only the best bestScore
            return new Player(playerName, bestScore);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading player data: " + e.getMessage());
            return new Player(playerName, 0); // If error, start fresh
        }
    }

    // Save player data to a file
    public void saveToFile() {
        Path pathname = Paths.get("./" + Username + ".txt");
        File file = new File(pathname.toString());

        if (!file.exists()) {
            try {
                Files.createFile(pathname);
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
                return;
            }
        }

        try (PrintWriter filewriter = new PrintWriter(file)) {
            filewriter.println(bestScore); // Write the best bestScore
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

}




