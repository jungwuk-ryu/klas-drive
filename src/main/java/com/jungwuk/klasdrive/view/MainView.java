package com.jungwuk.klasdrive.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    @Getter
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    @Getter
    private JList<String> fileList;
    @Getter
    private JButton uploadButton;
    @Getter
    private JTextField downloadPathField;
    private JLabel titleLabel;

    public MainView() {
        setTitle("KLAS Drive");
        setSize(400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
    }


    private void initComponents() {
        uploadButton = new JButton("새 파일 업로드");

        downloadPathField = new JTextField();
        downloadPathField.setEditable(true);

        fileList = new JList<>(listModel);

        titleLabel = new JLabel("KLAS Drive");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void layoutComponents() {
        JPanel parentPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(uploadButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JScrollPane fileScrollPane = new JScrollPane(fileList);

        centerPanel.add(downloadPathField, BorderLayout.NORTH);
        centerPanel.add(fileScrollPane, BorderLayout.CENTER);

        parentPanel.add(topPanel, BorderLayout.NORTH);
        parentPanel.add(centerPanel, BorderLayout.CENTER);

        add(titleLabel, BorderLayout.NORTH);
        add(parentPanel, BorderLayout.CENTER);
    }
}
