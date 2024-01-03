import java.util.*;

public class ResponseGenerator {
    String[] colors = GameConfiguration.colors;
    String history="HISTORY:";
    int pegNumber = GameConfiguration.pegNumber;
    public ResponseGenerator(){
    }

    private boolean isValidColor(String color){
        for(int i=0;i<colors.length;i++){
            if(color.equals(colors[i])) return true;
        }
        return false;
    }
    public boolean isValidGuess(String userGuess){
        if(userGuess.length() != pegNumber) return false;
        for(int i=0;i<pegNumber;i++){
            if(!isValidColor(userGuess.substring(i, i+1))) return false;
        }
        return true;
    }

    public String getHistory(){
        return history;
    }

    public String guessResponse(String userGuess, String secretCode){
        int blackPegs = 0, whitePeg = 0;
        Dictionary<String, Integer> guessDictionary = new Hashtable<>();
        Dictionary<String, Integer> answerDictionary = new Hashtable<>();

        for(int i=0;i<colors.length;i++){//set up dictionaries
            guessDictionary.put(GameConfiguration.colors[i], 0);
        }
        for(int i=0;i<colors.length;i++){
            answerDictionary.put(GameConfiguration.colors[i], 0);
        }

        for(int i=0;i<pegNumber;i++){
            String userColor = userGuess.substring(i, i+1);
            String secretColor = secretCode.substring(i,i+1);

            if(userColor.equals(secretColor)){//Same color at same position
                blackPegs++;
                guessDictionary.put(userColor, guessDictionary.get(userColor)-1);
                answerDictionary.put(secretColor, answerDictionary.get(secretColor)-1);
            }
            else{//record both separately for potential white pegs
                guessDictionary.put(userColor, guessDictionary.get(userColor)+1);
                answerDictionary.put(secretColor, answerDictionary.get(secretColor)+1);
            }
        }



        for(int i=0;i< colors.length;i++){
            int userColorTimesUsed = guessDictionary.get(colors[i]);
            int secretColorTimesUsed = answerDictionary.get(colors[i]);
            if(userColorTimesUsed > 0 && secretColorTimesUsed > 0){
                whitePeg += Math.min(secretColorTimesUsed,userColorTimesUsed);
            }
        }
        String pegNums = blackPegs+"B_"+whitePeg+"W\n";
        history += userGuess+"        "+ pegNums;
        return userGuess+" -> Result: "+pegNums;
    }
}
