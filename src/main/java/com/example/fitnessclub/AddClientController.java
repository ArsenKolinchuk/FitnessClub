package com.example.fitnessclub;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class AddClientController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private Label errorLabel;
    @FXML private Button cancelButton;

    private MainController mainController;

    @FXML
    public void initialize() {
        sexComboBox.setItems(FXCollections.observableArrayList("Чоловік", "Жінка"));
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    void handleSave(ActionEvent event) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        String sex = sexComboBox.getValue();
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || dob == null || sex == null) {
            showError("Будь ласка, заповніть усі поля!");
            return;
        }
        if (phone.length() != 10) {
            showError("Номер телефону повинен містити 10 цифр!");
            return;
        }
        int newId = 1;
        String maxIdQuery = "SELECT MAX(ID_Client) FROM Client";
        try (Connection connect = DatabaseConnection.getConnection();
             Statement statement = connect.createStatement();
             ResultSet resultSet = statement.executeQuery(maxIdQuery)) {
            if (resultSet.next()) {
                newId = resultSet.getInt(1) + 1; // Беремо максимальний ID і додаємо 1
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Помилка генерації ID!");
            return;
        }
        String insertQuery = "INSERT INTO Client (ID_Client, Full_name, Date_of_birth, Sex, Phone_number, Email, Registration_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connect.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, newId); // Передаємо згенерований ID
            preparedStatement.setString(2, name);
            preparedStatement.setDate(3, java.sql.Date.valueOf(dob));
            preparedStatement.setString(4, sex);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, email);
            preparedStatement.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            preparedStatement.executeUpdate();
            if (mainController != null) {
                mainController.loadClientData();
            }
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Помилка додавання до бази даних!");
        }
    }
    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}