package mmm.i7bachelor_smartsale.app.Models;

import com.google.firebase.firestore.DocumentSnapshot;

public class PrivateMessage  implements Comparable<PrivateMessage> {

    private String messageBody, messageDate, receiver, sender, path;
    private Boolean messageRead;

    public PrivateMessage(){
    }

    public PrivateMessage(String receiver, String sender, String messageBody, String messageDate,
                          Boolean messageRead, String path) {
        this.receiver = receiver;
        this.sender = sender;
        this.messageBody = messageBody;
        this.messageDate = messageDate;
        this.messageRead = messageRead;
        this.path = path;
    }


    public static PrivateMessage fromSnapshot(DocumentSnapshot doc) {
        if(doc != null)
        {
            PrivateMessage message = new PrivateMessage(doc.get("Receiver").toString(),
                    doc.get("Sender").toString(),
                    doc.get("MessageBody").toString(),
                    doc.get("MessageDate").toString(),
                    Boolean.parseBoolean(doc.get("Read").toString()),
                    doc.get("Path").toString());
            return message;
        }
        return new PrivateMessage();
    }

    @Override
    public int compareTo(PrivateMessage m) {
        return getMessageDate().compareTo(m.getMessageDate());
    }

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
