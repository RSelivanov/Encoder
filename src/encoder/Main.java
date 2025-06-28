package encoder;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

    private final int margin = 5;
    private final int indentY = 90;

    private ArrayList<String> lines = new ArrayList<>();
    private JPanel layout = new JPanel(null); // абсолютное позиционирование
    private JScrollPane scrollPane = new JScrollPane(layout);

    private JPasswordField fieldKey = new JPasswordField();
    private JTextField fieldName = new JTextField();
    private JTextField fieldLogin = new JTextField();
    private JPasswordField fieldPassword = new JPasswordField();
    private JButton addBtn = new JButton("Add");
    private JCheckBox simplePasswordBox = new JCheckBox("Simple Password");

    private int spaseY = margin * 2;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createUI());
    }

    private void createUI() {
        JFrame frame = new JFrame("Encoder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(555, Toolkit.getDefaultToolkit().getScreenSize().height);
        frame.setLocation(0, 0);

        lines = Data.readToFile();
        Collections.sort(lines, String.CASE_INSENSITIVE_ORDER);

        render();

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }

    private void render() {

        for (ActionListener al : addBtn.getActionListeners())
        {
            addBtn.removeActionListener(al);
        }

        layout.removeAll();
        spaseY = margin * 2;

        JLabel labelKey = new JLabel("Key:");
        labelKey.setBounds(margin, margin + 3, 30, 20);
        layout.add(labelKey);

        fieldKey.setBounds(25 + margin, margin, 150, 25);
        layout.add(fieldKey);

        simplePasswordBox.setBounds(190 + margin, margin + 4, 150, 25);
        layout.add(simplePasswordBox);

        JLabel labelName = new JLabel("Name");
        labelName.setBounds(margin, 25 + margin + 3, 40, 20);
        layout.add(labelName);

        JLabel labelLogin = new JLabel("Login");
        labelLogin.setBounds(150 + margin, 25 + margin + 3, 40, 20);
        layout.add(labelLogin);

        JLabel labelPassword = new JLabel("Password");
        labelPassword.setBounds(300 + margin, 25 + margin + 3, 60, 20);
        layout.add(labelPassword);

        fieldName.setBounds(margin, 50 + margin, 130, 25);
        layout.add(fieldName);

        fieldLogin.setBounds(150 + margin, 50 + margin, 130, 25);
        layout.add(fieldLogin);

        fieldPassword.setBounds(300 + margin, 50 + margin, 130, 25);
        layout.add(fieldPassword);

        addBtn.setBounds(450 + margin, 50 + margin, 60, 25);
        layout.add(addBtn);

        addBtn.addActionListener(e -> {

            String key = new String(fieldKey.getPassword()).trim();
             if (fieldName.getText().trim().isEmpty() || fieldLogin.getText().trim().isEmpty() || key.isEmpty()) 
             {
                JOptionPane.showMessageDialog(null, "Key Name or Login is Empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

                String cryptPass;

                try {
                    if (new String(fieldPassword.getPassword()).trim().isEmpty()) {

                        String generatedPassword = Encoder.generatePassword(simplePasswordBox.isSelected());
                        if (generatedPassword == null || generatedPassword.trim().isEmpty()) {
                            throw new RuntimeException("Password generated empty");
                        }
                        cryptPass = Encoder.encryptModern(generatedPassword, key);

                    } else {
                        cryptPass = Encoder.encryptModern(new String(fieldPassword.getPassword()), key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                lines.add(fieldName.getText() + "<::>" + fieldLogin.getText() + "<::>" + cryptPass);
                fieldName.setText("");
                fieldLogin.setText("");
                fieldPassword.setText("");

                Data.writeToFile(lines);
                render();
            }
        );

        for (int i = 0; i < lines.size(); i++) {
            String[] cols = lines.get(i).split("<::>");
            int y = indentY + i * 25;

            JTextField name = new JTextField(cols[0]);
            name.setBounds(margin, y, 130, 25);
            name.setCaretPosition(0);
            layout.add(name);

            JTextField login = new JTextField(cols[1]);
            login.setBounds(150 + margin, y, 130, 25);
            login.setCaretPosition(0);
            layout.add(login);

            JButton getBtn = new JButton("Get Password");
            getBtn.setBounds(300 + margin, y, 140, 25);
            layout.add(getBtn);

            JButton delBtn = new JButton("Del");
            delBtn.setBounds(450 + margin, y, 60, 25);
            layout.add(delBtn);

            int index = i;

            getBtn.addActionListener(evt -> {
                try {
                    String key = new String(fieldKey.getPassword()).trim();
                    if (key.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Key is Empty", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String[] arr = lines.get(index).split("<::>");
                    String decryptedPawword = "";

                    try {
                            String decrypted = Encoder.decryptModern(arr[2], key);
                            decryptedPawword = decrypted;
                            // Успех
                        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
                            // Неверный ключ или повреждённые данные
                            JOptionPane.showMessageDialog(null, "Wrong key or corrupted data", "Decrypt Error", JOptionPane.ERROR_MESSAGE);
                            return; // НЕ ПРОДОЛЖАЕМ
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Decrypt Error", JOptionPane.ERROR_MESSAGE);
                            return; // НЕ ПРОДОЛЖАЕМ
                        }

                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(decryptedPawword), null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            delBtn.addActionListener(evt -> {
                lines.remove(index);
                Data.writeToFile(lines);
                render();
            });

            spaseY += 25;
        }

        layout.setPreferredSize(new Dimension(570, indentY + spaseY));
        layout.revalidate();
        layout.repaint();
    }
}
