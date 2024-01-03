import java.util.*;


public class Game {
    private boolean Testing=true;
    private Scanner scanner = new Scanner(System.in);
    Game(Scanner scanner){
        //this.Testing = Testing;
        this.scanner = scanner;
    }

    public void askForInput(int guessLeft){
        System.out.println("You have "+ guessLeft +" guesses left.");
        System.out.println("What is your next guess?");
        System.out.println("Type in the characters for your guess and press enter.");
        System.out.print("Enter guess: ");
    }
    public void runGame(){
        String secretCode;
        int guessingTime = GameConfiguration.guessNumber;
        boolean endTheGame = false;
        String userResponse;

        System.out.print("Welcome to Mastermind.  Here are the rules.\n\n" +
                "This is a text version of the classic board game Mastermind.\n\n" +
                "The computer will think of a secret code. The code consists of 4 colored pegs. The pegs MUST be one of six colors: blue, green, orange, purple, red, or yellow. A color may appear more than once in the code. You try to guess what colored pegs are in the code and what order they are in. After you make a valid guess the result (feedback) will be displayed.\n\n" +
                "The result consists of a black peg for each peg you have guessed exactly correct (color and position) in your guess. For each peg in the guess that is the correct color, but is out of position, you get a white peg. For each peg, which is fully incorrect, you get no feedback.\n\n" +
                "Only the first letter of the color is displayed. B for Blue, R for Red, and so forth. When entering guesses you only need to enter the first character of each color as a capital letter.\n\n" +
                "You have 12 guesses to figure out the secret code or you lose the game. Are you ready to play? (Y/N): ");
        userResponse = scanner.nextLine();
        if(!userResponse.equals("Y"));/** TO DO: add line**/
        System.out.print("\nGenerating secret code ...");

        while(!endTheGame){
            secretCode = SecretCodeGenerator.getInstance().getNewSecretCode();
            ResponseGenerator computerResponse = new ResponseGenerator();



            if(Testing)System.out.println("...(for this example the secret code is "+secretCode+")\n");
            else System.out.print("\n");

            while(guessingTime > 0){
                askForInput(guessingTime);
                userResponse = scanner.nextLine();
                System.out.println();


                if(userResponse.equals("HISTORY")){
                    System.out.println(computerResponse.getHistory());
                }
                else if(computerResponse.isValidGuess(userResponse)) {//valid guess
                    if(guessingTime == 1 && !userResponse.equals(secretCode)){
                        System.out.println("Sorry, you are out of guesses. You lose, boo-hoo.\n");
                        /**TO DO: add LINE**/
                        //break;
                    }
                    else if(userResponse.equals(secretCode)){
                        break;
                    }else{
                        System.out.println(computerResponse.guessResponse(userResponse, secretCode));
                    }
                    guessingTime--;
                }
                else System.out.println("\n"+userResponse+" -> INVALID GUESS\n");
            }

            if(userResponse.equals(secretCode)) {
                System.out.println(userResponse+" -> Result: 4B_0W - You win !!\n");
                /**TO DO: add LINE**/
            }

//            System.out.print("Are you ready for another game (Y/N): ");
//            userResponse = scanner.nextLine();
//            if(userResponse.equals("N")) endTheGame = true;
//            guessingTime = GameConfiguration.guessNumber;
        }

        /**TO DO: add LINE**/

    }
}
