
import java.io.*;
import java.net.*;
import java.util.*;

public class MyClient {
    public static void main(String[] args) {

        try(Socket s=new Socket("localhost",6666);){
            String input="";
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            DataInputStream din = new DataInputStream(s.getInputStream());
            String serverResponse;

            while(true){
                serverResponse = din.readUTF();
                System.out.println("Server: " + serverResponse);
                if(serverResponse.contains("You win") || serverResponse.contains("You lose, boo-hoo") || serverResponse.contains("Someone already guessed the code")){
                    System.out.print("Client: Exiting...");
                    break;
                }

                //if server asked to type in your guess
                input = consoleReader.readLine();
                if(input.equals("N") || input.contains("n")){
                    System.out.print("See you next time!\nClient: Exiting...");
                    break;
                }
                dout.writeUTF(input);
                dout.flush();

            }
        }catch(Exception e){System.out.println(e);}
    }
}
