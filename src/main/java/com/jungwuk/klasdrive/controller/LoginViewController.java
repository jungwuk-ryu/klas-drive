package com.jungwuk.klasdrive.controller;

import com.google.gson.Gson;
import com.jungwuk.klasdrive.ApiManager;
import com.jungwuk.klasdrive.view.LoginView;
import com.jungwuk.klasdrive.view.MainView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LoginViewController {
    private final LoginView view;
    private ApiManager apiManager = ApiManager.getInstance();

    public LoginViewController(LoginView view) {
        this.view = view;
        initComponents();
    }

    private void initComponents() {
        JButton loginButton = view.getLoginButton();
        loginButton.addActionListener(_ -> {
            loginButton.setEnabled(false);

            login();
        });
        view.getPasswordField().addActionListener(_ -> login());
    }

    private void login() {
        CompletableFuture.runAsync(() -> {
            String data = "";

            try {
                updateWarningMessage("로그인 중 ...");
                var result = apiManager.loginToKlas(view.getUsername(), view.getPassword());
                if (result.isSuccess()) {
                    view.dispose();
                    MainView mainView = new MainView();
                    mainView.setVisible(true);

                    MainViewController controller = new MainViewController(mainView);
                    return;
                }

                data = result.getData();
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }

            SwingUtilities.invokeLater(() -> view.getLoginButton().setEnabled(true));
            if (data != null) {
                System.out.println(data);

                String message = null;
                Gson gson = new Gson();
                try {
                    Map response = gson.fromJson(data, Map.class);
                    ArrayList fields = (ArrayList) response.get("fieldErrors");
                    Map errorData = (Map) fields.get(0);
                    message = (String) errorData.get("message");

                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }

                updateWarningMessage(Objects.requireNonNullElse(message, "로그인에 실패했어요"));
            } else {
                updateWarningMessage("로그인에 실패했어요");
            }
        });
    }

    private void updateWarningMessage(String message) {
        SwingUtilities.invokeLater(() -> view.getWaringLabel().setText(message));
    }
}
