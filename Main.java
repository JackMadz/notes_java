import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main {
    record Note(String title, String content) {
        @Override
        public String toString() {
            return title;
        }
    }

    public static void main(String[] args) {
        var settings = Settings.defaultSettings();

        JFrame frame = new JFrame(settings.programName());
        frame.setSize(settings.width(), settings.height());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        DefaultListModel<Note> notesModel = new DefaultListModel<>();
        JList<Note> notesList = new JList<>(notesModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Правая часть с CardLayout
        JPanel rightPanel = new JPanel(new CardLayout());
        
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.add(new JLabel("Выберите заметку слева или создайте новую"));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField viewTitleField = new JTextField();
        viewTitleField.setEditable(false);
        JTextArea viewContentArea = new JTextArea();
        viewContentArea.setLineWrap(true);
        viewContentArea.setWrapStyleWord(true);
        viewContentArea.setEditable(false);

        JPanel viewEditorPanel = new JPanel(new BorderLayout(5, 5));
        JPanel topViewPanel = new JPanel(new BorderLayout(5, 5));
        topViewPanel.add(new JLabel("Заголовок:"), BorderLayout.NORTH);
        topViewPanel.add(viewTitleField, BorderLayout.CENTER);
        viewEditorPanel.add(topViewPanel, BorderLayout.NORTH);
        viewEditorPanel.add(new JScrollPane(viewContentArea), BorderLayout.CENTER);

        contentPanel.add(viewEditorPanel, BorderLayout.CENTER);

        rightPanel.add(emptyPanel, "EMPTY");
        rightPanel.add(contentPanel, "CONTENT");
        
        CardLayout cardLayout = (CardLayout) rightPanel.getLayout();
        cardLayout.show(rightPanel, "EMPTY");

        // Левая панель (сайдбар)
        JButton addButton = new JButton("Добавить заметку");
        JPanel leftSidebar = new JPanel(new BorderLayout(5, 5));
        leftSidebar.setBorder(new EmptyBorder(5, 5, 5, 5));
        leftSidebar.setPreferredSize(new Dimension(250, 0));
        
        leftSidebar.add(new JLabel("Список заметок"), BorderLayout.NORTH);
        leftSidebar.add(new JScrollPane(notesList), BorderLayout.CENTER);
        leftSidebar.add(addButton, BorderLayout.SOUTH);

        // Обработка кликов по списку (в том числе мимо элементов)
        notesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Определяем индекс элемента по координатам клика
                int index = notesList.locationToIndex(e.getPoint());
                // Если кликнули ниже существующие элементы (в пустое место)
                if (index == -1 || !notesList.getCellBounds(index, index).contains(e.getPoint())) {
                    notesList.clearSelection();
                    cardLayout.show(rightPanel, "EMPTY");
                }
            }
        });

        // При выборе заметки — показываем контент
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Note selected = notesList.getSelectedValue();
                if (selected != null) {
                    viewTitleField.setText(selected.title());
                    viewContentArea.setText(selected.content());
                    cardLayout.show(rightPanel, "CONTENT");
                }
            }
        });

        // Модальное окно добавления заметки
        addButton.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Новая заметка", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(frame);
            dialog.setLayout(new BorderLayout(10, 10));
            ((JComponent) dialog.getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

            JTextField newTitleField = new JTextField();
            JTextArea newContentArea = new JTextArea();
            newContentArea.setLineWrap(true);
            newContentArea.setWrapStyleWord(true);

            JPanel formPanel = new JPanel(new BorderLayout(5, 5));
            JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
            titlePanel.add(new JLabel("Заголовок:"), BorderLayout.NORTH);
            titlePanel.add(newTitleField, BorderLayout.CENTER);
            formPanel.add(titlePanel, BorderLayout.NORTH);

            JPanel textPanel = new JPanel(new BorderLayout(5, 5));
            JLabel charCountLabel = new JLabel("Символов: 0 / 250");
            textPanel.add(new JLabel("Текст (до 250 символов):"), BorderLayout.NORTH);
            textPanel.add(new JScrollPane(newContentArea), BorderLayout.CENTER);
            textPanel.add(charCountLabel, BorderLayout.SOUTH);
            formPanel.add(textPanel, BorderLayout.CENTER);

            newContentArea.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyTyped(java.awt.event.KeyEvent evt) {
                    if (newContentArea.getText().length() >= 250 && evt.getKeyChar() != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                        evt.consume();
                    }
                }

                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    int length = newContentArea.getText().length();
                    if (length > 250) {
                        newContentArea.setText(newContentArea.getText().substring(0, 250));
                        length = 250;
                    }
                    charCountLabel.setText("Символов: " + length + " / 250");
                }
            });

            JButton saveDialogButton = new JButton("Сохранить");
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(saveDialogButton, BorderLayout.SOUTH);

            saveDialogButton.addActionListener(ev -> {
                String title = newTitleField.getText().trim();
                String content = newContentArea.getText().trim();

                if (!title.isEmpty() || !content.isEmpty()) {
                    if (title.isEmpty()) title = "Без названия";
                    Note newNote = new Note(title, content);
                    notesModel.addElement(newNote);
                    notesList.setSelectedValue(newNote, true);
                }
                dialog.dispose();
            });

            dialog.setVisible(true);
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSidebar, rightPanel);
        splitPane.setDividerLocation(250);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}