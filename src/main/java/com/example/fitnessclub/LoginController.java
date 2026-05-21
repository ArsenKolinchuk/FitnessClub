package com.example.fitnessclub;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        // Валідація на порожні поля на рівні інтерфейсу
        if (email.isEmpty()) {
            showError("Будь ласка, введіть Email");
            return;
        }
        if (password.isEmpty()) {
            showError("Будь ласка, введіть пароль");
            return;
        }
        String query = "SELECT ID_Administrator, Full_name, Password FROM Administrator WHERE Email = ?";
        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connect.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String dbPassword = resultSet.getString("Password");
                if (password.equals(dbPassword)) {
                    String adminName = resultSet.getString("Full_name");
                    errorLabel.setVisible(false);
                    System.out.println("Вхід успішний! Вітаємо, " + adminName);
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
                            Parent root = loader.load();
                            Scene mainScene = new Scene(root, 900, 600);
                            Stage currentStage = (Stage) loginButton.getScene().getWindow();
                            currentStage.setScene(mainScene);
                            currentStage.setTitle("Power Gym - Панель керування");
                            currentStage.centerOnScreen();
                            currentStage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showError("Не вдалося завантажити головне вікно!");
                        }
                    });
                } else {
                    showError("Невірний пароль!");
                }
            } else {
                showError("Невірний email або користувача не існує");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Помилка підключення до бази даних!");
        }
    }
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}