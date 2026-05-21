module com.example.fitnessclub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 

    opens com.example.fitnessclub to javafx.fxml;
    exports com.example.fitnessclub;
}
