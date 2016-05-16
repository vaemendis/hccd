package net.vaemendis.hccd;


import javax.swing.*;

public class Hccd {

    private static MainForm frame;


    public static void main(String[] args) {
        try {
            frame = new MainForm(new FileWatcher());
            frame.init();
            SwingUtilities.invokeLater(() -> frame.setVisible(true));
        } catch (Exception e) {
            ErrorDialog.show(frame, e);
        }
    }

    public static void log(String msg) {
        frame.log(msg);
    }

}
