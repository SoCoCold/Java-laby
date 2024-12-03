import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);


        // Get username
        System.out.println("Provide a username:");
        String Username = scanner.nextLine();

        //Load player or create new
        Player player = Player.loadFromFile(Username);


        System.out.println("Hello " + player.getUsername() + "!");
        System.out.println("Your current best score is: " + player.getbestScore());


        //Gameplay loop
        Random random = new Random();
        int score = 0;
        int RandomNumber = random.nextInt(101);
        boolean playing = true;



        while (playing) {

            System.out.println("Guess a number between 0-100: ");
            int guess = scanner.nextInt();
            scanner.nextLine();


            if (guess == RandomNumber) {


                System.out.println("Congratulations you guessed the correct number!");
                System.out.println("You earn 10 points!");
                score += 10;
                System.out.println("Your current score is: " + score);

                if (score > player.getbestScore()){
                    player.setbestScore(score);
                    System.out.println("Your new high score is: " + player.getbestScore());
                }



            }

            else {


                System.out.println("Incorrect guess, the number was: " + RandomNumber);
                System.out.println("Game Over! Your final score is: " + score);

                boolean validInput = false;

                while (!validInput) {

                    System.out.println("Want to try again? \n Type: \n 1 - YES (Score will be set to 0!) \n 2 - NO");

                    if (scanner.hasNextInt()) {
                        int YESNO = scanner.nextInt();
                        scanner.nextLine();


                        if (YESNO == 1) {

                            score = 0;
                            playing = true;
                            System.out.println();
                            validInput = true;



                        } else if (YESNO == 2) {

                            validInput = true;
                            playing = false;

                        } else {


                            System.out.println("Only \"1\" or \"2\" are allowed");


                        }

                    }
                    else{

                        System.out.println("Only \"1\" or \"2\" are allowed");
                        scanner.nextLine();
                        System.out.println();
                    }
                }



            }
        }

        //Save player data to file
        player.saveToFile();
        System.out.println("Your best score is saved as: " + player.getbestScore());


    }

}