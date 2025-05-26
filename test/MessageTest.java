import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testMessageInitialization() {
        Message message = new Message("+27831234567", "Hello there");

        assertNotNull(message.getMessageId());
        assertEquals("+27831234567", message.getRecipient());
        assertEquals("Hello there", message.getMessageText());
        assertNotNull(message.getTimestamp());
    }

    @Test
    public void testCreateMessageHash_basic() {
        Message message = new Message("+27831234567", "Hello world");
        String hash = message.createMessageHash();

        assertTrue(hash.matches("^\\d{2}:[A-Z]+[A-Z]+$"), "Hash format invalid: " + hash);
    }

    @Test
    public void testCreateMessageHash_singleWord() {
        Message message = new Message("+27831234567", "Hello");
        String hash = message.createMessageHash();

        assertTrue(hash.matches("^\\d{2}:[A-Z]+[A-Z]+$"));
    }

    @Test
    public void testCreateMessageHash_emptyMessage() {
        Message message = new Message("+27831234567", "");
        String hash = message.createMessageHash();

        assertTrue(hash.startsWith("00:"));
    }

    @Test
    public void testTimestampIsSet() {
        Message message = new Message("+27831234567", "Test");
        Date timestamp = message.getTimestamp();

        assertNotNull(timestamp);
        long now = System.currentTimeMillis();
        assertTrue(timestamp.getTime() <= now && timestamp.getTime() > now - 10000); // within last 10s
    }
}
