package com.jungwuk.klasuploader.controller;

import com.jungwuk.klasuploader.dialog.SimpleInformationDialog;
import com.jungwuk.klasuploader.utils.KlasDownloadManager;
import com.jungwuk.klasuploader.view.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewController {
    private final MainView view;
    private final KlasDownloadManager downloadManager = new KlasDownloadManager();
    private Map<String, List<String>> fileChunks = new HashMap<>();

    MainViewController(MainView view) {
        this.view = view;
        loadFileNames();
        initComponents();
    }

    private void initComponents() {
        initUploadButton();
        initDownloadPathField();
        initFileListView();
    }

    private void initFileListView() {
        var fileList = view.getFileList();
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addMouseListener(new MouseAdapter() {

            private static void setSelected(MouseEvent e) {
                JList list = (JList) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                if (list.getSelectedIndex() != index) {
                    list.setSelectedIndex(index);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    setSelected(e);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String selectedFile = fileList.getSelectedValue();

                if (SwingUtilities.isRightMouseButton(e)) {
                    showRemoveItemDialog(selectedFile);
                } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    downloadItem(selectedFile);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String selectedFile = fileList.getSelectedValue();

                if (SwingUtilities.isRightMouseButton(e)) {
                    showRemoveItemDialog(selectedFile);
                }
            }

            private void downloadItem(String selectedFile) {
                File outputDir = new File(view.getDownloadPathField().getText());
                if (!outputDir.exists()) {
                    if(!outputDir.mkdirs()) {
                        JOptionPane.showMessageDialog(view, "파일 저장할 디렉토리 생성 실패", "오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else if (!outputDir.isDirectory()) {
                    JOptionPane.showMessageDialog(view, "입력된 경로는 디렉토리가 아닙니다!", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<String> responses = fileChunks.get(selectedFile);
                if (responses != null) {
                    try {
                        combineDownloadedFiles(selectedFile, responses);

                        if (Desktop.isDesktopSupported()) {
                            int response = JOptionPane.showConfirmDialog(view, "다운로드가 완료되었어요! 이 파일을 열까요?", selectedFile + "다운로드", JOptionPane.OK_CANCEL_OPTION);
                            if (response == JOptionPane.OK_OPTION) {
                                Desktop.getDesktop().open(new File(view.getDownloadPathField().getText(), selectedFile));
                            }
                        } else {
                            JOptionPane.showMessageDialog(view, "다운로드가 완료되었어요!", selectedFile + "다운로드", JOptionPane.PLAIN_MESSAGE);
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "오류가 발생하였습니다: " + ex.getMessage());
                    }
                }
            }

            private void showRemoveItemDialog(String selectedFile) {
                int answer = JOptionPane.showConfirmDialog(view, selectedFile + "을(를) 목록에서 제거할까요?", "파일 제거", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    fileChunks.remove(selectedFile);
                    downloadManager.saveFileDatabase(fileChunks);

                    var listModel = fileList.getModel();
                    int size = listModel.getSize();
                    for (int i = 0; i < size; i++) {
                        var element = (String) listModel.getElementAt(i);
                        if (element.equals(selectedFile)) {
                            view.getListModel().removeElement(element);
                        }
                    }
                }
            }
        });
    }

    private void initDownloadPathField() {
        view.getDownloadPathField().setText(System.getProperty("user.dir"));
    }

    private void initUploadButton() {
        var uploadBtn = view.getUploadButton();

        uploadBtn.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try {
                    List<String> responses = downloadManager.uploadFileInChunks(file, view);
                    fileChunks.put(file.getName(), responses);
                    view.getListModel().addElement(file.getName());
                    downloadManager.saveFileDatabase(fileChunks);
                    JOptionPane.showMessageDialog(null, "업로드를 완료했어요");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "오류가 발생하였어요: " + ex.getMessage());
                }
            }
        });
    }

    private void loadFileNames() {
        fileChunks = downloadManager.getFileDatabase();
        for (String fileName : fileChunks.keySet()) {
            view.getListModel().addElement(fileName);
        }
    }

    private void combineDownloadedFiles(String fileName, List<String> responses) throws IOException {
        SimpleInformationDialog loadingDialog = new SimpleInformationDialog(view, "다운로드 중...");

        new Thread(() -> {
            try (OutputStream outputStream = new FileOutputStream(new File(view.getDownloadPathField().getText(), fileName))) {
                int size = responses.size();
                for (int i = 0; i < size; i++) {
                    String response = responses.get(i);
                    loadingDialog.setContent("다운로드 중(" + i + " / " + size + ")");
                    downloadManager.downloadFile(response, outputStream);
                }

            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            loadingDialog.dispose();
        }).start();
        loadingDialog.setVisible(true);
    }
}
