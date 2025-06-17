import javax.swing.*;
import java.util.Date;

public class Message {
    
    
     public static boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\+\\d{1,4}\\d{10}$");
    }

    public static boolean validateMessageLength(String message) {
        return message != null && message.length() <= 250;
    }

  
    private String messageId;
    private String recipient;
    private String messageText;
    private Date timestamp;

    public Message(String recipient, String messageText) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.timestamp = new Date();
        this.messageId = generateMessageId();
    }

    private String generateMessageId() {
         long timePart = System.currentTimeMillis() % 10000;
        return String.format("MSG%04d", timePart);
    }

    public String createMessageHash() {
        String[] words = messageText.split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;

        String numbers = messageId.replaceAll("[^0-9]", "");
        String firstTwoNumbers = numbers.length() >= 2 ? numbers.substring(0, 2) : "00";

        return firstTwoNumbers + ":" + firstWord.toUpperCase() + lastWord.toUpperCase();
    }

    public void sendMessage() {
        String details = "--- Message Details ---\n" +
                "Message ID: " + messageId + "\n" +
                "Message Hash: " + createMessageHash() + "\n" +
                "Recipient: " + recipient + "\n" +
                "Message: " + messageText + "\n" +
                "Timestamp: " + timestamp + "\n" +
                "-----------------------";

        JOptionPane.showMessageDialog(null, details, "Message Sent", JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}