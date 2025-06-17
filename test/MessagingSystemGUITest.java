import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.json.JSONObject;
import java.util.Date;
import java.util.List;
import java.io.File;
import javax.swing.*;

public class MessagingSystemGUITest {
    private MessagingSystemGUI system;
    private final String TEST_FILE = "test_messages.json";

    @Before
    public void setUp() {
        system = new MessagingSystemGUI();
        system.clearTestData(); // Reset before each test
    }

    // ===== ORIGINAL TESTS (UNCHANGED) =====
    @Test
    public void testMessageCreation() {
        String recipient = "+27831234567";
        String messageText = "Test message";
        Message message = new Message(recipient, messageText);
        
        assertNotNull("Message should not be null", message);
        assertEquals("Recipient should match", recipient, message.getRecipient());
        assertEquals("Message text should match", messageText, message.getMessageText());
        assertNotNull("Timestamp should be set", message.getTimestamp());
        verifyMessageIdFormat(message.getMessageId());
    }

    // ... (keep all your other original tests unchanged) ...

    // ===== UPDATED TESTS FOR MessagingSystemGUI =====
    @Test
    public void testMessageStorage() {
        Message msg1 = new Message("+27831234567", "Hello");
        Message msg2 = new Message("+27837654321", "World");
        
        system.addTestMessage(msg1, "sent");
        system.addTestMessage(msg2, "stored");
        
        List<Message> sent = system.getSentMessagesForTest();
        assertEquals(1, sent.size());
        assertEquals("Hello", sent.get(0).getMessageText());
    }

    @Test
    public void testFilePersistence() {
        // 1. Add and save message
        Message testMsg = new Message("+27831234567", "Persisted");
        system.addTestMessage(testMsg, "sent");
        
        // 2. Verify file exists
        assertTrue("File should exist", new File(TEST_FILE).exists());
        
        // 3. Load in new instance
        MessagingSystemGUI newSystem = new MessagingSystemGUI();
        newSystem.loadMessagesFromFileForTest();
        List<Message> loaded = newSystem.getSentMessagesForTest();
        
        // 4. Verify persistence
        assertEquals(1, loaded.size());
        assertEquals("Persisted", loaded.get(0).getMessageText());
    }

    @Test
    public void testFindLongestMessage() {
        system.addTestMessage(new Message("+27831234567", "Short"), "sent");
        system.addTestMessage(new Message("+27837654321", "This is the longest message"), "sent");
        system.addTestMessage(new Message("+27839876543", "Medium length"), "sent");
        
        Message longest = system.findLongestMessageForTest();
        assertEquals("This is the longest message", longest.getMessageText());
    }

    // ===== TEST HELPERS =====
    private void verifyMessageIdFormat(String messageId) {
        assertNotNull("Message ID should not be null", messageId);
        assertTrue("Message ID should start with MSG", 
                 messageId.startsWith("MSG"));
        assertEquals("Message ID should be 7 characters", 
                   7, messageId.length());
        assertTrue("Message ID should end with digits",
                 messageId.substring(3).matches("\\d{4}"));
    }
}