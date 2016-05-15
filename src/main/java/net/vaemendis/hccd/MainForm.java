package net.vaemendis.hccd;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainForm extends JFrame implements UserConfiguration{

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private JTextArea logPanel;
    private FileWatcher watcher;
    private final JRadioButton excelRadio;
    private final JSpinner rowSpinner;
    private final JSpinner colSpinner;
    private final String[] delimiters = {";", ","};
    private final JComboBox<String> delimiterBox;
    private final JTextField cardFilter;



    public MainForm(FileWatcher watcher) {
        this.watcher = watcher;
        this.watcher.setConfiguration(this);

        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        setTitle("HTML+CSS Card Designer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));

        // TOP PANEL
        JButton openBtn = new JButton("Open...");
        openBtn.addActionListener(e -> {
            try {
                openFile();
            } catch (IOException e1) {
                ErrorDialog.show(MainForm.this, e1);
            }
        });
        openBtn.setPreferredSize(new Dimension(120, 25));
        openBtn.setMaximumSize(new Dimension(120, 25));

        JLabel rowLbl = new JLabel("Rows: ");
        rowSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 50, 1));
        rowSpinner.setMaximumSize(new Dimension(40, 50));
        JLabel colLbl = new JLabel("Columns: ");
        colSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 50, 1));
        colSpinner.setMaximumSize(new Dimension(40, 50));

        JLabel formatLabel = new JLabel("CSV format: ");
        excelRadio = new JRadioButton("Excel");
        JRadioButton rfcRadio = new JRadioButton("RFC-4180");
        ButtonGroup group = new ButtonGroup();
        group.add(rfcRadio);
        group.add(excelRadio);
        rfcRadio.setSelected(true);

        JLabel delimiterLbl = new JLabel("Delimiter: ");
        delimiterBox = new JComboBox<>(delimiters);
        delimiterBox.setSelectedIndex(0);
        delimiterBox.setMaximumSize(new Dimension(40, 50));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            try {
                this.watcher.generateCardSheet();
            } catch (IOException e1) {
                ErrorDialog.show(MainForm.this, e1);
            }
        });
        refreshBtn.setPreferredSize(new Dimension(120, 25));
        refreshBtn.setMaximumSize(new Dimension(120, 25));


        JLabel filterLbl = new JLabel("Card filter (eg. 1,3,6-8)");
        cardFilter = new JTextField();
        cardFilter.setMaximumSize(new Dimension(120, 25));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.add(openBtn);
        rightPanel.add(Box.createVerticalStrut(60));
        rightPanel.add(filterLbl);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(cardFilter);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(refreshBtn);


        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(rightPanel, BorderLayout.EAST);


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.add(rowLbl);
        topPanel.add(rowSpinner);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(colLbl);
        topPanel.add(colSpinner);
        topPanel.add(Box.createHorizontalStrut(30));
        topPanel.add(formatLabel);
        topPanel.add(rfcRadio);
        topPanel.add(excelRadio);
        topPanel.add(Box.createHorizontalStrut(30));
        topPanel.add(delimiterLbl);
        topPanel.add(delimiterBox);


        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(topPanel, BorderLayout.NORTH);

        // CENTER PANEL
        logPanel = new JTextArea();
        logPanel.setEditable(false);
        logPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        JScrollPane scrollPane = new JScrollPane(logPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Add drag and drop support
        new FileDrop(null, logPanel, files -> {
            String errorMsg = null;
            if (files.length == 1) {
                File file = files[0];
                if (file.isFile() && isHtml(file)) {
                    try {
                        watcher.watch(file);
                    } catch (Exception e) {
                        ErrorDialog.show(MainForm.this, e);
                    }
                } else {
                    errorMsg = "Only HTML files are supported";
                }
            } else {
                errorMsg = "Drop only one file at a time";
            }
            if (errorMsg != null) {
                JOptionPane.showMessageDialog(MainForm.this, errorMsg, "File opening failed", JOptionPane.ERROR_MESSAGE);
            }
        });


    }

    public void init() throws IOException {
        setIconImages(Tools.getApplicationIcons());

        // center frame
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setLocation((screenWidth - getWidth()) / 2, (screenHeight - getHeight()) / 2);
    }

    public void openFile() throws IOException {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || isHtml(f);
            }

            @Override
            public String getDescription() {
                return "HTML files";
            }
        });
        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File chosenFile = chooser.getSelectedFile();
            watcher.watch(chosenFile);
        }
    }

    public void log(String msg) {
        SwingUtilities.invokeLater(() -> logPanel.append(sdf.format(new Date()) + " - " + msg + "\n"));
    }

    private boolean isHtml(File f) {
        return f.getName().toLowerCase().endsWith("htm") ||
                f.getName().toLowerCase().endsWith("html");
    }

    @Override
    public int getGridColNumber() {
        return (int) colSpinner.getValue();
    }

    @Override
    public int getGridRowNumber() {
        return (int) rowSpinner.getValue();
    }

    @Override
    public boolean useExcelFormat() {
        return excelRadio.isSelected();
    }

    @Override
    public char getDelimiter(){
        return ((String) delimiterBox.getSelectedItem()).charAt(0);
    }

    @Override
    public List<Integer> getCardFilter() {
        List<Integer> cardList = new ArrayList<>();
        String filterString = cardFilter.getText();
        try {
            if (filterString != null && filterString.length() > 0) {
                String[] commaSepValues = filterString.split(",");
                for (String s : commaSepValues) {
                    if (s.contains("-")) {
                        String[] dashSepValues = s.split("-");
                        int start = Integer.valueOf(dashSepValues[0]);
                        int end = Integer.valueOf(dashSepValues[1]);
                        for (int i = start; i < end + 1; i++) {
                            cardList.add(i);
                        }
                    } else {
                        cardList.add(Integer.valueOf(s));
                    }
                }
            }
        } catch (Exception e) {
            Hccd.log("Invalid filter: " + filterString);
            cardList.clear();
        }

        return cardList;
    }
}
