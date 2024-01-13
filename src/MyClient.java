
import java.io.*;
import java.net.*;
import java.util.*;

public class MyClient {
    public static void main(String[] args) {

        try(Socket socket=new Socket("localhost",6666);){
            String input="",serverResponse;
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            Message messageToSend = new Message(), messageReceived = new Message();

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


            while(true){
                messageReceived = (Message)in.readObject();
                serverResponse = messageReceived.getContent();
                System.out.println("Server: " + serverResponse);
                if(serverResponse.contains("You win") || serverResponse.contains("You lose, boo-hoo") || serverResponse.contains("Someone already guessed the code")){
                    if(messageReceived.getMode()){
                        System.out.println("The game has ended, here is your game summary:\n"+messageReceived.getTrainingFeedback());
                    }
                    System.out.print("Client: Exiting...");
                    break;
                }

                //if server asked to type in your guess
                input = consoleReader.readLine();
                System.out.println("You typed: "+input);

                if(serverResponse.contains("(Y/N)")&&!input.equals("Y")){
                    System.out.print("See you next time!\nClient: Exiting...");
                    break;
                }//Client refused to play

                out.writeObject(messageToSend.updateAndGetMessage(input));
                out.reset();
                out.flush();
                messageReceived = (Message)in.readObject();
                serverResponse = messageReceived.getContent();
                System.out.println("Server: " + serverResponse);

            }
            out.close();
            in.close();
        }catch(Exception e){
            System.out.println(e);

        }

    }
}