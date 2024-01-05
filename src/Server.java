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
        private final Socket clientSocket;
        private final String secretCode;
        private int guessLeft = GameConfiguration.guessNumber;
        private volatile boolean shouldStop = false;
        public ClientHandler(Socket clientSocket, String secretCode) {
            this.clientSocket = clientSocket;
            this.secretCode = secretCode;
        }

        public String parseClientMessage(ObjectInputStream stream){
            try{
                Message m = (Message) stream.readObject();
                return m.getContent();
            }catch(Exception e){
                System.out.println(e);
            }
            return "Something went wrong when parsing user input";
        }

        public void sendMessageToClient(ObjectOutputStream out, String message){
            try{
                Message m = new Message(message);
                out.writeObject(m);
                out.reset();
            }catch(Exception e){
                System.out.println(e);
            }
        }

        @Override
        public void run() {
            try {

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                String guess, userResponse;
                boolean endTheGame = false;
                Message MessageToClient = new Message(), MessageFromClient= new Message();

                sendMessageToClient(out, "Welcome to Mastermind.  Here are the rules.\n\n" +
                        "This is a text version of the classic board game Mastermind.\n\n" +
                        "The computer will think of a secret code. The code consists of 4 colored pegs. The pegs MUST be one of six colors: blue, green, orange, purple, red, or yellow. A color may appear more than once in the code. You try to guess what colored pegs are in the code and what order they are in. After you make a valid guess the result (feedback) will be displayed.\n\n" +
                        "The result consists of a black peg for each peg you have guessed exactly correct (color and position) in your guess. For each peg in the guess that is the correct color, but is out of position, you get a white peg. For each peg, which is fully incorrect, you get no feedback.\n\n" +
                        "Only the first letter of the color is displayed. B for Blue, R for Red, and so forth. When entering guesses you only need to enter the first character of each color as a capital letter.\n\n" +
                        "You have "+guessLeft +" guesses to figure out the secret code or you lose the game. Are you ready to play? (Y/N): \n");

                userResponse = parseClientMessage(in);
                if(userResponse.equals("Y")){
                    sendMessageToClient(out, "Great!  Let's play!\n");
                }
                else sendMessageToClient(out,"See you next time!");

//                if(!userResponse.equals("Y")){
//                    /** TO DO: add line**/
//                    try {
//                        clientSocket.close();
//                        return;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }

                System.out.println("\nGame started. Generating secret code ...");

                ResponseGenerator gameResponse = new ResponseGenerator();
                while(guessLeft > 0 && !shouldStop){
                    System.out.println("Enter case 1");
                    sendMessageToClient(out,"Secret code is: "+secretCode+"You have "+ guessLeft +" guesses left.\nWhat is your next guess?\nType in the characters for your guess and press enter.\nEnter guess: ");
                    userResponse = parseClientMessage(in);
                    System.out.println("User response is: "+userResponse);

                    if(userResponse.equals("HISTORY")){
                        sendMessageToClient(out,gameResponse.getHistory());
                    }
                    else if(gameResponse.isValidGuess(userResponse)) {//valid guess
                        System.out.println("Is a valid guess");
                        if(guessLeft == 1 && !userResponse.equals(secretCode)){//last guess
                            sendMessageToClient(out, "Sorry, you are out of guesses. You lose, boo-hoo.");
                            break;
                        }
                        else if(userResponse.equals(secretCode)){//game won
                            sendMessageToClient(out,userResponse+" -> Result: 4B_0W - You win !!");
                            broadcastToAll(this,"Someone already guessed the code! Game over! The code was: "+secretCode+'\n');
                            //disconnectAllClients();
                            break;
                        }else{//Generate response and continue the game
                            sendMessageToClient(out, gameResponse.guessResponse(userResponse, secretCode));
                        }
                        guessLeft--;
                        System.out.println("message is: "+MessageToClient.getContent());
                    }
                    else {
                        System.out.println("Is not a valid guess");
                        sendMessageToClient(out, userResponse+" -> INVALID GUESS\n");
                        System.out.println("message is: "+MessageToClient.getContent());
                    }
                }
                if(userResponse.equals(secretCode)) {
                    SomeoneWon = true;
                    shouldStop = true;
                    /**TO DO: add LINE**/
                }
                out.close();
                in.close();

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
