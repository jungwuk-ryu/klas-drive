package com.jungwuk.klasuploader.view;

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

    public MainView() {
        setTitle("KLAS Drive");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
    }


    private void initComponents() {
        uploadButton = new JButton("새 파일 업로드");
        downloadPathField = new JTextField();
        fileList = new JList<>(listModel);
    }

    private void layoutComponents() {
        add(uploadButton, BorderLayout.NORTH);
        add(new JScrollPane(fileList), BorderLayout.CENTER);
        add(downloadPathField, BorderLayout.SOUTH);
    }
}
