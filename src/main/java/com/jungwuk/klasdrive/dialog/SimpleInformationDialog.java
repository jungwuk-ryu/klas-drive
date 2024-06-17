package com.jungwuk.klasdrive.dialog;

import javax.swing.*;
import java.awt.*;

public class SimpleInformationDialog extends JDialog {
    private JLabel centerLabel = new JLabel();

    public SimpleInformationDialog(Window owner, String title, String message) {
        super(owner, title, ModalityType.APPLICATION_MODAL);

        centerLabel = new JLabel(message, JLabel.CENTER);
        this.add(centerLabel);
        this.setSize(300, 100);
        this.setLocationRelativeTo(this);
    }

    public SimpleInformationDialog(Window owner, String title) {
        this(owner, title, title);
    }

    public void setContent(String text) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> setContent(text));
        } else {
            centerLabel.setText(text);
        }
    }
}
