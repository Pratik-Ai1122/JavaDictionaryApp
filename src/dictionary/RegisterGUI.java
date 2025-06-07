package dictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterGUI {

    private JFrame frame;
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;
    private JLabel statusLabel;

    public RegisterGUI() {
        frame = new JFrame("Register - Dictionary App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 420);
        frame.setLocationRelativeTo(null); // Center on screen

        // Main panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 245, 238)); // Light peach
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("📝 Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username label
        JLabel usernameLabel = new JLabel("👤 Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);

        // Username field
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        panel.add(usernameField, gbc);

        // Email label
        JLabel emailLabel = new JLabel("📧 Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        panel.add(emailLabel, gbc);

        // Email field
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        panel.add(emailField, gbc);

        // Password label
        JLabel passwordLabel = new JLabel("🔑 Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        panel.add(passwordLabel, gbc);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        panel.add(passwordField, gbc);

        // Status label
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(Color.RED);
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        // Buttons
        registerButton = new JButton("✅ Register");
        registerButton.setBackground(new Color(65, 105, 225)); // Royal Blue
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        backButton = new JButton("🔙 Back to Login");
        backButton.setBackground(new Color(255, 165, 0)); // Orange
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 245, 238));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridy++;
        panel.add(buttonPanel, gbc);

        // Add everything
        frame.add(panel);
        frame.setVisible(true);

        // Actions
        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> {
            frame.dispose();
            new LoginGUI();
        });
    }

    private void register() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ All fields are required.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                statusLabel.setText("❌ Email is already registered.");
                rs.close();
                checkStmt.close();
                return;
            }

            rs.close();
            checkStmt.close();

            String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();
            stmt.close();

            JOptionPane.showMessageDialog(frame, "✅ Registered successfully! You can now login.");
            frame.dispose();
            new LoginGUI();
        } catch (SQLException e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
        }
    }
}
