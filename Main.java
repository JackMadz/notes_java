import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Загружаем настройки
        var settings = Settings.defaultSettings();

        // Создаем главное окно
        JFrame frame = new JFrame(settings.programName());
        frame.setSize(settings.width(), settings.height());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Центрируем окно на экране

        // Добавим простую текстовую область для будущих заметок
        JTextArea textArea = new JTextArea("Здесь будут ваши заметки...");
        frame.add(new JScrollPane(textArea));

        // Делаем окно видимым
        frame.setVisible(true);
    }
}