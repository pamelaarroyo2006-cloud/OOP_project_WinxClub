package iems.ui;

import iems.dao.MaterialDAO;
import iems.model.Material;
import iems.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

// Custom renderer for buttons
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        setText((value == null) ? "View" : value.toString());
        return this;
    }
}

// Custom editor for buttons
class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String filePath;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton("View");
        button.setOpaque(true);
        button.addActionListener(e -> {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(button,
                            "File not found: " + filePath,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(button,
                        "Unable to open file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        // Assuming file_path is stored in column 4 (Format column is 4, file_path not
        // shown in table)
        // Adjust index if you add file_path column explicitly
        filePath = (String) table.getModel().getValueAt(row, 4);
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "View";
    }
}

public class TeacherMaterialsPanel extends JPanel {
    private final User teacher;
    private final MaterialDAO materialDAO;
    private final DefaultTableModel materialsTableModel;
    private final JTable materialsTable;

    public TeacherMaterialsPanel(User teacher) {
        this.teacher = teacher;
        try {
            materialDAO = new MaterialDAO();
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }

        materialsTableModel = new DefaultTableModel(
                new String[] { "ID", "Title", "Topic", "Level", "Format", "View" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };
        materialsTable = new JTable(materialsTableModel);

        initializeUI();
        loadMaterials();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIStyle.LIGHT_BG);

        // Header
        JPanel header = UIStyle.card();
        JLabel title = UIStyle.title("Course Materials");
        JLabel breadcrumb = UIStyle.section(" / " + teacher.getFullName());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(title);
        left.add(breadcrumb);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        JButton addMaterialBtn = UIStyle.success("Add Material");
        JButton uploadBtn = UIStyle.primary("Upload File");
        JButton refreshBtn = UIStyle.primary("Refresh");
        buttons.add(addMaterialBtn);
        buttons.add(uploadBtn);
        buttons.add(refreshBtn);

        header.add(left, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        UIStyle.styleTable(materialsTable);
        JScrollPane scrollPane = new JScrollPane(materialsTable);
        scrollPane.setBorder(null);
        JPanel tableCard = UIStyle.card();
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // Add renderer/editor for "View" column
        materialsTable.getColumn("View").setCellRenderer(new ButtonRenderer());
        materialsTable.getColumn("View").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Actions
        addMaterialBtn.addActionListener(e -> showAddMaterialDialog());
        uploadBtn.addActionListener(e -> uploadMaterialFile());
        refreshBtn.addActionListener(e -> loadMaterials());
    }

    private void loadMaterials() {
        try {
            List<Material> materials = materialDAO.all();
            materialsTableModel.setRowCount(0);
            for (Material m : materials) {
                materialsTableModel.addRow(new Object[] {
                        m.getId(), m.getTitle(), m.getTopic(),
                        m.getLevel(), m.getFormat(), "View"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading materials: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddMaterialDialog() {
        JTextField titleField = UIStyle.styledTextField(20);
        JTextField topicField = UIStyle.styledTextField(20);
        JTextField levelField = UIStyle.styledTextField(20);
        JTextField formatField = UIStyle.styledTextField(20);

        int result = JOptionPane.showConfirmDialog(this,
                new Object[] { "Title", titleField, "Topic", topicField,
                        "Level", levelField, "Format", formatField },
                "Add Material", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Material m = new Material();
                m.setTitle(titleField.getText().trim());
                m.setTopic(topicField.getText().trim());
                m.setLevel(levelField.getText().trim());
                m.setFormat(formatField.getText().trim());
                m.setTeacherId(teacher.getId());
                materialDAO.create(m);
                loadMaterials();
                JOptionPane.showMessageDialog(this, "Material added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void uploadMaterialFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            JTextField titleField = UIStyle.styledTextField(20);
            JTextField topicField = UIStyle.styledTextField(20);
            JTextField levelField = UIStyle.styledTextField(20);
            JTextField formatField = UIStyle.styledTextField(20);

            int metaResult = JOptionPane.showConfirmDialog(this,
                    new Object[] { "Title", titleField, "Topic", topicField,
                            "Level", levelField, "Format", formatField },
                    "Material Metadata", JOptionPane.OK_CANCEL_OPTION);

            if (metaResult == JOptionPane.OK_OPTION) {
                try {
                    Material m = new Material();
                    m.setTitle(titleField.getText().trim());
                    m.setTopic(topicField.getText().trim());
                    m.setLevel(levelField.getText().trim());
                    m.setFormat(formatField.getText().trim());
                    m.setFilePath(file.getAbsolutePath());
                    m.setTeacherId(teacher.getId());

                    materialDAO.create(m);
                    loadMaterials();

                    JOptionPane.showMessageDialog(this,
                            "File uploaded and saved: " + file.getName(),
                            "Upload Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error saving material: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}