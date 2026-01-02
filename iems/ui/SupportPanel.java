package iems.ui;

import iems.dao.SupportDAO;
import iems.model.Role;
import iems.model.SupportRequest;
import iems.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SupportPanel extends JPanel {
    private final User user;
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton newRequestBtn = UIStyle.success("➕ New Request");
    private final JButton exportBtn = UIStyle.primary("📤 Export CSV");
    private final JButton reviewBtn = UIStyle.warning("✔ Review Selected");
    private final JComboBox<String> statusFilter = UIStyle.styledComboBox(
            new String[] { "All", "Draft", "Submitted", "Under Review", "Approved", "Rejected" });

    public SupportPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(UIStyle.LIGHT_BG);

        // Header
        JPanel header = UIStyle.card();
        header.add(UIStyle.title("Educational Support Requests"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Filter & Actions
        JPanel actions = UIStyle.card();
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setOpaque(false);
        bar.add(new JLabel("Status filter"));
        bar.add(statusFilter);
        bar.add(newRequestBtn);
        bar.add(exportBtn);
        if (user.getRole() == Role.STAFF || user.getRole() == Role.TEACHER) {
            bar.add(reviewBtn);
        }
        actions.add(bar, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        // Table
        model = new DefaultTableModel(new String[] { "ID", "Type", "Status", "Notes" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UIStyle.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        reload();

        // Listeners
        statusFilter.addActionListener(e -> applyFilter());
        newRequestBtn.addActionListener(e -> createRequest());
        exportBtn.addActionListener(e -> exportCsv());
        reviewBtn.addActionListener(e -> reviewSelected());
    }

    private void reload() {
        try {
            model.setRowCount(0);
            SupportDAO dao = new SupportDAO();
            List<SupportRequest> data = (user.getRole() == Role.STAFF || user.getRole() == Role.TEACHER)
                    ? dao.all()
                    : dao.forUser(user.getId());
            for (SupportRequest r : data) {
                model.addRow(new Object[] { r.getId(), r.getType(), r.getStatus(), r.getNotes() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String sf = (String) statusFilter.getSelectedItem();
        for (int i = 0; i < model.getRowCount(); i++) {
            String st = model.getValueAt(i, 2).toString();
            boolean show = "All".equals(sf) || sf.equals(st);
            table.setRowHeight(i, show ? 28 : 0);
        }
    }

    private void createRequest() {
        JComboBox<String> type = UIStyle.styledComboBox(new String[] {
                "Device loan", "Learning vouchers", "Transport assistance", "Fees assistance", "Special needs support"
        });
        JTextArea notes = UIStyle.styledTextArea(4, 24);

        int result = JOptionPane.showConfirmDialog(this,
                new Object[] { "Type", type, "Notes (optional)", new JScrollPane(notes) },
                "New Support Request", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                SupportRequest r = new SupportRequest();
                r.setUserId(user.getId());
                r.setType((String) type.getSelectedItem());
                r.setStatus("Submitted");
                r.setNotes(notes.getText().trim());
                new SupportDAO().create(r);
                reload();
                JOptionPane.showMessageDialog(this, "Request submitted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Submission Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reviewSelected() {
        if (!(user.getRole() == Role.STAFF || user.getRole() == Role.TEACHER)) {
            JOptionPane.showMessageDialog(this, "Only staff/teachers can review.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a request to review.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long id = Long.parseLong(model.getValueAt(row, 0).toString());
        JComboBox<String> status = UIStyle.styledComboBox(new String[] { "Under Review", "Approved", "Rejected" });
        int result = JOptionPane.showConfirmDialog(this, new Object[] { "New status", status },
                "Update Status", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                new SupportDAO().updateStatus(id, (String) status.getSelectedItem());
                reload();
                JOptionPane.showMessageDialog(this, "Status updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportCsv() {
        try (var w = new FileWriter("support_requests.csv")) {
            w.write("ID,Type,Status,Notes\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                if (table.getRowHeight(i) == 0)
                    continue;
                w.write(String.join(",",
                        model.getValueAt(i, 0).toString(),
                        model.getValueAt(i, 1).toString(),
                        model.getValueAt(i, 2).toString(),
                        model.getValueAt(i, 3).toString().replace(",", ";")) + "\n");
            }
            JOptionPane.showMessageDialog(this, "Exported support_requests.csv",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}