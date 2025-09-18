package com.std.atm;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class User {
    private String username;
    private String password;
    private double balance;

    public User(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/atm_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root"; // Change to your MySQL username
    private static final String DB_PASSWORD = "Aaki1234"; // Change to your MySQL password
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ("
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "username VARCHAR(50) UNIQUE, "
            + "password VARCHAR(50), "
            + "balance DECIMAL(10,2))";

    public DatabaseHelper() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_USERS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database connection error: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void insertUser(String username, String password, double balance) throws SQLException {
        String sql = "INSERT INTO users(username, password, balance) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setDouble(3, balance);
            pstmt.executeUpdate();
        }
    }

    public void updateUserBalance(String username, double newBalance) throws SQLException {
        String sql = "UPDATE users SET balance = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }

    public void updateUserPassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }

    public User getUser(String username) throws SQLException {
        String sql = "SELECT username, password, balance FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String user = rs.getString("username");
                String pass = rs.getString("password");
                double balance = rs.getDouble("balance");
                return new User(user, pass, balance);
            }
        }
        return null;
    }

    public boolean hasUsers() throws SQLException {
        String sql = "SELECT count(*) FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}

public class ATM extends JFrame {
    private DatabaseHelper dbHelper;
    private User currentUser;
    private JTextField amountField;
    private JTextArea messageArea;
    private JLabel balanceLabel;
    private JLabel welcomeLabel;

    public ATM(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.currentUser = user;
        
        setTitle("ATM Machine");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create components
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton balanceButton = new JButton("Check Balance");
        JButton changePinButton = new JButton("Change PIN");
        JButton exitButton = new JButton("Exit");
        
        amountField = new JTextField();
        messageArea = new JTextArea(10, 30);
        messageArea.setEditable(false);
        balanceLabel = new JLabel("Current Balance: $" + String.format("%.2f", currentUser.getBalance()), SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.BLUE);
        
        // Add components to panels
        buttonPanel.add(withdrawButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(balanceButton);
        buttonPanel.add(changePinButton);
        buttonPanel.add(exitButton);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Amount: $"), BorderLayout.WEST);
        inputPanel.add(amountField, BorderLayout.CENTER);
        
        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.add(welcomeLabel);
        northPanel.add(balanceLabel);
        
        // Add panels to frame
        add(northPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
        add(new JScrollPane(messageArea), BorderLayout.SOUTH);
        
        // Add action listeners
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleWithdraw();
            }
        });
        
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeposit();
            }
        });
        
        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBalance();
            }
        });
        
        changePinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangePin();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        updateBalanceDisplay();
        promptForInitialDeposit();
    }
    
    private void promptForInitialDeposit() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                this,
                "Welcome " + currentUser.getUsername() + "! Please enter initial deposit amount:",
                "Initial Deposit",
                JOptionPane.PLAIN_MESSAGE);
            
            if (input == null) {
                messageArea.setText("Initial deposit canceled. Account balance remains $0.00\n");
                break;
            }
            
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Initial deposit must be positive!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        currentUser.setBalance(amount);
                        dbHelper.updateUserBalance(currentUser.getUsername(), amount);
                        messageArea.setText(String.format("Initial deposit of $%.2f successful\n", amount));
                        updateBalanceDisplay();
                        break;
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Database error: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleWithdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                messageArea.setText("Error: Withdrawal amount must be positive.\n");
                return;
            }
            
            if (amount > currentUser.getBalance()) {
                messageArea.setText("Error: Insufficient funds.\n");
                return;
            }
            
            currentUser.setBalance(currentUser.getBalance() - amount);
            dbHelper.updateUserBalance(currentUser.getUsername(), currentUser.getBalance());
            messageArea.setText(String.format("Successfully withdrew $%.2f\n", amount));
            updateBalanceDisplay();
        } catch (NumberFormatException e) {
            messageArea.setText("Error: Please enter a valid number.\n");
        } catch (SQLException e) {
            messageArea.setText("Database error: " + e.getMessage() + "\n");
        }
        amountField.setText("");
    }
    
    private void handleDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                messageArea.setText("Error: Deposit amount must be positive.\n");
                return;
            }
            
            currentUser.setBalance(currentUser.getBalance() + amount);
            dbHelper.updateUserBalance(currentUser.getUsername(), currentUser.getBalance());
            messageArea.setText(String.format("Successfully deposited $%.2f\n", amount));
            updateBalanceDisplay();
        } catch (NumberFormatException e) {
            messageArea.setText("Error: Please enter a valid number.\n");
        } catch (SQLException e) {
            messageArea.setText("Database error: " + e.getMessage() + "\n");
        }
        amountField.setText("");
    }
    
    private void checkBalance() {
        messageArea.setText(String.format("Current balance: $%.2f\n", currentUser.getBalance()));
    }
    
    private void handleChangePin() {
        JPanel changePinPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel currentPinLabel = new JLabel("Current PIN:");
        JPasswordField currentPinField = new JPasswordField();
        JLabel newPinLabel = new JLabel("New PIN:");
        JPasswordField newPinField = new JPasswordField();
        JLabel confirmPinLabel = new JLabel("Confirm New PIN:");
        JPasswordField confirmPinField = new JPasswordField();
        
        changePinPanel.add(currentPinLabel);
        changePinPanel.add(currentPinField);
        changePinPanel.add(newPinLabel);
        changePinPanel.add(newPinField);
        changePinPanel.add(confirmPinLabel);
        changePinPanel.add(confirmPinField);
        
        int option = JOptionPane.showConfirmDialog(
            this, 
            changePinPanel, 
            "Change PIN", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String currentPin = new String(currentPinField.getPassword());
            String newPin = new String(newPinField.getPassword());
            String confirmPin = new String(confirmPinField.getPassword());
            
            if (!currentPin.equals(currentUser.getPassword())) {
                JOptionPane.showMessageDialog(
                    this,
                    "Current PIN is incorrect!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPin.equals(confirmPin)) {
                JOptionPane.showMessageDialog(
                    this,
                    "New PIN and confirmation do not match!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (newPin.length() < 4) {
                JOptionPane.showMessageDialog(
                    this,
                    "PIN must be at least 4 characters!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                dbHelper.updateUserPassword(currentUser.getUsername(), newPin);
                currentUser.setPassword(newPin);
                messageArea.setText("PIN successfully changed!\n");
                JOptionPane.showMessageDialog(
                    this,
                    "PIN changed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateBalanceDisplay() {
        balanceLabel.setText(String.format("Current Balance: $%.2f", currentUser.getBalance()));
    }

    public static void main(String[] args) {
        // Initialize database
        DatabaseHelper dbHelper = new DatabaseHelper();
        
        // Create default user if no users exist
        try {
            if (!dbHelper.hasUsers()) {
                dbHelper.insertUser("user", "1234", 0.0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Login dialog
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        loginPanel.add(userLabel);
        loginPanel.add(userField);
        loginPanel.add(passLabel);
        loginPanel.add(passField);
        
        int attempts = 0;
        boolean loggedIn = false;
        User[] currentUserHolder = new User[1]; // Using an array to hold the user
        
        while (attempts < 3 && !loggedIn) {
            int option = JOptionPane.showConfirmDialog(
                null, 
                loginPanel, 
                "ATM Login", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
            
            if (option == JOptionPane.OK_OPTION) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                
                try {
                    User user = dbHelper.getUser(username);
                    if (user != null && user.getPassword().equals(password)) {
                        currentUserHolder[0] = user; // Store in the array
                        loggedIn = true;
                    } else {
                        attempts++;
                        JOptionPane.showMessageDialog(
                            null, 
                            "Invalid credentials! Attempts left: " + (3 - attempts), 
                            "Login Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(
                        null, 
                        "Database error: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            } else {
                JOptionPane.showMessageDialog(
                    null, 
                    "Login canceled. Exiting.", 
                    "Canceled", 
                    JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
        
        if (loggedIn) {
            SwingUtilities.invokeLater(() -> {
                new ATM(dbHelper, currentUserHolder[0]).setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(
                null, 
                "Too many failed attempts. Exiting.", 
                "Access Denied", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}