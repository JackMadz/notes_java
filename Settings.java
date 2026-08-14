public record Settings(String programName, int width, int height) {
    // Настройки по умолчанию
    public static Settings defaultSettings() {
        return new Settings("Мои Заметки", 800, 600);
    }
}