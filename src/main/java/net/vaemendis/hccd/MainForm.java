package net.vaemendis.hccd;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

public class MainForm extends JFrame implements UserConfiguration {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private static final String PREF_NODE_NAME = "net.vaemendis.hccd.GUI_PREFERENCES";
    private static final String PREF_ROWS = "rows";
    private static final String PREF_COLS = "columns";
    private static final String PREF_EXCEL_CSV = "csv_excel";
    private static final String PREF_DELIMITER = "delimiter";
    private static final String PREF_FALSE_VALUE = "false_value";
    private static final String PREF_CARD_FILTER = "card_filter";
    private static final String PREF_WATCHED_FILE = "watched_file";


    private Preferences prefs;

    private JTextArea logPanel;
    private FileWatcher watcher;
    private final JRadioButton excelRadio;
    private final JSpinner rowSpinner;
    private final JSpinner colSpinner;
    private final String[] delimiters = {";", ","};
    private final JComboBox<String> delimiterBox;
    private final JComboBox<FalseValue> falseBox;
    private final JTextField cardFilter;
    private String watchedFilePath;


    public MainForm(FileWatcher watcher) {
        this.watcher = watcher;
        this.watcher.setConfiguration(this);
        prefs = Preferences.userRoot().node(PREF_NODE_NAME);

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
        // delimiterBox.setMaximumSize(new Dimension(50, 50));

        JLabel falseLbl = new JLabel("False string: ");
        falseBox = new JComboBox<>(FalseValue.values());
        falseBox.setSelectedIndex(0);

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
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(formatLabel);
        topPanel.add(rfcRadio);
        topPanel.add(excelRadio);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(delimiterLbl);
        topPanel.add(delimiterBox);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(falseLbl);
        topPanel.add(falseBox);


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
                        watchedFilePath = file.getPath();
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

        // save preferences on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                savePreferences();
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
        loadPreferences();
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
            watchedFilePath = chosenFile.getPath();
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
    public char getDelimiter() {
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

    @Override
    public FalseValue getFalseValue() {
        return (FalseValue) falseBox.getSelectedItem();
    }

    private void savePreferences() {
        prefs.putInt(PREF_ROWS, getGridRowNumber());
        prefs.putInt(PREF_COLS, getGridColNumber());
        prefs.putBoolean(PREF_EXCEL_CSV, useExcelFormat());
        prefs.put(PREF_DELIMITER, String.valueOf(getDelimiter()));
        prefs.put(PREF_FALSE_VALUE, getFalseValue().name());
        prefs.put(PREF_CARD_FILTER, cardFilter.getText());
        prefs.put(PREF_WATCHED_FILE, watchedFilePath);
    }

    private void loadPreferences() {
        rowSpinner.setValue(prefs.getInt(PREF_ROWS, 2));
        colSpinner.setValue(prefs.getInt(PREF_COLS, 4));
        if (prefs.getBoolean(PREF_EXCEL_CSV, false)) {
            excelRadio.setSelected(true);
        } else {
            excelRadio.setSelected(false);
        }
        delimiterBox.setSelectedItem(prefs.get(PREF_DELIMITER, ";"));
        falseBox.setSelectedItem(FalseValue.valueOf(prefs.get(PREF_FALSE_VALUE, "NONE")));
        cardFilter.setText(prefs.get(PREF_CARD_FILTER, ""));
        watchedFilePath = prefs.get(PREF_WATCHED_FILE, null);

        boolean restored = false;
        if (watchedFilePath != null) {
            File wf = new File(watchedFilePath);
            if (wf.isFile()) {
                try {
                    watcher.watch(wf);
                    restored = true;
                } catch (Exception e) {
                    log("Error while trying to monitor file: " + wf.getPath());
                }
            }
        }
        if (!restored) {
            log("Open your HTML file or drag and drop it here");
        }
    }
}

