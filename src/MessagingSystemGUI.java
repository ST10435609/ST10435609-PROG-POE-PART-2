import javax.swing.*;
import java.util.*;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

public class MessagingSystemGUI {
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static Map<String, String> userCredentials = new HashMap<>();
    private static String currentUser = null;
    private static final String MESSAGES_FILE = "messages.json";

    public static void main(String[] args) {
        loadMessagesFromFile();
        showWelcomeScreen();
    }

    private static void showWelcomeScreen() {
        Object[] options = {"Login", "Register", "Exit"};
        int choice = JOptionPane.showOptionDialog(null,
                "Welcome to QuickClick Messaging System",
                "Welcome",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                if (loginUser()) {
                    runMainMenu();
                } else {
                    showWelcomeScreen();
                }
                break;
            case 1:
                registerUser();
                showWelcomeScreen();
                break;
            case 2:
            default:
                saveMessagesToFile();
                System.exit(0);
        }
    }

    private static void registerUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] fields = {
                "Username (must contain _ and be ≤5 chars):", usernameField,
                "Password (8+ chars, 1 uppercase, 1 number, 1 special char):", passwordField
        };

        while (true) {
            int option = JOptionPane.showConfirmDialog(null, fields, "Registration", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (!username.matches(".*_.*") || username.length() > 5) {
                JOptionPane.showMessageDialog(null,
                        "Username must contain underscore and be ≤5 characters",
                        "Invalid Username",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (userCredentials.containsKey(username)) {
                JOptionPane.showMessageDialog(null,
                        "Username already exists",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$")) {
                JOptionPane.showMessageDialog(null,
                        "Password must contain:\n- 8+ characters\n- 1 uppercase letter\n- 1 number\n- 1 special character",
                        "Invalid Password",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            userCredentials.put(username, password);
            JOptionPane.showMessageDialog(null,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private static boolean loginUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return false;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            currentUser = username;
            return true;
        }

        JOptionPane.showMessageDialog(null,
                "Invalid username or password",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private static void runMainMenu() {
        while (true) {
            Object[] options = {"Send Messages", "Show recently sent messages", "Quit"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Welcome " + currentUser + "!",
                    "QuickClick",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0:
                    sendMessages();
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "Coming Soon.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 2:
                case JOptionPane.CLOSED_OPTION:
                    currentUser = null;
                    return;
            }
        }
    }

    private static void sendMessages() {
        String numMessagesStr = JOptionPane.showInputDialog(null,
                "How many messages would you like to send?",
                "Send Messages",
                JOptionPane.QUESTION_MESSAGE);

        if (numMessagesStr == null || numMessagesStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Number of messages cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int numMessages = Integer.parseInt(numMessagesStr);
            if (numMessages <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int successfullySent = 0;
            for (int i = 0; i < numMessages; i++) {
                Message message = createAndValidateMessage(i + 1, numMessages);
                if (message == null) continue;

                int sendStatus = handleMessageOptions(message);
                if (sendStatus == 1) {
                    successfullySent++;
                }
            }
            totalMessagesSent += successfullySent;

            JOptionPane.showMessageDialog(null,
                    "Successfully sent " + successfullySent + " of " + numMessages + " messages",
                    "Sending Complete",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Message createAndValidateMessage(int current, int total) {
        while (true) {
            JTextField recipientField = new JTextField();
            JTextArea messageArea = new JTextArea(5, 20);
            messageArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(messageArea);

            Object[] messageComponents = {
                    "Message " + current + " of " + total,
                    "Recipient phone number (e.g. +27831234567):", recipientField,
                    "Message:", scrollPane
            };

            int option = JOptionPane.showConfirmDialog(null,
                    messageComponents,
                    "Create Message",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return null;
            }

            String recipient = recipientField.getText().trim();
            String messageText = messageArea.getText().trim();

            if (recipient.isEmpty() || messageText.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Recipient and message cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!recipient.matches("^\\+\\d{1,4}\\d{10}$")) {
                JOptionPane.showMessageDialog(null,
                        "Phone number must start with country code (e.g. +27) followed by exactly 10 digits\n\n" +
                                "Example: +27831234567",
                        "Invalid Phone Number",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (messageText.length() > 250) {
                JOptionPane.showMessageDialog(null,
                        "Message exceeds 250 characters by " + (messageText.length() - 250),
                        "Message Too Long",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            return new Message(recipient, messageText);
        }
    }

    private static int handleMessageOptions(Message message) {
        Object[] options = {"Send Message", "Disregard Message", "Store Message"};
        int choice = JOptionPane.showOptionDialog(null,
                "Message Options\n\n" +
                        "To: " + message.getRecipient() + "\n" +
                        "Message: " + (message.getMessageText().length() > 30 ?
                        message.getMessageText().substring(0, 30) + "..." : message.getMessageText()),
                "Message ID: " + message.getMessageId(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                message.sendMessage();
                sentMessages.add(message);
                saveMessagesToFile();
                return 1;
            case 1:
                JOptionPane.showMessageDialog(null, "Message discarded", "Info", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            case 2:
                storedMessages.add(message);
                saveMessagesToFile();
                JOptionPane.showMessageDialog(null, "Message stored", "Success", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            default:
                return 0;
        }
    }

    private static void saveMessagesToFile() {
        try {
            JSONObject jsonData = new JSONObject();
            JSONArray sentArray = new JSONArray();
            JSONArray storedArray = new JSONArray();

            for (Message msg : sentMessages) {
                sentArray.put(messageToJson(msg));
            }

            for (Message msg : storedMessages) {
                storedArray.put(messageToJson(msg));
            }

            jsonData.put("sentMessages", sentArray);
            jsonData.put("storedMessages", storedArray);
            jsonData.put("totalMessagesSent", totalMessagesSent);

            try (FileWriter file = new FileWriter(MESSAGES_FILE)) {
                file.write(jsonData.toString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving messages: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static JSONObject messageToJson(Message message) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("messageId", message.getMessageId());
        jsonMessage.put("recipient", message.getRecipient());
        jsonMessage.put("messageText", message.getMessageText());
        jsonMessage.put("timestamp", message.getTimestamp().getTime());
        return jsonMessage;
    }

    private static void loadMessagesFromFile() {
        File file = new File(MESSAGES_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            StringBuilder jsonString = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }

            JSONObject jsonData = new JSONObject(jsonString.toString());
            totalMessagesSent = jsonData.getInt("totalMessagesSent");

            JSONArray sentArray = jsonData.getJSONArray("sentMessages");
            for (int i = 0; i < sentArray.length(); i++) {
                sentMessages.add(jsonToMessage(sentArray.getJSONObject(i)));
            }

            JSONArray storedArray = jsonData.getJSONArray("storedMessages");
            for (int i = 0; i < storedArray.length(); i++) {
                storedMessages.add(jsonToMessage(storedArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading messages: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static Message jsonToMessage(JSONObject jsonMessage) {
        String recipient = jsonMessage.getString("recipient");
        String messageText = jsonMessage.getString("messageText");
        Message message = new Message(recipient, messageText);
        message.setTimestamp(new Date(jsonMessage.getLong("timestamp")));
        return message;
    }
}