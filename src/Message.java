import java.io.Serializable;

public class Message implements Serializable {
    private String message;
    public Message(){
        message="";
    }
    public Message(String s){
        message = s;
    }

    public String getContent(){
        return message;
    }

    public void updateMessage(String s){
        message = s;
    }

    public Message updateAndGetMessage(String s){
        message = s;
        return this;
    }
}
