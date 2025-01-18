import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // Get the number of players
        int numberOfPlayers = 0;
        while (true) {
            System.out.println("Enter the number of players:");
            if (scanner.hasNextInt()) {
                numberOfPlayers = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer
                if (numberOfPlayers > 0) {
                    break; // Valid input, exit the loop
                } else {
                    System.out.println("The number of players must be greater than 0. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a positive integer.");
                scanner.nextLine(); // Clear the invalid input
            }
        }

        System.out.println("Number of players: " + numberOfPlayers);
        // Create a list of players
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.println("Enter username for Player " + (i + 1) + ":");
            String username = scanner.nextLine();
            players.add(Player.loadFromFile(username));
        }

        //creates new bot
        Bot bot = new Bot("GameBot");


        // Select difficulty level (shared for all players)
        String difficulty = selectDifficulty(scanner, players, bot);
        for (Player player : players) {
            player.setDifficulty(difficulty);
        }
        bot.setDifficulty(difficulty);

        System.out.println("All players will play on " + difficulty + " difficulty!");

        // Main game loop
        Main mainInstance = new Main();

        while (true) { // Keep the game running until the user decides to quit
            int choice = mainInstance.displayMenu(scanner, players, bot);

            switch (choice) {
                case 1: // Player vs Bot
                    for (Player player : players) {
                        mainInstance.playPlayerGame(player, players, bot, scanner, random);
                    }
                    break;
                case 2: // Bot vs Players
                    for (Player player : players) {
                        mainInstance.playBotGame(scanner, bot, player, players);
                    }
                    break;
                case 3: // Mixed mode
                    for (Player player : players) {
                        mainInstance.playMixedGame(scanner, player, bot, players, random);
                    }
                    break;
                case 4: // Multiplayer mode
                    for (Player player : players) {
                        mainInstance.playMultiplayerMode(scanner, players, new Random());
                    }
                    break;
                case 5: // Quit and Save
                    mainInstance.endGame(players, bot);
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }

    }

    public static String selectDifficulty(Scanner scanner, List<Player> players, Bot bot) {
        System.out.println("Select difficulty level for the game:");
        System.out.println("1 - Easy (0-100)");
        System.out.println("2 - Normal (0-10,000)");
        System.out.println("3 - Hard (0-1,000,000)");
        System.out.println("4 - Custom (set your own range)");

        while (true) {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                switch (choice) {
                    case 1:
                        return "Easy";
                    case 2:
                        return "Normal";
                    case 3:
                        return "Hard";
                    case 4:
                        setCustomDifficulty(scanner, players, bot);
                        return "Custom";
                    default:
                        System.out.println("Invalid choice. Please select 1, 2, or 3.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    public static int displayMenu(Scanner scanner, List<Player> players, Bot bot) {
        while (true) {
            System.out.println("Choose the game mode you want to play (type number to select): ");
            System.out.println("1 - Player vs Bot (ONLY SOLO)");
            System.out.println("2 - Bot vs Player (ONLY SOLO)");
            System.out.println("3 - Mixed mode (ONLY SOLO)");
            System.out.println("4 - Multiplayer mode");
            System.out.println("5 - Quit and Save");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                if (choice >= 1 && choice <= 5) {
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

    public void playPlayerGame(Player player, List<Player> players, Bot bot, Scanner scanner, Random random) {
        int score = 0;
        boolean playing = true;

        int maxRange = getMaxRange(player.getDifficulty(), player);

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
                            endGame(players, bot);
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
                            endGame(players, bot);
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

    public void playBotGame(Scanner scanner, Bot bot, Player player, List<Player> players) {
        int maxRange = getMaxRange(bot.getDifficulty(), bot);

        System.out.println("Think of a number between 0 and " + maxRange + ". I will try to guess it.");
        int low = 0;
        int high = maxRange;
        boolean guessed = false;
        int attempts = 0;

        while (!guessed && low <= high) {
            int guess = bot.makeGuess(low, high);
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
                        if (bot.getBotAttemptsForDifficulty(player.getDifficulty()) == 0 || attempts < bot.getBotAttemptsForDifficulty(player.getDifficulty())) {
                            bot.setBotAttemptsForDifficulty(player.getDifficulty(), attempts);
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
                    endGame(players, bot);
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    public void playMixedGame(Scanner scanner, Player player, Bot bot, List<Player> players, Random random) {
        int maxRange = getMaxRange(player.getDifficulty(), player);
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
                    bot.setMixedBotScoreForDifficulty(player.getDifficulty(), bot.getMixedBotScoreForDifficulty(player.getDifficulty()) + 1);
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
                    playMixedGame(scanner, player, bot, players, random);
                } else if (choice == 2) {
                    endGame(players, bot);
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private int getMaxRange(String difficulty, Player player) {
        switch (difficulty) {
            case "Normal":
                return 10000;
            case "Hard":
                return 1000000;
            case "Custom":
                return player.getCustomMaxRange();
            default:
                return 100; // Easy
        }
    }

    private boolean tossCoin(Random random) {
        return random.nextBoolean();
    }

    public void playMultiplayerMode(Scanner scanner, List<Player> players, Random random) {
        int maxRange = getMaxRange(players.get(0).getDifficulty(), players.get(0));
        int randomNumber = random.nextInt(maxRange + 1);
        boolean guessed = false;
        Player winner = null; // Initialize winner

        System.out.println("I have chosen a number between 0 and " + maxRange + ".");
        System.out.println("Players will take turns guessing the number!");

        while (!guessed) {
            for (Player player : players) {
                System.out.println(player.getUsername() + "'s turn to guess!");
                System.out.println("Enter your guess:");

                if (scanner.hasNextInt()) {
                    int guess = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer

                    if (guess == randomNumber) {
                        guessed = true;
                        winner = player; // Assign the winner
                        System.out.println("Correct! " + player.getUsername() + " guessed the number!");

                        // Update multiplayer score
                        player.setMultiplayerScoreForDifficulty(
                                player.getDifficulty(),
                                player.getMultiplayerScoreForDifficulty(player.getDifficulty()) + 1
                        );

                        break;

                    } else if (guess < randomNumber) {
                        System.out.println("TOO LOW!");
                    } else {
                        System.out.println("TOO HIGH!");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear invalid input
                }
            }
        }

        System.out.println("The number was: " + randomNumber);

        for (Player player : players) {
            player.saveToFile();
        }

        boolean validInput = false;

        while (!validInput) {
            System.out.println("Play again or Save and Quit?\n1 - Play Again\n2 - Save and Quit");
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                if (choice == 1) {
                    validInput = true;

                    // Let the winner choose the difficulty for the next game
                    if (winner != null) {
                        System.out.println(winner.getUsername() + ", you won! Please select the difficulty for the next game:");
                        String newDifficulty = selectDifficulty(scanner, players, null); // Winner selects difficulty
                        for (Player player : players) {
                            player.setDifficulty(newDifficulty); // Apply difficulty to all players
                        }
                        System.out.println("The new difficulty is set to " + newDifficulty + "!");
                    }

                    // Start the next multiplayer game
                    playMultiplayerMode(scanner, players, random);
                } else if (choice == 2) {
                    endGame(players); // Save and exit
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }


    public static void setCustomDifficulty(Scanner scanner, List<Player> players, Bot bot) {
        int minRange = 0;
        int maxRange = 0;

        // Get the minimum value for the range
        while (true) {
            System.out.println("Enter the minimum value of the range:");
            if (scanner.hasNextInt()) {
                minRange = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
                break;
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        // Get the maximum value for the range
        while (true) {
            System.out.println("Enter the maximum value of the range (must be greater than " + minRange + "):");
            if (scanner.hasNextInt()) {
                maxRange = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
                if (maxRange > minRange) {
                    break;
                } else {
                    System.out.println("Maximum value must be greater than the minimum value. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        // Set the custom range for all players and the bot
        for (Player player : players) {
            player.setDifficulty("Custom");
            player.setCustomRange(minRange, maxRange);
        }

        // Apply custom range to the bot only if bot is not null
        if (bot != null) {
            bot.setDifficulty("Custom");
            bot.setCustomRange(minRange, maxRange);
        }

        System.out.println("Custom range set: " + minRange + " to " + maxRange);
    }


    public void endGame(List<Player> players, Bot bot) {
        for (Player player : players) {
            player.saveToFile();
        }
        bot.saveToFile();
        System.out.println("All progress has been saved!");
        System.exit(0);
    }

    public void endGame(List<Player> players) {
        for (Player player : players) {
            player.saveToFile();
        }
        System.out.println("Your progress has been saved!");
        System.exit(0);
    }



}
