import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import javax.swing.border.LineBorder;

public class SimpleQuizApp extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // User data
    private String currentUser = null;
    private Map<String, String> users = new HashMap<>(); // username -> password hash
    private Map<String, UserStats> userStats = new HashMap<>();
    private Map<String, String> userNames = new HashMap<>(); // username -> full name
    private String currentUserFullName = null;
    
    // Quiz data
    private java.util.List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private java.util.List<Integer> userAnswers = new ArrayList<>();
    
    // UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    // Separate fields for login
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    // Separate fields for registration
    private JTextField registerUsernameField, registerEmailField, registerFirstNameField, registerLastNameField;
    private JPasswordField registerPasswordField, registerConfirmPasswordField;
    private JLabel questionLabel, scoreLabel, timerLabel;
    private JRadioButton[] optionButtons;
    private JTextArea resultsArea;
    
    // Timer
    private javax.swing.Timer quizTimer;
    private int timeRemaining = 60;
    
    // Add new UI components for advanced features
    private JTextArea profileStatsArea;
    private JTextArea leaderboardArea;
    private JLabel explanationLabel;
    private JPanel feedbackPanel;
    
    // Quiz history data
    private java.util.List<QuizHistoryEntry> quizHistory = new ArrayList<>();
    
    // Enhanced quiz data
    private String selectedCategory;
    private String selectedDifficulty;
    private java.util.List<Question> filteredQuestions = new ArrayList<>();
    
    // Modern color palette and gradients
    private static final Color PRIMARY_BG = new Color(236, 240, 251); // lighter, bluish
    private static final Color SIDEBAR_BG = new Color(30, 34, 45);
    private static final Color CARD_BG = new Color(255, 255, 255, 230); // semi-transparent white
    private static final Color CARD_BORDER = new Color(210, 215, 230);
    private static final Color PRIMARY = new Color(88, 101, 242); // vibrant blue
    private static final Color SUCCESS = new Color(56, 217, 169); // teal
    private static final Color WARNING = new Color(255, 195, 0); // yellow
    private static final Color TEXT_DARK = new Color(34, 40, 49);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color HOVER_BG = new Color(230, 240, 255); // for hover
    private static final Color HOVER_SIDEBAR = new Color(88, 101, 242, 180); // for sidebar hover
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font CARD_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private ButtonGroup optionsGroup; // Add this field for radio button grouping
    
    public SimpleQuizApp() {
        setTitle("Java Quiz App - Simple Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeData();
        createUI();
        showLoginScreen();
    }
    
    private void initializeData() {
        // Load or create sample users
        loadUsers();
        
        // Create sample questions
        createSampleQuestions();
    }
    
    private void loadUsers() {
        // Try to load users from file
        try {
            File file = new File("users.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length >= 7) {
                        users.put(parts[0], parts[1]);
                        if (parts.length >= 7) {
                            userNames.put(parts[0], parts[6]);
                        }
                        if (parts.length >= 6) {
                            userStats.put(parts[0], new UserStats(
                                Integer.parseInt(parts[2]),
                                Double.parseDouble(parts[3]),
                                Double.parseDouble(parts[4]),
                                Integer.parseInt(parts[5])
                            ));
                        }
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("Could not load users: " + e.getMessage());
        }
        
        // Create default admin user if no users exist
        if (users.isEmpty()) {
            users.put("admin", hashPassword("admin123"));
            userStats.put("admin", new UserStats());
        }
    }
    
    private void saveUsers() {
        try {
            PrintWriter writer = new PrintWriter("users.txt");
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String username = entry.getKey();
                String password = entry.getValue();
                UserStats stats = userStats.getOrDefault(username, new UserStats());
                String fullName = userNames.getOrDefault(username, "");
                writer.println(String.format("%s,%s,%d,%.1f,%.1f,%d,%s",
                    username, password, stats.totalQuizzes, stats.averageScore, 
                    stats.bestScore, stats.totalQuestions, fullName));
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Could not save users: " + e.getMessage());
        }
    }
    
    private void createSampleQuestions() {
        // Java Programming Questions
        questions.add(new Question(
            "What is the main method signature in Java?",
            Arrays.asList(
                "public static void main(String[] args)",
                "public void main(String[] args)",
                "static void main(String[] args)",
                "public static main(String[] args)"
            ),
            0,
            "Java Programming"
        ));
        
        questions.add(new Question(
            "Which keyword is used to inherit from a class in Java?",
            Arrays.asList("extends", "implements", "inherits", "super"),
            0,
            "Java Programming"
        ));
        
        questions.add(new Question(
            "What is the default value of an int variable in Java?",
            Arrays.asList("0", "null", "undefined", "-1"),
            0,
            "Java Programming"
        ));
        
        questions.add(new Question(
            "Which of the following is NOT a primitive data type in Java?",
            Arrays.asList("String", "int", "double", "boolean"),
            0,
            "Java Programming"
        ));
        
        questions.add(new Question(
            "What is the purpose of the 'final' keyword in Java?",
            Arrays.asList("To prevent inheritance", "To make a variable constant", "To end a program", "To create a loop"),
            1,
            "Java Programming"
        ));
        
        // Data Structures Questions
        questions.add(new Question(
            "Which data structure uses LIFO (Last In, First Out)?",
            Arrays.asList("Stack", "Queue", "Tree", "Graph"),
            0,
            "Data Structures"
        ));
        
        questions.add(new Question(
            "What is the time complexity of searching in a binary search tree?",
            Arrays.asList("O(log n)", "O(n)", "O(n²)", "O(1)"),
            0,
            "Data Structures"
        ));
        
        questions.add(new Question(
            "Which data structure is best for implementing a queue?",
            Arrays.asList("Array", "Linked List", "Stack", "Tree"),
            1,
            "Data Structures"
        ));
        
        questions.add(new Question(
            "What is the time complexity of inserting an element at the beginning of an array?",
            Arrays.asList("O(1)", "O(log n)", "O(n)", "O(n²)"),
            2,
            "Data Structures"
        ));
        
        questions.add(new Question(
            "Which of the following is a linear data structure?",
            Arrays.asList("Tree", "Graph", "Array", "Heap"),
            2,
            "Data Structures"
        ));
        
        // Algorithms Questions
        questions.add(new Question(
            "What is the time complexity of binary search?",
            Arrays.asList("O(log n)", "O(n)", "O(n²)", "O(1)"),
            0,
            "Algorithms"
        ));
        
        questions.add(new Question(
            "Which sorting algorithm has the best average-case time complexity?",
            Arrays.asList("QuickSort", "BubbleSort", "SelectionSort", "InsertionSort"),
            0,
            "Algorithms"
        ));
        
        questions.add(new Question(
            "What is the time complexity of bubble sort?",
            Arrays.asList("O(n)", "O(n log n)", "O(n²)", "O(log n)"),
            2,
            "Algorithms"
        ));
        
        questions.add(new Question(
            "Which algorithm is used to find the shortest path in a graph?",
            Arrays.asList("Breadth-First Search", "Depth-First Search", "Dijkstra's Algorithm", "Binary Search"),
            2,
            "Algorithms"
        ));
        
        questions.add(new Question(
            "What is the space complexity of merge sort?",
            Arrays.asList("O(1)", "O(log n)", "O(n)", "O(n²)"),
            2,
            "Algorithms"
        ));
        
        // Database Questions
        questions.add(new Question(
            "What does ACID stand for in database transactions?",
            Arrays.asList(
                "Atomicity, Consistency, Isolation, Durability",
                "Access, Control, Integrity, Data",
                "Authentication, Consistency, Integrity, Durability",
                "Atomicity, Control, Isolation, Data"
            ),
            0,
            "Databases"
        ));
        
        questions.add(new Question(
            "Which SQL command is used to modify existing data?",
            Arrays.asList("UPDATE", "MODIFY", "CHANGE", "ALTER"),
            0,
            "Databases"
        ));
        
        questions.add(new Question(
            "What is a primary key in a database?",
            Arrays.asList("A foreign key reference", "A unique identifier for each row", "An index on a column", "A constraint on data type"),
            1,
            "Databases"
        ));
        
        questions.add(new Question(
            "Which SQL clause is used to filter results?",
            Arrays.asList("SELECT", "WHERE", "FROM", "GROUP BY"),
            1,
            "Databases"
        ));
        
        questions.add(new Question(
            "What is normalization in database design?",
            Arrays.asList("Adding more data", "Organizing data to reduce redundancy", "Creating backups", "Indexing tables"),
            1,
            "Databases"
        ));
        
        // Web Development Questions
        questions.add(new Question(
            "What does HTML stand for?",
            Arrays.asList(
                "HyperText Markup Language",
                "High Tech Modern Language",
                "Home Tool Markup Language",
                "Hyperlink and Text Markup Language"
            ),
            0,
            "Web Development"
        ));
        
        questions.add(new Question(
            "Which CSS property is used to change the text color?",
            Arrays.asList("text-color", "color", "font-color", "foreground-color"),
            1,
            "Web Development"
        ));
        
        questions.add(new Question(
            "What is the purpose of JavaScript in web development?",
            Arrays.asList("Styling pages", "Adding interactivity", "Structuring content", "Storing data"),
            1,
            "Web Development"
        ));
        
        questions.add(new Question(
            "Which HTTP method is used to send data to a server?",
            Arrays.asList("GET", "POST", "PUT", "DELETE"),
            1,
            "Web Development"
        ));
        
        questions.add(new Question(
            "What is a responsive web design?",
            Arrays.asList("Fast loading websites", "Websites that work on all devices", "Secure websites", "Interactive websites"),
            1,
            "Web Development"
        ));
        
        // Computer Science Questions
        questions.add(new Question(
            "What is the time complexity of accessing an element in an array?",
            Arrays.asList("O(1)", "O(log n)", "O(n)", "O(n²)"),
            0,
            "Computer Science"
        ));
        
        questions.add(new Question(
            "Which programming paradigm focuses on objects?",
            Arrays.asList("Procedural", "Object-Oriented", "Functional", "Logical"),
            1,
            "Computer Science"
        ));
        
        questions.add(new Question(
            "What is recursion in programming?",
            Arrays.asList("A loop structure", "A function calling itself", "A data structure", "An algorithm"),
            1,
            "Computer Science"
        ));
        
        questions.add(new Question(
            "What is the purpose of an operating system?",
            Arrays.asList("To run applications", "To manage hardware and software resources", "To connect to the internet", "To store data"),
            1,
            "Computer Science"
        ));
        
        questions.add(new Question(
            "What is a compiler?",
            Arrays.asList("A program that translates high-level code to machine code", "A text editor", "A debugger", "A database"),
            0,
            "Computer Science"
        ));
        
        // Shuffle questions
        Collections.shuffle(questions);
    }
    
    private void createUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create different screens
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");
        mainPanel.add(createQuizPanel(), "QUIZ");
        mainPanel.add(createResultsPanel(), "RESULTS");
        mainPanel.add(createProfilePanel(), "PROFILE");
        mainPanel.add(createLeaderboardPanel(), "LEADERBOARD");
        
        add(mainPanel);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Title
        JLabel titleLabel = new JLabel("Welcome to Java Quiz App", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(MAIN_FONT);
        
        loginUsernameField = new JTextField(20);
        loginUsernameField.setFont(MAIN_FONT);
        
        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(MAIN_FONT);
        
        loginPasswordField = new JPasswordField(20);
        loginPasswordField.setFont(MAIN_FONT);
        
        // Buttons
        JButton loginButton = new JButton("Sign In");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setBackground(PRIMARY);
        loginButton.setForeground(TEXT_LIGHT);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.addActionListener(_ -> performLogin());
        
        JButton registerButton = new JButton("Create Account");
        registerButton.setFont(MAIN_FONT);
        registerButton.setBackground(SUCCESS);
        registerButton.setForeground(TEXT_LIGHT);
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.addActionListener(_ -> cardLayout.show(mainPanel, "REGISTER"));
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(loginUsernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(loginPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(registerButton, gbc);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Title
        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        
        // Form fields
        registerUsernameField = new JTextField(15);
        registerEmailField = new JTextField(15);
        registerPasswordField = new JPasswordField(15);
        registerConfirmPasswordField = new JPasswordField(15);
        registerFirstNameField = new JTextField(15);
        registerLastNameField = new JTextField(15);
        
        // Add form rows
        int row = 0;
        addFormRow(formPanel, "Username:", registerUsernameField, gbc, row++);
        addFormRow(formPanel, "Email:", registerEmailField, gbc, row++);
        addFormRow(formPanel, "Password:", registerPasswordField, gbc, row++);
        addFormRow(formPanel, "Confirm Password:", registerConfirmPasswordField, gbc, row++);
        addFormRow(formPanel, "First Name:", registerFirstNameField, gbc, row++);
        addFormRow(formPanel, "Last Name:", registerLastNameField, gbc, row++);
        
        // Buttons
        JButton registerButton = new JButton("Create Account");
        registerButton.setFont(BUTTON_FONT);
        registerButton.setBackground(SUCCESS);
        registerButton.setForeground(TEXT_LIGHT);
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.addActionListener(_ -> performRegistration());
        
        JButton backButton = new JButton("Back to Login");
        backButton.setFont(MAIN_FONT);
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(TEXT_LIGHT);
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "LOGIN"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addFormRow(JPanel panel, String labelText, JComponent field, 
                           GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(MAIN_FONT);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(label, gbc);
        
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(field, gbc);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_BG, getWidth(), getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);

        // Sidebar navigation
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(180, 0));

        JLabel logo = new JLabel("\uD83D\uDCDD Quiz App", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(TEXT_LIGHT);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        JButton dashboardBtn = createSidebarButton("\uD83C\uDFE0 Dashboard");
        JButton profileBtn = createSidebarButton("\uD83D\uDC64 Profile");
        JButton leaderboardBtn = createSidebarButton("\uD83C\uDFC6 Leaderboard");
        JButton logoutBtn = createSidebarButton("\uD83D\uDEAA Logout");

        dashboardBtn.setEnabled(false); // Already on dashboard
        profileBtn.addActionListener(_ -> showProfilePanel());
        leaderboardBtn.addActionListener(_ -> showLeaderboardPanel());
        logoutBtn.addActionListener(_ -> logout());

        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(profileBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(leaderboardBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(30));

        // Main content area
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(PRIMARY_BG);
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // User stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        UserStats stats = userStats.getOrDefault(currentUser, new UserStats());
        statsPanel.add(createStatCard("\uD83D\uDCCA Quizzes Taken", String.valueOf(stats.totalQuizzes), PRIMARY));
        statsPanel.add(createStatCard("\uD83C\uDFC6 Best Score", String.format("%.1f%%", stats.bestScore), SUCCESS));
        statsPanel.add(createStatCard("\uD83D\uDCDD Questions", String.valueOf(stats.totalQuestions), WARNING));

        // Category selection
        JPanel categoriesPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        categoriesPanel.setOpaque(false);
        String[][] categories = {
            {"Java Programming", "\u2615", "#3498db"},
            {"Data Structures", "\uD83D\uDD17", "#2ecc71"},
            {"Algorithms", "\u26A1", "#9b59b6"},
            {"Databases", "\uD83D\uDCBE", "#e67e22"},
            {"Web Development", "\uD83C\uDF10", "#e74c3c"},
            {"Computer Science", "\uD83D\uDCBB", "#34495e"}
        };
        for (String[] cat : categories) {
            categoriesPanel.add(createCategoryCard(cat[0], cat[1], cat[2]));
        }

        JLabel welcomeLabel = new JLabel("Welcome, " + (currentUserFullName != null ? currentUserFullName : currentUser) + "!", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_DARK);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        content.add(welcomeLabel, BorderLayout.NORTH);
        content.add(statsPanel, BorderLayout.CENTER);
        content.add(categoriesPanel, BorderLayout.SOUTH);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(160, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(HOVER_SIDEBAR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_BG);
            }
        });
        // Add subtle shadow
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(0,0,0,20)),
            btn.getBorder()
        ));
        return btn;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(CARD_VALUE_FONT);
        valueLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCategoryCard(String title, String icon, String colorHex) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.decode(colorHex), getWidth(), getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(Color.decode(colorHex));
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startQuiz(title);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.decode(colorHex), 2),
                    BorderFactory.createEmptyBorder(19, 19, 19, 19)
                ));
                card.setBackground(HOVER_BG);
                card.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CARD_BORDER, 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
                card.setBackground(new Color(255,255,255,0));
                card.repaint();
            }
        });
        // Add subtle shadow
        card.setBorder(BorderFactory.createCompoundBorder(
            card.getBorder(),
            BorderFactory.createMatteBorder(0, 0, 8, 0, new Color(0,0,0,15))
        ));
        return card;
    }

    // Placeholder for Profile and Leaderboard panels
    private void showProfilePanel() {
        updateProfileStats();
        cardLayout.show(mainPanel, "PROFILE");
    }
    
    private void showLeaderboardPanel() {
        updateLeaderboard();
        cardLayout.show(mainPanel, "LEADERBOARD");
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Sidebar navigation (same as dashboard)
        JPanel sidebar = createSidebar();
        sidebar.getComponent(1).setEnabled(false); // Disable profile button

        // Main content
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(PRIMARY_BG);
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Profile header
        JLabel profileTitle = new JLabel("👤 User Profile", SwingConstants.LEFT);
        profileTitle.setFont(TITLE_FONT);
        profileTitle.setForeground(TEXT_DARK);

        // Profile content
        JPanel profileContent = new JPanel(new BorderLayout(20, 20));
        profileContent.setOpaque(false);

        // Stats section
        JPanel statsSection = new JPanel(new GridLayout(1, 4, 15, 0));
        statsSection.setOpaque(false);
        UserStats stats = userStats.getOrDefault(currentUser, new UserStats());
        
        statsSection.add(createStatCard("📊 Total Quizzes", String.valueOf(stats.totalQuizzes), PRIMARY));
        statsSection.add(createStatCard("🎯 Average Score", String.format("%.1f%%", stats.averageScore), SUCCESS));
        statsSection.add(createStatCard("🏆 Best Score", String.format("%.1f%%", stats.bestScore), SUCCESS));
        statsSection.add(createStatCard("📝 Questions", String.valueOf(stats.totalQuestions), WARNING));

        // Profile actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actionsPanel.setOpaque(false);
        
        JButton changePasswordBtn = new JButton("🔐 Change Password");
        changePasswordBtn.setFont(MAIN_FONT);
        changePasswordBtn.setBackground(PRIMARY);
        changePasswordBtn.setForeground(TEXT_LIGHT);
        changePasswordBtn.addActionListener(_ -> showChangePasswordDialog());
        
        JButton exportDataBtn = new JButton("📤 Export Data");
        exportDataBtn.setFont(MAIN_FONT);
        exportDataBtn.setBackground(SUCCESS);
        exportDataBtn.setForeground(TEXT_LIGHT);
        exportDataBtn.addActionListener(_ -> exportUserData());
        
        actionsPanel.add(changePasswordBtn);
        actionsPanel.add(exportDataBtn);

        // Quiz history
        JLabel historyLabel = new JLabel("📚 Quiz History");
        historyLabel.setFont(TITLE_FONT);
        historyLabel.setForeground(TEXT_DARK);
        
        profileStatsArea = new JTextArea();
        profileStatsArea.setFont(SMALL_FONT);
        profileStatsArea.setEditable(false);
        profileStatsArea.setBackground(new Color(248, 249, 250));
        
        JScrollPane historyScroll = new JScrollPane(profileStatsArea);
        historyScroll.setPreferredSize(new Dimension(600, 200));
        historyScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        profileContent.add(statsSection, BorderLayout.NORTH);
        profileContent.add(actionsPanel, BorderLayout.CENTER);
        profileContent.add(historyLabel, BorderLayout.SOUTH);
        profileContent.add(historyScroll, BorderLayout.SOUTH);

        content.add(profileTitle, BorderLayout.NORTH);
        content.add(profileContent, BorderLayout.CENTER);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Sidebar navigation
        JPanel sidebar = createSidebar();
        sidebar.getComponent(3).setEnabled(false); // Disable leaderboard button

        // Main content
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(PRIMARY_BG);
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Leaderboard header
        JLabel leaderboardTitle = new JLabel("🏆 Leaderboard", SwingConstants.LEFT);
        leaderboardTitle.setFont(TITLE_FONT);
        leaderboardTitle.setForeground(TEXT_DARK);

        // Leaderboard content
        leaderboardArea = new JTextArea();
        leaderboardArea.setFont(SMALL_FONT);
        leaderboardArea.setEditable(false);
        leaderboardArea.setBackground(new Color(248, 249, 250));
        
        JScrollPane leaderboardScroll = new JScrollPane(leaderboardArea);
        leaderboardScroll.setPreferredSize(new Dimension(700, 400));
        leaderboardScroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        content.add(leaderboardTitle, BorderLayout.NORTH);
        content.add(leaderboardScroll, BorderLayout.CENTER);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(180, 0));

        JLabel logo = new JLabel("\uD83D\uDCDD Quiz App", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(TEXT_LIGHT);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        JButton dashboardBtn = createSidebarButton("\uD83C\uDFE0 Dashboard");
        JButton profileBtn = createSidebarButton("\uD83D\uDC64 Profile");
        JButton leaderboardBtn = createSidebarButton("\uD83C\uDFC6 Leaderboard");
        JButton logoutBtn = createSidebarButton("\uD83D\uDEAA Logout");

        dashboardBtn.addActionListener(_ -> cardLayout.show(mainPanel, "DASHBOARD"));
        profileBtn.addActionListener(_ -> showProfilePanel());
        leaderboardBtn.addActionListener(_ -> showLeaderboardPanel());
        logoutBtn.addActionListener(_ -> logout());

        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(profileBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(leaderboardBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(30));
        
        return sidebar;
    }
    
    private void logout() {
        currentUser = null;
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    private void startQuiz(String category) {
        selectedCategory = category;
        
        // Show difficulty selection dialog
        String[] difficulties = {"Easy", "Medium", "Hard"};
        selectedDifficulty = (String) JOptionPane.showInputDialog(
            this,
            "Choose difficulty level for " + category + ":",
            "Select Difficulty",
            JOptionPane.QUESTION_MESSAGE,
            null,
            difficulties,
            difficulties[0]
        );
        
        if (selectedDifficulty == null) {
            return; // User cancelled
        }
        
        // Filter questions by category and difficulty
        filterQuestionsByCategoryAndDifficulty(category, selectedDifficulty);
        
        if (filteredQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No questions available for " + category + " (" + selectedDifficulty + "). Using all questions.",
                "No Questions", 
                JOptionPane.INFORMATION_MESSAGE);
            filteredQuestions = new ArrayList<>(questions);
        }
        
        // Reset quiz state
        currentQuestionIndex = 0;
        score = 0;
        userAnswers.clear();
        timeRemaining = 60; // Timer for the whole quiz
        
        // Display first question and start timer
        displayQuestion();
        startTimer();
        cardLayout.show(mainPanel, "QUIZ");
    }
    
    private void filterQuestionsByCategoryAndDifficulty(String category, String difficulty) {
        filteredQuestions.clear();
        for (Question q : questions) {
            if (q.getCategory().equals(category)) {
                // For now, we'll use all questions from the category
                // In a real app, you'd have difficulty levels per question
                filteredQuestions.add(q);
            }
        }
        // Limit to 10 questions
        if (filteredQuestions.size() > 10) {
            Collections.shuffle(filteredQuestions);
            filteredQuestions = filteredQuestions.subList(0, 10);
        }
    }
    
    private void displayQuestion() {
        if (currentQuestionIndex >= filteredQuestions.size()) {
            submitQuiz();
            return;
        }
        
        Question question = filteredQuestions.get(currentQuestionIndex);
        questionLabel.setText((currentQuestionIndex + 1) + ". " + question.getQuestionText());
        
        // Clear all radio buttons and reset their state
        optionsGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(question.getOptions().get(i));
            optionButtons[i].setBackground(CARD_BG);
            optionButtons[i].setEnabled(true);
        }
        
        scoreLabel.setText("Score: " + score + "/" + filteredQuestions.size());
        
        // Hide feedback panel for new question
        feedbackPanel.setVisible(false);
        
        // Do not reset timer per question here
        timerLabel.setText("Time: " + timeRemaining + "s");
    }
    
    private void nextQuestion() {
        // Check if user has selected an answer
        int selectedAnswer = -1;
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selectedAnswer = i;
                break;
            }
        }
        
        if (selectedAnswer == -1) {
            JOptionPane.showMessageDialog(this, "Please select an answer before proceeding.", "No Answer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Disable all buttons during feedback
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);
        }
        
        userAnswers.add(selectedAnswer);
        
        if (currentQuestionIndex < filteredQuestions.size()) {
            Question question = filteredQuestions.get(currentQuestionIndex);
            boolean isCorrect = selectedAnswer == question.getCorrectAnswerIndex();
            
            if (isCorrect) {
                score++;
            }
            
            // Show feedback
            showQuestionFeedback(question, selectedAnswer, isCorrect);
            
            // Wait 3 seconds before next question to give user time to read feedback
            javax.swing.Timer feedbackTimer = new javax.swing.Timer(3000, e -> {
                currentQuestionIndex++;
                displayQuestion();
                ((javax.swing.Timer)e.getSource()).stop();
            });
            feedbackTimer.setRepeats(false);
            feedbackTimer.start();
        } else {
            // This should not happen as displayQuestion checks for this
            submitQuiz();
        }
    }
    
    private void showQuestionFeedback(Question question, int selectedAnswer, boolean isCorrect) {
        String feedback = isCorrect ? "✅ Correct!" : "❌ Incorrect!";
        String explanation = "<html>" + feedback + "<br>Your answer: <b>" + question.getOptions().get(selectedAnswer) +
                            "</b><br>Correct answer: <b>" + question.getOptions().get(question.getCorrectAnswerIndex()) + "</b></html>";

        explanationLabel.setText(explanation);
        feedbackPanel.setBackground(isCorrect ? new Color(144, 238, 144, 60) : new Color(255, 182, 193, 60));
        feedbackPanel.setVisible(true);
        
        // Visual feedback on option buttons using border highlight
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setBackground(CARD_BG); // Reset all
            optionButtons[i].setForeground(TEXT_DARK);
            optionButtons[i].setBorder(null); // Remove any previous border
        }
        if (isCorrect) {
            optionButtons[selectedAnswer].setBorder(new LineBorder(new Color(46, 204, 113), 3)); // green border
        } else {
            optionButtons[selectedAnswer].setBorder(new LineBorder(new Color(231, 76, 60), 3)); // red border
            optionButtons[question.getCorrectAnswerIndex()].setBorder(new LineBorder(new Color(46, 204, 113), 3)); // green border
        }
        // Force repaint to ensure changes are visible immediately
        for (int i = 0; i < 4; i++) {
            optionButtons[i].repaint();
        }
    }
    
    private void submitQuiz() {
        // Ask for confirmation before submitting
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to submit the quiz?\nYou have answered " + userAnswers.size() + " out of " + filteredQuestions.size() + " questions.",
            "Confirm Quiz Submission",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }
        
        if (quizTimer != null) {
            quizTimer.stop();
        }
        
        // Save final answer if not already saved
        if (userAnswers.size() <= currentQuestionIndex && currentQuestionIndex < filteredQuestions.size()) {
            int selectedAnswer = -1;
            for (int i = 0; i < 4; i++) {
                if (optionButtons[i].isSelected()) {
                    selectedAnswer = i;
                    break;
                }
            }
            if (selectedAnswer != -1) {
                userAnswers.add(selectedAnswer);
                Question question = filteredQuestions.get(currentQuestionIndex);
                if (selectedAnswer == question.getCorrectAnswerIndex()) {
                    score++;
                }
            }
        }
        
        // Calculate percentage
        double percentage = (double) score / filteredQuestions.size() * 100;
        
        // Update user stats
        if (currentUser != null) {
            UserStats stats = userStats.get(currentUser);
            stats.totalQuizzes++;
            stats.totalQuestions += filteredQuestions.size();
            stats.averageScore = ((stats.averageScore * (stats.totalQuizzes - 1)) + percentage) / stats.totalQuizzes;
            if (percentage > stats.bestScore) {
                stats.bestScore = percentage;
            }
            
            // Add to quiz history
            quizHistory.add(new QuizHistoryEntry(
                selectedCategory, selectedDifficulty, score, filteredQuestions.size(), percentage
            ));
            
            saveUsers();
        }
        
        // Display results
        displayResults();
        cardLayout.show(mainPanel, "RESULTS");
    }
    
    private void displayResults() {
        StringBuilder results = new StringBuilder();
        results.append("QUIZ RESULTS\n");
        results.append("============\n\n");
        results.append("Category: ").append(selectedCategory).append("\n");
        results.append("Difficulty: ").append(selectedDifficulty).append("\n");
        results.append("Final Score: ").append(score).append("/").append(filteredQuestions.size()).append("\n");
        results.append("Percentage: ").append(String.format("%.1f%%", (double) score / filteredQuestions.size() * 100)).append("\n\n");
        
        results.append("QUESTION REVIEW\n");
        results.append("===============\n\n");
        
        for (int i = 0; i < filteredQuestions.size(); i++) {
            Question question = filteredQuestions.get(i);
            results.append("Question ").append(i + 1).append(":\n");
            results.append(question.getQuestionText()).append("\n");
            
            if (i < userAnswers.size()) {
                int userAnswer = userAnswers.get(i);
                results.append("Your Answer: ").append(question.getOptions().get(userAnswer)).append("\n");
                results.append("Correct Answer: ").append(question.getOptions().get(question.getCorrectAnswerIndex())).append("\n");
                
                if (userAnswer == question.getCorrectAnswerIndex()) {
                    results.append("Status: ✅ Correct\n");
                } else {
                    results.append("Status: ❌ Incorrect\n");
                }
            } else {
                results.append("Your Answer: Not answered\n");
                results.append("Correct Answer: ").append(question.getOptions().get(question.getCorrectAnswerIndex())).append("\n");
                results.append("Status: ❌ Not answered\n");
            }
            results.append("\n");
        }
        
        resultsArea.setText(results.toString());
    }
    
    private void startTimer() {
        if (quizTimer != null) quizTimer.stop();
        quizTimer = new javax.swing.Timer(1000, _ -> {
            timeRemaining--;
            timerLabel.setText("Time: " + timeRemaining + "s");
            if (timeRemaining <= 0) {
                quizTimer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Submitting your quiz.");
                submitQuiz();
            }
        });
        quizTimer.start();
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password; // Fallback to plain text if hashing fails
        }
    }
    
    // Inner classes
    private static class Question {
        private String questionText;
        private java.util.List<String> options;
        private int correctAnswerIndex;
        private String category;
        
        public Question(String questionText, java.util.List<String> options, int correctAnswerIndex, String category) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
            this.category = category;
        }
        
        public String getQuestionText() { return questionText; }
        public java.util.List<String> getOptions() { return options; }
        public int getCorrectAnswerIndex() { return correctAnswerIndex; }
        public String getCategory() { return category; }
    }
    
    private static class UserStats {
        public int totalQuizzes = 0;
        public double averageScore = 0.0;
        public double bestScore = 0.0;
        public int totalQuestions = 0;
        
        public UserStats() {}
        
        public UserStats(int totalQuizzes, double averageScore, double bestScore, int totalQuestions) {
            this.totalQuizzes = totalQuizzes;
            this.averageScore = averageScore;
            this.bestScore = bestScore;
            this.totalQuestions = totalQuestions;
        }
    }
    
    // Add new inner class for quiz history
    private static class QuizHistoryEntry {
        String category;
        String difficulty;
        int score;
        int totalQuestions;
        double percentage;
        
        public QuizHistoryEntry(String category, String difficulty, int score, int totalQuestions, double percentage) {
            this.category = category;
            this.difficulty = difficulty;
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.percentage = percentage;
        }
    }
    
    // Profile actions
    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();
        
        panel.add(new JLabel("Current Password:"));
        panel.add(oldPass);
        panel.add(new JLabel("New Password:"));
        panel.add(newPass);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPass);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPass.getPassword());
            String newPassword = new String(newPass.getPassword());
            String confirmPassword = new String(confirmPass.getPassword());
            
            if (newPassword.equals(confirmPassword) && 
                users.get(currentUser).equals(hashPassword(oldPassword))) {
                users.put(currentUser, hashPassword(newPassword));
                saveUsers();
                JOptionPane.showMessageDialog(this, "Password changed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid current password or passwords don't match!");
            }
        }
    }
    
    private void exportUserData() {
        try {
            String filename = currentUser + "_quiz_data.txt";
            PrintWriter writer = new PrintWriter(filename);
            UserStats stats = userStats.get(currentUser);
            
            writer.println("QUIZ DATA EXPORT");
            writer.println("================");
            writer.println("Username: " + currentUser);
            writer.println("Total Quizzes: " + stats.totalQuizzes);
            writer.println("Average Score: " + String.format("%.1f%%", stats.averageScore));
            writer.println("Best Score: " + String.format("%.1f%%", stats.bestScore));
            writer.println("Total Questions: " + stats.totalQuestions);
            writer.println("\nQuiz History:");
            
            for (QuizHistoryEntry entry : quizHistory) {
                writer.println(String.format("- %s (%s): %d/%d (%.1f%%)", 
                    entry.category, entry.difficulty, entry.score, 
                    entry.totalQuestions, entry.percentage));
            }
            
            writer.close();
            JOptionPane.showMessageDialog(this, "Data exported to " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage());
        }
    }
    
    // Profile and Leaderboard update methods
    private void updateProfileStats() {
        UserStats stats = userStats.getOrDefault(currentUser, new UserStats());
        StringBuilder profileText = new StringBuilder();
        
        profileText.append("USER STATISTICS\n");
        profileText.append("===============\n\n");
        profileText.append("Username: ").append(currentUser).append("\n");
        profileText.append("Total Quizzes: ").append(stats.totalQuizzes).append("\n");
        profileText.append("Average Score: ").append(String.format("%.1f%%", stats.averageScore)).append("\n");
        profileText.append("Best Score: ").append(String.format("%.1f%%", stats.bestScore)).append("\n");
        profileText.append("Total Questions: ").append(stats.totalQuestions).append("\n\n");
        
        profileText.append("RECENT QUIZ HISTORY\n");
        profileText.append("===================\n\n");
        
        if (quizHistory.isEmpty()) {
            profileText.append("No quiz history available.\n");
        } else {
            for (int i = quizHistory.size() - 1; i >= Math.max(0, quizHistory.size() - 10); i--) {
                QuizHistoryEntry entry = quizHistory.get(i);
                profileText.append(String.format("%d. %s (%s) - %d/%d (%.1f%%)\n", 
                    quizHistory.size() - i, entry.category, entry.difficulty, 
                    entry.score, entry.totalQuestions, entry.percentage));
            }
        }
        
        profileStatsArea.setText(profileText.toString());
    }
    
    private void updateLeaderboard() {
        StringBuilder leaderboardText = new StringBuilder();
        leaderboardText.append("🏆 TOP PERFORMERS\n");
        leaderboardText.append("================\n\n");
        
        // Sort users by best score
        java.util.List<Map.Entry<String, UserStats>> sortedUsers = new ArrayList<>(userStats.entrySet());
        sortedUsers.sort((a, b) -> Double.compare(b.getValue().bestScore, a.getValue().bestScore));
        
        for (int i = 0; i < Math.min(10, sortedUsers.size()); i++) {
            Map.Entry<String, UserStats> entry = sortedUsers.get(i);
            String rank = (i == 0) ? "🥇" : (i == 1) ? "🥈" : (i == 2) ? "🥉" : String.valueOf(i + 1);
            leaderboardText.append(String.format("%s %s\n", rank, entry.getKey()));
            leaderboardText.append(String.format("   Best Score: %.1f%% | Quizzes: %d | Avg: %.1f%%\n\n", 
                entry.getValue().bestScore, entry.getValue().totalQuizzes, entry.getValue().averageScore));
        }
        
        leaderboardArea.setText(leaderboardText.toString());
    }
    
    private void showLoginScreen() {
        cardLayout.show(mainPanel, "LOGIN");
        setVisible(true);
    }
    
    private void performLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password");
            return;
        }
        
        String hashedPassword = users.get(username);
        if (hashedPassword != null && hashedPassword.equals(hashPassword(password))) {
            String fullName = userNames.get(username);
            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }
            currentUser = username;
            currentUserFullName = fullName;
            cardLayout.show(mainPanel, "DASHBOARD");
            loginUsernameField.setText("");
            loginPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        }
    }
    
    private void performRegistration() {
        String username = registerUsernameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(registerConfirmPasswordField.getPassword());
        String firstName = registerFirstNameField.getText().trim();
        String lastName = registerLastNameField.getText().trim();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }
        
        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists");
            return;
        }
        
        users.put(username, hashPassword(password));
        userStats.put(username, new UserStats());
        userNames.put(username, firstName + " " + lastName);
        saveUsers();
        
        JOptionPane.showMessageDialog(this, "Account created successfully!");
        cardLayout.show(mainPanel, "LOGIN");
        
        // Clear fields
        registerUsernameField.setText("");
        registerEmailField.setText("");
        registerPasswordField.setText("");
        registerConfirmPasswordField.setText("");
        registerFirstNameField.setText("");
        registerLastNameField.setText("");
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Quiz Results", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_LIGHT);
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(MAIN_FONT);
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(TEXT_LIGHT);
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "DASHBOARD"));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        // Results area
        resultsArea = new JTextArea();
        resultsArea.setFont(SMALL_FONT);
        resultsArea.setEditable(false);
        resultsArea.setBackground(new Color(248, 249, 250));
        
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Header with enhanced info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel categoryLabel = new JLabel("Category: " + (selectedCategory != null ? selectedCategory : "All"));
        categoryLabel.setFont(MAIN_FONT);
        categoryLabel.setForeground(TEXT_LIGHT);
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(TITLE_FONT);
        scoreLabel.setForeground(TEXT_LIGHT);
        
        timerLabel = new JLabel("Time: 60s");
        timerLabel.setFont(MAIN_FONT);
        timerLabel.setForeground(SUCCESS);
        
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(MAIN_FONT);
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(TEXT_LIGHT);
        backButton.addActionListener(_ -> cardLayout.show(mainPanel, "DASHBOARD"));
        
        headerPanel.add(categoryLabel, BorderLayout.WEST);
        headerPanel.add(scoreLabel, BorderLayout.CENTER);
        headerPanel.add(timerLabel, BorderLayout.EAST);
        headerPanel.add(backButton, BorderLayout.SOUTH);
        
        // Question panel with feedback
        JPanel questionPanel = new JPanel(new BorderLayout(20, 20));
        questionPanel.setBackground(CARD_BG);
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        questionLabel = new JLabel("Question will appear here");
        questionLabel.setFont(TITLE_FONT);
        questionLabel.setForeground(TEXT_DARK);
        
        // Options panel
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBackground(CARD_BG);
        
        optionButtons = new JRadioButton[4];
        optionsGroup = new ButtonGroup(); // Initialize the ButtonGroup
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton("Option " + (i + 1));
            optionButtons[i].setFont(MAIN_FONT);
            optionButtons[i].setBackground(CARD_BG);
            optionsGroup.add(optionButtons[i]); // Add to ButtonGroup
            optionsPanel.add(optionButtons[i]);
        }
        
        // Feedback panel for explanations
        feedbackPanel = new JPanel(new BorderLayout());
        feedbackPanel.setBackground(new Color(248, 249, 250));
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        feedbackPanel.setVisible(false);
        
        explanationLabel = new JLabel("");
        explanationLabel.setFont(SMALL_FONT);
        explanationLabel.setForeground(TEXT_DARK);
        feedbackPanel.add(explanationLabel, BorderLayout.CENTER);
        
        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        navPanel.setBackground(CARD_BG);
        
        JButton nextButton = new JButton("Next Question →");
        nextButton.setFont(BUTTON_FONT);
        nextButton.setBackground(PRIMARY);
        nextButton.setForeground(TEXT_LIGHT);
        nextButton.setPreferredSize(new Dimension(150, 40));
        nextButton.addActionListener(_ -> nextQuestion());
        
        JButton submitButton = new JButton("Submit Quiz");
        submitButton.setFont(BUTTON_FONT);
        submitButton.setBackground(SUCCESS);
        submitButton.setForeground(TEXT_LIGHT);
        submitButton.setPreferredSize(new Dimension(150, 40));
        submitButton.addActionListener(_ -> submitQuiz());
        
        navPanel.add(nextButton);
        navPanel.add(submitButton);
        
        questionPanel.add(questionLabel, BorderLayout.NORTH);
        questionPanel.add(optionsPanel, BorderLayout.CENTER);
        questionPanel.add(feedbackPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(questionPanel, BorderLayout.CENTER);
        panel.add(navPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new SimpleQuizApp().setVisible(true);
        });
    }
} 