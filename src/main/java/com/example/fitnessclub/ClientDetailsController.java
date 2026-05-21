package com.example.fitnessclub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label bioLabel;
    @FXML private Label subTitleLabel;
    @FXML private Label subDetailsLabel;
    @FXML private Label regDateLabel;
    @FXML private Button closeButton;

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    public void setClientData(Client client) {
        nameLabel.setText(client.getFullName());
        phoneLabel.setText(client.getPhoneNumber());
        emailLabel.setText(client.getEmail());
        bioLabel.setText(client.getDateOfBirth() + " (" + client.getSex() + ")");
        regDateLabel.setText(client.getRegistrationDate().toString());

        loadSubscriptionStatus(client.getId());
    }

    private void loadSubscriptionStatus(int clientId) {
        String query = "SELECT st.Name, s.End_date " +
                "FROM subscription s " +
                "JOIN subscription_type st ON s.ID_SubscriptionType = st.ID_SubscriptionType " +
                "WHERE s.ID_Client = ? AND s.End_date >= CURDATE() " +
                "ORDER BY s.End_date DESC LIMIT 1";
        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connect.prepareStatement(query)) {
            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String subscriptionName = resultSet.getString("Name"); // з таблиці subscription_type
                java.sql.Date endDate = resultSet.getDate("End_date");   // з таблиці subscription

                subTitleLabel.setText("АКТИВНИЙ: " + subscriptionName.toUpperCase());
                subTitleLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                subDetailsLabel.setText("Діє до: " + endDate.toString() + "\nВхід дозволено.");
            } else {

                subTitleLabel.setText("НЕМАЄ АБОНЕМЕНТА");
                subTitleLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                subDetailsLabel.setText("Усі абонементи завершилися або ні разу не купувалися.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            subTitleLabel.setText("Помилка зчитування БД");
            subDetailsLabel.setText("Не вдалося завантажити статус абонемента через помилку SQL.");
        }
    }
}