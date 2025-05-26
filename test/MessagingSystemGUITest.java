import org.junit.Test;
import static org.junit.Assert.*;
import org.json.JSONObject;
import java.util.Date;

public class MessagingSystemGUITest {

    @Test
    public void testMessageCreation() {
        String recipient = "+27831234567";
        String messageText = "Test message";
        Message message = new Message(recipient, messageText);
        
        // Verify basic properties
        assertNotNull("Message should not be null", message);
        assertEquals("Recipient should match", recipient, message.getRecipient());
        assertEquals("Message text should match", messageText, message.getMessageText());
        assertNotNull("Timestamp should be set", message.getTimestamp());
        
        // Verify message ID pattern without exact value matching
        verifyMessageIdFormat(message.getMessageId());
    }

    @Test
    public void testMessageSerializationRoundTrip() {
        // Create original message
        Message original = new Message("+27123456789", "Round trip test");
        
        // Verify original ID format
        verifyMessageIdFormat(original.getMessageId());
        
        // Convert to JSON and back
        JSONObject json = MessagingSystemGUI.messageToJson(original);
        Message restored = MessagingSystemGUI.jsonToMessage(json);
        
        // Verify restored ID format
        verifyMessageIdFormat(restored.getMessageId());
        
        // Verify all other properties match
        assertEquals("Recipient should match after round trip",
                   original.getRecipient(), restored.getRecipient());
        assertEquals("Message text should match after round trip",
                   original.getMessageText(), restored.getMessageText());
        assertEquals("Timestamp should match after round trip",
                   original.getTimestamp(), restored.getTimestamp());
    }

    private void verifyMessageIdFormat(String messageId) {
        assertNotNull("Message ID should not be null", messageId);
        assertTrue("Message ID should start with MSG", 
                 messageId.startsWith("MSG"));
        assertEquals("Message ID should be 7 characters", 
                   7, messageId.length());
        assertTrue("Message ID should end with digits",
                 messageId.substring(3).matches("\\d{4}"));
    }

    @Test
    public void testStaticMessageValidation() {
        // Test phone validation
        assertTrue("Valid number should pass", 
                 Message.validatePhoneNumber("+27831234567"));
        assertFalse("Invalid number should fail",
                  Message.validatePhoneNumber("0831234567"));
        
        // Test message length validation
        String longMessage = new String(new char[251]).replace('\0', 'a');
        assertFalse("Long message should fail",
                  Message.validateMessageLength(longMessage));
        assertTrue("250 char message should pass",
                 Message.validateMessageLength(new String(new char[250]).replace('\0', 'a')));
    }

    @Test
    public void testMessageHashGeneration() {
        Message message = new Message("+27831234567", "First Middle Last");
        String hash = message.createMessageHash();
        
        // Verify hash format without depending on exact ID
        assertTrue("Hash should match pattern: XX:FIRSTLAST",
                 hash.matches("\\d{2}:FIRST[A-Z]*LAST"));
        
        // Get the first two digits from the message ID
        String idDigits = message.getMessageId().substring(3, 5);
        assertTrue("Hash should start with digits from message ID",
                 hash.startsWith(idDigits + ":"));
    }
}