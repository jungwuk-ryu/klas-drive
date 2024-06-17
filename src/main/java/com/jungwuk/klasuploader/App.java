package com.jungwuk.klasuploader;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.jungwuk.klasuploader.controller.LoginViewController;
import com.jungwuk.klasuploader.view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class App {
    public static void main(String[] args) throws IOException, FontFormatException {
        FlatLightLaf.setup(new FlatMacDarkLaf());

        InputStream is = App.class.getClassLoader().getResourceAsStream("fonts/SUITE-Regular.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        font = font.deriveFont(15f);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, font);
        }


        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);

            LoginViewController loginViewController = new LoginViewController(loginView);
        });
    }
}