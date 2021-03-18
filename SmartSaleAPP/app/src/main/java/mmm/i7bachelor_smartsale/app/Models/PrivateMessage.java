package mmm.i7bachelor_smartsale.app.Models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

public class PrivateMessage implements Serializable {

    private String messageBody, messageDate, receiver, sender, regarding, path;
    private Boolean messageRead;

    public PrivateMessage(){
    }

    public PrivateMessage(String receiver, String sender, String messageBody, String messageDate,
                          Boolean messageRead, String regarding, String path) {
        this.receiver = receiver;
        this.sender = sender;
        this.messageBody = messageBody;
        this.messageDate = messageDate;
        this.messageRead = messageRead;
        this.regarding = regarding;
        this.path = path;
    }


    public static PrivateMessage fromSnapshot(DocumentSnapshot d, String sender) {
        PrivateMessage message = new PrivateMessage(d.get("Receiver").toString(),
                sender,
                d.get("MessageBody").toString(),
                d.get("MessageDate").toString(),
                Boolean.parseBoolean(d.get("Read").toString()),
                d.get("Regarding").toString(), d.get("Path").toString());
        return message;
    }

    public String getRegarding(){return this.regarding;}

    public void setRegarding(String regarding){this.regarding = regarding;}

    public String getReceiver(){return receiver;}

    public void setReceiver(String receiver){this.receiver = receiver;}

    public String getSender(){return sender;}

    public void setSender(String sender){this.sender = sender;}

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public Boolean getMessageRead() {
        return messageRead;
    }

    public void setMessageRead(Boolean messageRead) {
        this.messageRead = messageRead;
    }

    public String getPath(){return this.path;}
}
