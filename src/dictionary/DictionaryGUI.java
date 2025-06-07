package dictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.sql.*;

import com.sun.speech.freetts.*;

public class DictionaryGUI {

    private JFrame frame;
    private JTextField searchField;
    private JButton searchButton, pronounceButton, addButton, clearButton, exitButton, exportButton, historyButton;
    private JTextArea resultArea;
    private JToggleButton themeToggle;
    private boolean isDarkMode = false;

    private String username; // 👤 Holds the logged-in username

    public DictionaryGUI(String username) {
        this.username = username;

        frame = new JFrame("Java Dictionary App - Welcome, " + username);
        frame.setSize(800, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 🔝 Top panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());

        JLabel label = new JLabel("Enter word:");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        pronounceButton = new JButton("\uD83D\uDD0A Pronounce");
        themeToggle = new JToggleButton("🌞 Light Mode");

        inputPanel.add(label);
        inputPanel.add(searchField);
        inputPanel.add(searchButton);
        inputPanel.add(pronounceButton);
        inputPanel.add(themeToggle);

        JLabel userLabel = new JLabel("👤 " + username);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        searchPanel.add(inputPanel, BorderLayout.CENTER);
        searchPanel.add(userLabel, BorderLayout.EAST);

        // 🖥️ Center
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Serif", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // ⬇️ Bottom panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        addButton = new JButton("\u2795 Add Word");
        clearButton = new JButton("\uD83E\uDEB9 Clear");
        exportButton = new JButton("\u2B07 Export");
        historyButton = new JButton("\uD83D\uDCDC History");
        exitButton = new JButton("\u274C Exit");

        controlPanel.add(addButton);
        controlPanel.add(clearButton);
        controlPanel.add(exportButton);
        controlPanel.add(historyButton);
        controlPanel.add(exitButton);

        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        applyLightTheme(); // 🌞 Initial theme

        // 🎯 Actions
        searchButton.addActionListener(e -> {
            String word = searchField.getText().trim();
            if (!word.isEmpty()) searchWord(word);
            else resultArea.setText("⚠️ Please enter a word.");
        });

        pronounceButton.addActionListener(e -> {
            String word = searchField.getText().trim();
            if (!word.isEmpty()) speak(word);
            else JOptionPane.showMessageDialog(frame, "Please enter a word to pronounce.");
        });

        clearButton.addActionListener(e -> {
            searchField.setText("");
            resultArea.setText("");
        });

        exitButton.addActionListener(e -> System.exit(0));

        addButton.addActionListener(e -> showAddWordDialog());

        exportButton.addActionListener(e -> exportToFile());

        historyButton.addActionListener(e -> showSearchHistory());

        themeToggle.addActionListener(e -> {
            if (themeToggle.isSelected()) {
                isDarkMode = true;
                applyDarkTheme();
                themeToggle.setText("🌙 Dark Mode");
            } else {
                isDarkMode = false;
                applyLightTheme();
                themeToggle.setText("🌞 Light Mode");
            }
        });

        frame.setVisible(true);
    }

    private void applyDarkTheme() {
        Color bg = new Color(45, 45, 45);
        Color fg = new Color(230, 230, 230);

        frame.getContentPane().setBackground(bg);
        for (Component comp : frame.getContentPane().getComponents()) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            if (comp instanceof JPanel) {
                for (Component sub : ((JPanel) comp).getComponents()) {
                    sub.setBackground(new Color(60, 60, 60));
                    sub.setForeground(fg);
                }
            }
        }
        resultArea.setBackground(new Color(30, 30, 30));
        resultArea.setForeground(fg);
    }

    private void applyLightTheme() {
        Color bg = Color.WHITE;
        Color fg = Color.BLACK;

        frame.getContentPane().setBackground(bg);
        for (Component comp : frame.getContentPane().getComponents()) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            if (comp instanceof JPanel) {
                for (Component sub : ((JPanel) comp).getComponents()) {
                    sub.setBackground(new Color(176, 224, 230));
                    sub.setForeground(fg);
                }
            }
        }
        resultArea.setBackground(Color.WHITE);
        resultArea.setForeground(Color.BLACK);
    }

    private void searchWord(String word) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM words WHERE word = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String definition = rs.getString("definition");
                String synonyms = rs.getString("synonyms");
                String antonyms = rs.getString("antonyms");
                String example = rs.getString("example_sentence");

                resultArea.setText("\uD83D\uDCD6 Definition:\n" + definition + "\n\n"
                        + "\uD83D\uDD01 Synonyms:\n" + synonyms + "\n\n"
                        + "\uD83D\uDEAB Antonyms:\n" + antonyms + "\n\n"
                        + "\u270D\uFE0F Example Sentence:\n" + example);
            } else {
                resultArea.setText("❌ Word not found in dictionary.");
            }

            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO search_history (word) VALUES (?)");
            insertStmt.setString(1, word);
            insertStmt.executeUpdate();

            rs.close();
            stmt.close();
            insertStmt.close();

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private void exportToFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("WordDetails.txt"));
            int option = fileChooser.showSaveDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
                writer.write(resultArea.getText());
                writer.close();
                JOptionPane.showMessageDialog(frame, "✅ Exported successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "❌ Error exporting: " + e.getMessage());
        }
    }

    private void showSearchHistory() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT word, searched_at FROM search_history ORDER BY searched_at DESC LIMIT 20";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            StringBuilder history = new StringBuilder();
            while (rs.next()) {
                history.append(rs.getString("searched_at"))
                        .append(" - ")
                        .append(rs.getString("word"))
                        .append("\n");
            }
            rs.close();
            stmt.close();

            JTextArea historyArea = new JTextArea(history.toString(), 15, 30);
            historyArea.setEditable(false);
            JOptionPane.showMessageDialog(frame, new JScrollPane(historyArea),
                    "Recent Search History", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "❌ Error: " + e.getMessage());
        }
    }

    private void showAddWordDialog() {
        JTextField wordField = new JTextField();
        JTextField defField = new JTextField();
        JTextField synField = new JTextField();
        JTextField antField = new JTextField();
        JTextField exField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Word:"));
        panel.add(wordField);
        panel.add(new JLabel("Definition:"));
        panel.add(defField);
        panel.add(new JLabel("Synonyms:"));
        panel.add(synField);
        panel.add(new JLabel("Antonyms:"));
        panel.add(antField);
        panel.add(new JLabel("Example Sentence:"));
        panel.add(exField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add New Word", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            addWordToDatabase(
                    wordField.getText().trim(),
                    defField.getText().trim(),
                    synField.getText().trim(),
                    antField.getText().trim(),
                    exField.getText().trim()
            );
        }
    }

    private void addWordToDatabase(String word, String def, String syn, String ant, String ex) {
        if (word.isEmpty() || def.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Word and definition are required.");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO words (word, definition, synonyms, antonyms, example_sentence) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, word);
            stmt.setString(2, def);
            stmt.setString(3, syn);
            stmt.setString(4, ant);
            stmt.setString(5, ex);
            stmt.executeUpdate();
            stmt.close();
            JOptionPane.showMessageDialog(frame, "✅ Word added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "❌ Error: " + e.getMessage());
        }
    }

    private void speak(String text) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoice("kevin16");
        if (voice != null) {
            voice.allocate();
            voice.speak(text);
            voice.deallocate();
        } else {
            System.out.println("❌ Voice 'kevin16' not found.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DictionaryGUI("Guest")); // Replace "Guest" with actual username
    }
}
