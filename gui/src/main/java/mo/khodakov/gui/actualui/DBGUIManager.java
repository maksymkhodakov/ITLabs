package mo.khodakov.gui.actualui;

import mo.khodakov.gui.database.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class DBGUIManager extends JFrame {
    private Database database;

    private final JLabel dbNameLabel = new JLabel("Відкрийте вбо створіть БД");
    private final JList tableList = new JList();
    private final DefaultListModel tableListModel = new DefaultListModel();
    private final JLabel resultMessage = new JLabel();
    private final MyTableModel resultTableModel = new MyTableModel();
    private final JPanel tableControlPanel = new JPanel();
    private final JFileChooser fileChooser = new JFileChooser();

    DBGUIManager() {
        super("DBMS");

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1600, 1000);
        this.getContentPane().setBackground(Color.lightGray);
        this.getContentPane().add(BorderLayout.NORTH, initializeMenuBar()).setBackground(Color.lightGray);
        this.getContentPane().add(BorderLayout.WEST, initializeMenuPanel()).setBackground(Color.lightGray);
        this.getContentPane().add(BorderLayout.CENTER, initializeResultPanel()).setBackground(Color.lightGray);
        this.getContentPane().add(BorderLayout.SOUTH, initializeControlPanel()).setBackground(Color.lightGray);
    }

    private JMenuBar initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Файл");
        JMenu menuAction = new JMenu("Операції");
        JMenu menuHelp = new JMenu("Help");
        menuBar.add(menuFile);
        menuBar.add(menuAction);
        menuBar.add(menuHelp);
        JMenuItem menuFileOpen = new JMenuItem("Відкрити БД");
        menuFileOpen.addActionListener(e -> {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String databasePath = fileChooser.getSelectedFile().getAbsolutePath();
                try {
                    database = new DatabaseReader(databasePath).read();
                } catch (Exception ex) {
                    database = new Database(databasePath);
                    ex.printStackTrace();
                }
                dbNameLabel.setText(fileChooser.getSelectedFile().getName());
                populateTableList();
            }
        });
        JMenuItem menuFileSaveAs = new JMenuItem("Зберегти як");
        menuFileSaveAs.addActionListener(e -> {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                database.setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    database.save();
                    dbNameLabel.setText(fileChooser.getSelectedFile().getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menuFile.add(menuFileOpen);
        menuFile.add(menuFileSaveAs);
        JMenuItem menuCreateTable = new JMenuItem("Створити таблицю");
        JMenuItem menuDeleteTable = new JMenuItem("Видалити таблицю");
        JMenuItem menuCombine = new JMenuItem("Об'єднання таблиць");
        menuCreateTable.addActionListener(e -> {
            if (database == null) {
                JOptionPane.showMessageDialog(this, "Оберіть спочатку БД");
                return;
            }
            TableAddPanel tableAdd = new TableAddPanel();
            if (JOptionPane.showConfirmDialog(this, tableAdd,
                    "Введіть дані до таблиці:", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                displayQueryResults(database.query(tableAdd.getDBQuery()), false);
                populateTableList();
            }
        });
        menuDeleteTable.addActionListener(e -> {
            String tableName = (String) tableList.getSelectedValue();
            if (tableName == null || database == null) {
                JOptionPane.showMessageDialog(this, "Таблицю не обрано!");
                return;
            }
            if (JOptionPane.showConfirmDialog(this,
                    "Ви впевнені,що хочете видалити " + tableName + "?", "Будь ласка, підтвердіть",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                displayQueryResults(database.query("drop table " + tableName), false);
                populateTableList();
            }
        });
        menuCombine.addActionListener(e -> {
            java.util.List tableNames = tableList.getSelectedValuesList();
            if (tableNames.size() != 2 || database == null) {
                JOptionPane.showMessageDialog(this, "Треба обрати 2 таблиці");
                return;
            }
            JOptionPane.showMessageDialog(this, "Об'єднання " + tableNames.get(0) + " і " + tableNames.get(1));
            displayQueryResults(database.query(String.format("combine %s with %s", tableNames.get(0), tableNames.get(1))), false);
        });

        menuAction.add(menuCreateTable);
        menuAction.add(menuDeleteTable);
        menuAction.add(menuCombine);
        JMenuItem menuAbout = new JMenuItem("Info");
        menuAbout.addActionListener(e -> JOptionPane.showMessageDialog(this, "DBMS TTP-42"));
        menuHelp.add(menuAbout);
        return menuBar;
    }

    private JPanel initializeControlPanel() {
        JPanel controlPanel = new JPanel();
        JLabel label = new JLabel("Введіть запит: ");
        JTextField queryTextField = new JTextField(100);
        JButton clearButton = new JButton("Очистити");
        clearButton.addActionListener(e -> {
            queryTextField.setText("");
        });
        JButton run = new JButton("Запуск");
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (database != null) {
                    displayQueryResults(database.query(queryTextField.getText()), false);
                    populateTableList();
                }
            }
        };
        queryTextField.addActionListener(action);
        run.addActionListener(action);
        controlPanel.add(label);
        controlPanel.add(queryTextField);
        controlPanel.add(clearButton);
        controlPanel.add(run);
        return controlPanel;
    }

    private JPanel initializeMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        tableList.setModel(tableListModel);
        tableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableList.setLayoutOrientation(JList.VERTICAL);
        tableList.setVisibleRowCount(-1);
        tableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = tableList.locationToIndex(e.getPoint());
                    String tableName = tableListModel.getElementAt(index).toString();
                    displayQueryResults(database.query("select * from " + tableName), true);
                }
            }
        });
        JScrollPane listScroll = new JScrollPane(tableList);
        listScroll.setPreferredSize(new Dimension(300, 100));
        menuPanel.add(BorderLayout.NORTH, dbNameLabel);
        menuPanel.add(BorderLayout.CENTER, listScroll);
        return menuPanel;
    }

    private JPanel initializeResultPanel() {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(BorderLayout.NORTH, resultMessage);
        JTable resultTable = new JTable(resultTableModel);
        JScrollPane tableScroll = new JScrollPane(resultTable);
        resultTable.setFillsViewportHeight(true);
        resultPanel.add(BorderLayout.CENTER, tableScroll);
        JButton addRowButton = new JButton("Add row");
        addRowButton.addActionListener(e -> {
            if (database == null) {
                JOptionPane.showMessageDialog(this, "No open database");
                return;
            }
            String tableName = (String) tableList.getSelectedValue();
            try {
                RowAddPanel rowAdd = new RowAddPanel(tableName, database.getTableColumns(tableName));
                if (JOptionPane.showConfirmDialog(this, rowAdd,
                        "Enter data for the new row:", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    displayQueryResults(database.query(rowAdd.getDBQuery()), false);
                    populateTableList();
                }
            } catch (Exception ex) {
            }
        });
        tableControlPanel.add(addRowButton);
        tableControlPanel.setVisible(false);
        resultPanel.add(BorderLayout.SOUTH, tableControlPanel);
        return resultPanel;
    }

    private void populateTableList() {
        if (database != null) {
            Result tables = database.query("list tables");
            tableListModel.clear();
            for (Row row : tables.getRows()) {
                tableListModel.addElement(row.getElement("table_name").getValue());
            }
        }
    }

    private void displayQueryResults(Result result, boolean isTableDisplayed) {
        tableControlPanel.setVisible(isTableDisplayed);

        resultMessage.setText("<html>Result: " + result.getStatus() +
                (result.getStatus() == Result.Status.FAIL ? "<br/>" + result.getReport() : "") +
                (result.getRows() == null || result.getRows().isEmpty() ? "<br/>Result rows empty" : "") +
                "</html>");
        resultTableModel.setResult(result);
    }
}

class MyTableModel extends AbstractTableModel {
    ArrayList<Row> rows = new ArrayList<>();
    ArrayList<String> columns = new ArrayList<>();

    void setResult(Result result) {
        rows.clear();
        columns.clear();
        if (result.getRows() != null && !result.getRows().isEmpty()) {
            rows.addAll(result.getRows());
            for (Element element : result.getRows().iterator().next().getElements()) {
                columns.add(element.getColumn());
            }
        }
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).getElement(columns.get(columnIndex)).getValue();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }
}