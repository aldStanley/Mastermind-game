import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private volatile boolean SomeoneWon = false;
    String secretCode = SecretCodeGenerator.getInstance().getNewSecretCode();
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server(){}
    private static void broadcastToAll(ClientHandler winner, String message){
        for(ClientHandler client : clients){
            try {
                DataOutputStream dout = new DataOutputStream(client.clientSocket.getOutputStream());
                if(client != winner)dout.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void startServer(){
        try(ServerSocket server = new ServerSocket(6666)){
            System.out.println("Server started. Waiting for clients...");

            while(true){
                Socket client = server.accept();
                System.out.println("Client connected: ");
                ClientHandler clientThread = new ClientHandler(client, secretCode);
                clients.add(clientThread);
                new Thread(clientThread).start();
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    public class ClientHandler extends Thread {
        private Socket clientSocket;
        private String secretCode;
        private int guessLeft = GameConfiguration.guessNumber;
        private volatile boolean shouldStop = false;
        public ClientHandler(Socket clientSocket, String secretCode) {
            this.clientSocket = clientSocket;
            this.secretCode = secretCode;
        }

        @Override
        public void run() {
            try {
                // Implement the code to handle communication with the client
                // read guesses from the client, update the game state, and send responses back.
                DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream din = new DataInputStream(clientSocket.getInputStream());

                String guess, userResponse;
                boolean endTheGame = false;

                dout.writeUTF("Welcome to Mastermind.  Here are the rules.\n\n" +
                        "This is a text version of the classic board game Mastermind.\n\n" +
                        "The computer will think of a secret code. The code consists of 4 colored pegs. The pegs MUST be one of six colors: blue, green, orange, purple, red, or yellow. A color may appear more than once in the code. You try to guess what colored pegs are in the code and what order they are in. After you make a valid guess the result (feedback) will be displayed.\n\n" +
                        "The result consists of a black peg for each peg you have guessed exactly correct (color and position) in your guess. For each peg in the guess that is the correct color, but is out of position, you get a white peg. For each peg, which is fully incorrect, you get no feedback.\n\n" +
                        "Only the first letter of the color is displayed. B for Blue, R for Red, and so forth. When entering guesses you only need to enter the first character of each color as a capital letter.\n\n" +
                        "You have "+guessLeft +" guesses to figure out the secret code or you lose the game. Are you ready to play? (Y/N): \n");
                userResponse = din.readUTF();
                if(!userResponse.equals("Y")){
                    /** TO DO: add line**/

                    try {
                        clientSocket.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                System.out.println("\nGenerating secret code ...");

                ResponseGenerator gameResponse = new ResponseGenerator();
                while(guessLeft > 0 && !shouldStop){
                    dout.writeUTF("Secret code is: "+secretCode+"You have "+ guessLeft +" guesses left.\nWhat is your next guess?\nType in the characters for your guess and press enter.\nEnter guess: ");
                    userResponse = din.readUTF();
                    System.out.println("Client input!");

                    if(userResponse.equals("HISTORY")){
                        dout.writeUTF(gameResponse.getHistory()+'\n');
                        dout.flush();
                    }
                    else if(gameResponse.isValidGuess(userResponse)) {//valid guess
                        if(guessLeft == 1 && !userResponse.equals(secretCode)){
                            dout.writeUTF("Sorry, you are out of guesses. You lose, boo-hoo.\n");
                            dout.flush();
                            break;
                        }
                        else if(userResponse.equals(secretCode)){
                            dout.writeUTF(userResponse+" -> Result: 4B_0W - You win !!\n");
                            broadcastToAll(this,"Someone already guessed the code! Game over! The code was: "+secretCode+'\n');
                            //disconnectAllClients();
                            break;
                        }else{
                            dout.writeUTF(gameResponse.guessResponse(userResponse, secretCode)+'\n');
                            dout.flush();
                        }
                        guessLeft--;
                    }
                    else {
                        dout.writeUTF("\n"+userResponse+" -> INVALID GUESS\n");
                        dout.flush();
                    }

                }
                if(userResponse.equals(secretCode)) {

                    SomeoneWon = true;
                    shouldStop = true;

                    /**TO DO: add LINE**/
                }

                /**TO DO: add LINE**/

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
