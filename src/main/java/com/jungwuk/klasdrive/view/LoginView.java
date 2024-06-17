package com.jungwuk.klasdrive.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 550;

    @Getter
    private JTextField usernameField;
    @Getter
    private JPasswordField passwordField;
    @Getter
    private JButton loginButton;
    @Getter
    private JLabel waringLabel;
    private JLabel titleLabel;

    public LoginView() {
        setTitle("KLAS 로그인");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        usernameField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passwordField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        passwordField.setFont(new Font("Monospaced", Font.PLAIN, 15));

        waringLabel = new JLabel("");
        waringLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = new JButton("로그인");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleLabel = new JLabel("KLAS Drive");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.TOP);
        titleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        Font titleFont = titleLabel.getFont().deriveFont(Font.BOLD, 30);
        titleLabel.setFont(titleFont);
    }

    private void layoutComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 제목
        panel.add(titleLabel);
        panel.add(Box.createVerticalGlue());

        // 학번 영역
        JLabel usernameLabel = new JLabel("학번:");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(usernameLabel);

        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(usernameField);

        panel.add(Box.createVerticalStrut(10));

        // 비밀번호 영역
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordLabel);

        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordField);

        panel.add(Box.createVerticalGlue());

        // 로그인 버튼
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(waringLabel);

        add(panel, BorderLayout.CENTER);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }
}