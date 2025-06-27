package paymentremindersystem;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

public class PaymentReminderSystem extends JFrame {

    static class Customer {
        String name;
        String email;
        double outstandingBalance;

        Customer(String name, String email, double outstandingBalance) {
            this.name = name;
            this.email = email;
            this.outstandingBalance = outstandingBalance;
        }

        @Override
        public String toString() {
            return name + " - $" + outstandingBalance;
        }
    }

    private JList<Customer> customerJList;
    private DefaultListModel<Customer> listModel;
    private JTextArea reminderTextArea;
    private JButton sendReminderButton;

    private java.util.List<Customer> customers;

    public PaymentReminderSystem() {
        setTitle("Accounting Fee System - Payment Reminder");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        customers = new java.util.ArrayList<>();
        customers.add(new Customer("Alice Johnson", "alice@example.com", 150.75));
        customers.add(new Customer("Bob Smith", "bob@example.com", 0));
        customers.add(new Customer("Charlie Davis", "charlie@example.com", 320.00));
        customers.add(new Customer("Diana Moore", "diana@example.com", 0));
        customers.add(new Customer("Evan Lee", "evan@example.com", 50.00));

        listModel = new DefaultListModel<>();
        for (Customer c : customers) {
            listModel.addElement(c);
        }

        customerJList = new JList<>(listModel);
        customerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = new JScrollPane(customerJList);

        reminderTextArea = new JTextArea();
        reminderTextArea.setEditable(false);
        JScrollPane textScrollPane = new JScrollPane(reminderTextArea);

        sendReminderButton = new JButton("Send Payment Reminder");
        sendReminderButton.setEnabled(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, textScrollPane);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);
        add(sendReminderButton, BorderLayout.SOUTH);

        // ----------- ADDED: Input Panel for dynamic customer entry --------------
        JTextField nameField = new JTextField(10);
        JTextField emailField = new JTextField(10);
        JTextField balanceField = new JTextField(5);

        JButton addCustomerButton = new JButton("Add Customer");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Balance:"));
        inputPanel.add(balanceField);
        inputPanel.add(addCustomerButton);

        add(inputPanel, BorderLayout.NORTH);

        addCustomerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String balanceText = balanceField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || balanceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double balance;
            try {
                balance = Double.parseDouble(balanceText);
                if (balance < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number for balance.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer newCustomer = new Customer(name, email, balance);
            customers.add(newCustomer);
            listModel.addElement(newCustomer);

            // Clear fields after adding
            nameField.setText("");
            emailField.setText("");
            balanceField.setText("");
        });
        // ------------------------------------------------------------------------

        customerJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Customer selected = customerJList.getSelectedValue();
                if (selected != null && selected.outstandingBalance > 0) {
                    sendReminderButton.setEnabled(true);
                    reminderTextArea.setText(generateReminderMessage(selected));
                } else {
                    sendReminderButton.setEnabled(false);
                    reminderTextArea.setText("");
                }
            }
        });

        sendReminderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer selected = customerJList.getSelectedValue();
                if (selected != null && selected.outstandingBalance > 0) {
                    JOptionPane.showMessageDialog(PaymentReminderSystem.this,
                            "Payment reminder sent to " + selected.email,
                            "Reminder Sent",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private String generateReminderMessage(Customer c) {
        return "To: " + c.email + "\n\n" +
                "Dear " + c.name + ",\n\n" +
                "Our records indicate that you have an outstanding balance of $" + c.outstandingBalance + ".\n" +
                "Please arrange your payment at your earliest convenience.\n\n" +
                "Thank you.\n";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaymentReminderSystem app = new PaymentReminderSystem();
            app.setVisible(true);
        });
    }
}
