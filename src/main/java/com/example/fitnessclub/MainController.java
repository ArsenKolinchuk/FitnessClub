package com.example.fitnessclub;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainController {

    @FXML
    private TableView<Client> clientTable;

    @FXML
    private TableColumn<Client, Integer> idColumn;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TableColumn<Client, String> phoneColumn;

    @FXML
    private TableColumn<Client, String> emailColumn;

    @FXML
    private TableColumn<Client, Date> regDateColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button addClientButton;

    @FXML
    private Button viewDetailsButton;

    private final ObservableList<Client> clientList = FXCollections.observableArrayList();

    private FilteredList<Client> filteredData;

    @FXML
    public void initialize() {
        System.out.println("Головне вікно успішно завантажено!");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        regDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        filteredData = new FilteredList<>(clientList, p -> true);
        clientTable.setItems(filteredData);
        loadClientData();
        setupSearchFilter();
        addClientButton.setOnAction(event -> openAddClientWindow());
        viewDetailsButton.setOnAction(event -> {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();

            if (selectedClient != null) {
                openClientDetailsWindow(selectedClient);
            } else {
                System.out.println("Будь ласка, спочатку виберіть клієнта з таблиці!");
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, виберіть клієнта з таблиці для перегляду профілю!");
                alert.showAndWait();
            }
        });
    }
    public void loadClientData() {
        clientList.clear();
        String query = "SELECT ID_Client, Full_name, Date_of_birth, Sex, Phone_number, Email, Registration_date FROM Client";
        try (Connection connect = DatabaseConnection.getConnection();
             Statement statement = connect.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Client client = new Client(
                        resultSet.getInt("ID_Client"),
                        resultSet.getString("Full_name"),
                        resultSet.getDate("Date_of_birth"),
                        resultSet.getString("Sex"),
                        resultSet.getString("Phone_number"),
                        resultSet.getString("Email"),
                        resultSet.getDate("Registration_date")
                );
                clientList.add(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Помилка під час зчитування клієнтів з MySQL!");
        }
    }
    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase().trim();
                if (client.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (client.getPhoneNumber().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }
    private void openAddClientWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-client-view.fxml"));
            Parent root = loader.load();
            AddClientController addController = loader.getController();
            addController.setMainController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Реєстрація нового клієнта");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(addClientButton.getScene().getWindow());
            stage.setResizable(false);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Помилка відкриття файлу add-client-view.fxml. Перевірте його наявність у ресурсах!");
        }
    }
    private void openClientDetailsWindow(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-details-view.fxml"));
            Parent root = loader.load();
            ClientDetailsController detailsController = loader.getController();
            detailsController.setClientData(client);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Профіль: " + client.getFullName());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(viewDetailsButton.getScene().getWindow());
            stage.setResizable(false);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Помилка відкриття файлу client-details-view.fxml!");
        }
    }
}