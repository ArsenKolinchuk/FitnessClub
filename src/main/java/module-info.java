module com.example.fitnessclub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Цей рядок відкриває доступ до баз даних

    opens com.example.fitnessclub to javafx.fxml;
    exports com.example.fitnessclub;
}