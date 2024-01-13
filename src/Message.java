import java.io.Serializable;

public class Message implements Serializable {
    private String message;
    private String trainingFeedback;
    private boolean trainingMode;
    public Message(){
        message=""; trainingFeedback="";trainingMode=false;
    }
    public Message(String s){
        message = s;
    }

    public String getContent(){
        return message;
    }
    public String getTrainingFeedback(){return trainingFeedback;}
    public boolean getMode(){return trainingMode;}

    public void updateMessage(String s){
        message = s;
    }
    public void updateTraininngFeeback(String s){trainingFeedback = s;}
    public void updateMode(boolean b){trainingMode=b;}

    public Message updateAndGetMessage(String s){
        message = s;
        return this;
    }
}
