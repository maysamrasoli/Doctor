package ir.medxhub.doctor.message;

/**
 * Created by Ali on 2/15/2015.
 */
public class MessageItem {
    private int id, messageType;
    private String message, title, createdAt, receivedAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return createdAt;
    }

    public void setDate(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getreceivedAt() {
        return receivedAt;
    }

    public void setRecivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }
}
