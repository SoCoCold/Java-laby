import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // Get username
        System.out.println("Provide a username:");
        String username = scanner.nextLine();

        // Load player or create new
        Player player = Player.loadFromFile(username);

        System.out.println("Hello " + player.getUsername() + "!");

        // Select difficulty level
        selectDifficulty(scanner, player);

        int choice = displayMenu(scanner);
        Main mainInstance = new Main();

        switch (choice) {
            case 1:
                mainInstance.playPlayerGame(player, scanner, random);
                break;
            case 2:
                mainInstance.playBotGame(scanner, player);
                break;
            case 3:
                mainInstance.playMixedGame(scanner, player, random);
                break;
            default:
                System.out.println("Invalid choice. Exiting game...");
                mainInstance.endGame(player);
        }
    }

    public static void selectDifficulty(Scanner scanner, Player player) {
        System.out.println("Select difficulty level:");
        System.out.println("1 - Easy (0-100)");
        System.out.println("2 - Normal (0-10,000)");
        System.out.println("3 - Hard (0-1,000,000)");

        while (true) {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                switch (choice) {
                    case 1:
                        player.setDifficulty("Easy");
                        return;
                    case 2:
                        player.setDifficulty("Normal");
                        return;
                    case 3:
                        player.setDifficulty("Hard");
                        return;
                    default:
                        System.out.println("Invalid choice. Please select 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    public static int displayMenu(Scanner scanner) {
        while (true) {
            System.out.println("Choose the game mode you want to play (type number to select): ");
            System.out.println("1 - Player vs Bot");
            System.out.println("2 - Bot vs Player");
            System.out.println("3 - Mixed mode");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                if (choice >= 1 && choice <= 3) {
                    return choice; // Valid choice, return it
                } else {
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    public void playPlayerGame(Player player, Scanner scanner, Random random) {
        int score = 0;
        boolean playing = true;

        int maxRange = getMaxRange(player.getDifficulty());

        System.out.println("Your current best score for " + player.getDifficulty() + " difficulty is: " + player.getBestScoreForDifficulty(player.getDifficulty()));

        while (playing) {
            int randomNumber = random.nextInt(maxRange + 1);
            int maxRound = 10; // Rounds before "Game Over"
            int currentRound = 0;

            while (currentRound < maxRound) {
                currentRound++;
                System.out.println("Round " + currentRound + "/" + maxRound);
                System.out.println("Guess a number between 0 and " + maxRange + ":");
                if (scanner.hasNextInt()) {
                    int guess = scanner.nextInt();
                    scanner.nextLine();

                    if (guess == randomNumber) {
                        System.out.println("Congratulations! You guessed the correct number!");
                        score += 10;
                        System.out.println("Your current score is: " + score);

                        if (score > player.getBestScoreForDifficulty(player.getDifficulty())) {
                            player.setBestScoreForDifficulty(player.getDifficulty(), score);
                            System.out.println("Your new high score for " + player.getDifficulty() + " is: " + player.getBestScoreForDifficulty(player.getDifficulty()));
                        }
                        break;
                    } else if (guess < randomNumber) {
                        System.out.println("Incorrect guess - TOO LOW");
                    } else {
                        System.out.println("Incorrect guess - TOO HIGH");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                }
            }

            if (currentRound == maxRound) {
                System.out.println("Game Over! The correct number was: " + randomNumber);
                System.out.println("Your final score is: " + score);
                boolean validInput = false;

                while (!validInput) {
                    System.out.println("Want to try again?\n1 - Restart (Score will reset to 0)\n2 - Save and Quit");
                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        if (choice == 1) {
                            score = 0;
                            currentRound = 0;
                            validInput = true;
                        } else if (choice == 2) {
                            endGame(player);
                        } else {
                            System.out.println("Invalid choice. Please enter 1 or 2.");
                        }
                    } else {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
            } else {
                boolean validInput = false;

                while (!validInput) {
                    System.out.println("Play again or Save and Quit?\n1 - Play Again\n2 - Save and Quit");
                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        if (choice == 1) {
                            validInput = true;
                        } else if (choice == 2) {
                            endGame(player);
                        } else {
                            System.out.println("Invalid choice. Please enter 1 or 2.");
                        }
                    } else {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
            }
        }
    }

    public void playBotGame(Scanner scanner, Player player) {
        int maxRange = getMaxRange(player.getDifficulty());

        System.out.println("Think of a number between 0 and " + maxRange + ". I will try to guess it.");
        int low = 0;
        int high = maxRange;
        boolean guessed = false;
        int attempts = 0;

        while (!guessed && low <= high) {
            int guess = (low + high) / 2;
            attempts++;
            System.out.println("Is your number: " + guess + "?");
            System.out.println("1 - Too Low, 2 - Too High, 3 - Correct");

            if (scanner.hasNextInt()) {
                int response = scanner.nextInt();
                scanner.nextLine();

                switch (response) {
                    case 1:
                        low = guess + 1;
                        break;
                    case 2:
                        high = guess - 1;
                        break;
                    case 3:
                        guessed = true;
                        System.out.println("I guessed your number in " + attempts + " attempts!");
                        if (player.getBotAttemptsForDifficulty(player.getDifficulty()) == 0 || attempts < player.getBotAttemptsForDifficulty(player.getDifficulty())) {
                            player.setBotAttemptsForDifficulty(player.getDifficulty(), attempts);
                            System.out.println("New record for " + player.getDifficulty() + ": " + attempts + " attempts!");
                        }
                        break;
                    default:
                        System.out.println("Invalid input. Please enter 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }

        boolean validInput = false;

        while (!validInput) {
            System.out.println("Play again or Save and Quit?\n1 - Play Again\n2 - Save and Quit");
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    validInput = true;
                } else if (choice == 2) {
                    endGame(player);
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    public void playMixedGame(Scanner scanner, Player player, Random random) {
        int maxRange = getMaxRange(player.getDifficulty());
        boolean isPlayerTurn = tossCoin(random);
        int randomNumber = random.nextInt(maxRange + 1);
        boolean guessed = false;
        int low = 0;
        int high = maxRange;

        System.out.println(isPlayerTurn ? "Player starts!" : "Bot starts!");

        while (!guessed) {
            if (isPlayerTurn) {
                System.out.println("Your turn to guess! Enter a number:");
                if (scanner.hasNextInt()) {
                    int guess = scanner.nextInt();
                    scanner.nextLine();

                    if (guess == randomNumber) {
                        guessed = true;
                        System.out.println("You guessed the correct number! You win!");
                        player.setMixedPlayerScoreForDifficulty(player.getDifficulty(), player.getMixedPlayerScoreForDifficulty(player.getDifficulty()) + 1);
                    } else if (guess < randomNumber) {
                        System.out.println("TOO LOW");
                    } else {
                        System.out.println("TOO HIGH");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                }
            } else {
                int botGuess = (low + high) / 2;
                System.out.println("Bot guesses: " + botGuess);

                if (botGuess == randomNumber) {
                    guessed = true;
                    System.out.println("Bot guessed the correct number! Bot wins!");
                    player.setMixedBotScoreForDifficulty(player.getDifficulty(), player.getMixedBotScoreForDifficulty(player.getDifficulty()) + 1);
                } else if (botGuess < randomNumber) {
                    System.out.println("Bot's guess was TOO LOW");
                    low = botGuess + 1;
                } else {
                    System.out.println("Bot's guess was TOO HIGH");
                    high = botGuess - 1;
                }
            }

            isPlayerTurn = !isPlayerTurn;
        }

        boolean validInput = false;

        while (!validInput) {
            System.out.println("Play again or Save and Quit?\n1 - Play Again\n2 - Save and Quit");
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    validInput = true;
                } else if (choice == 2) {
                    endGame(player);
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private int getMaxRange(String difficulty) {
        switch (difficulty) {
            case "Normal":
                return 10000;
            case "Hard":
                return 1000000;
            default:
                return 100;
        }
    }

    private boolean tossCoin(Random random) {
        return random.nextBoolean();
    }

    public void endGame(Player player) {
        player.saveToFile();
        System.out.println("Your progress has been saved!");
        System.exit(0);
    }
}
