package net.vaemendis.hccd;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    public ErrorDialog(Throwable throwable) {
        setModal(true);
        setTitle("An error occured");
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.NORTH);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblNewLabel = new JLabel("An unexpected error occured");
        panel.add(lblNewLabel);

        Component verticalStrut1 = Box.createVerticalStrut(20);
        verticalStrut1.setMaximumSize(new Dimension(32767, 5));
        verticalStrut1.setMinimumSize(new Dimension(0, 5));
        verticalStrut1.setPreferredSize(new Dimension(0, 5));
        panel.add(verticalStrut1);

        Component verticalStrut2 = Box.createVerticalStrut(20);
        verticalStrut2.setPreferredSize(new Dimension(0, 10));
        verticalStrut2.setMinimumSize(new Dimension(0, 10));
        verticalStrut2.setMaximumSize(new Dimension(32767, 10));
        panel.add(verticalStrut2);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        textArea.setText(getStack(throwable));
        textArea.setCaretPosition(0);
        contentPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("Close");
        okButton.addActionListener(e -> System.exit(1));
        okButton.setPreferredSize(new Dimension(70, 25));
        okButton.setMinimumSize(new Dimension(70, 25));
        okButton.setMaximumSize(new Dimension(70, 25));
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

    }

    public static void show(Component parent, Throwable t) {
        ErrorDialog dialog = new ErrorDialog(t);
        try {
            dialog.setIconImages(Tools.getApplicationIcons());
        } catch (IOException e) {
            // don't really care if it fails
            e.printStackTrace();
        }

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private String getStack(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

}
