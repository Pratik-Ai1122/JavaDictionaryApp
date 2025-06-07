package dictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginGUI {

    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JLabel statusLabel;

    public LoginGUI() {
        frame = new JFrame("Login - Dictionary App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Center on screen

        // Main panel with padding
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title label
        JLabel titleLabel = new JLabel("🔐 Login to Dictionary", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Email label
        JLabel emailLabel = new JLabel("📧 Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++;
        gbc.gridwidth = 1;
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

        // Button panel
        loginButton = new JButton("🚪 Login");
        loginButton.setBackground(new Color(65, 105, 225));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        registerButton = new JButton("📝 Register");
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridy++;
        panel.add(buttonPanel, gbc);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);

        // Event listeners
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterGUI();
        });
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT username FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                frame.dispose();
                new DictionaryGUI(username);
            } else {
                statusLabel.setText("❌ Invalid email or password.");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            statusLabel.setText("⚠️ Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}
