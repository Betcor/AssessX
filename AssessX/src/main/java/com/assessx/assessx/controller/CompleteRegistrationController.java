package com.assessx.assessx.controller;

import com.assessx.assessx.api.ApiClient;
import com.assessx.assessx.api.ApiException;
import com.assessx.assessx.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CompleteRegistrationController {

    @FXML private TextField nameField;
    @FXML private ToggleGroup roleToggleGroup;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton teacherRadio;
    @FXML private VBox       groupBox;
    @FXML private TextField  groupField;
    @FXML private Label      groupError;
    @FXML private Label      nameError;
    @FXML private Label      errorLabel;
    @FXML private Button     submitButton;
    @FXML private ProgressIndicator spinner;

    @FXML
    public void initialize() {
        String name = SessionManager.get().getName();
        if (name != null) nameField.setText(name);

        studentRadio.selectedProperty().addListener((obs, old, selected) -> {
            groupBox.setVisible(selected);
            groupBox.setManaged(selected);
        });

        studentRadio.setSelected(true);
    }

    @FXML
    protected void onSubmit() {
        clearErrors();

        String name = nameField.getText().trim();
        boolean isStudent = studentRadio.isSelected();

        String groupText = groupField.getText().trim();

        boolean valid = true;

        if (name.isBlank()) {
            showError(nameError, "Введіть ім'я");
            valid = false;
        }

        if (isStudent && groupText.isBlank()) {
            showError(groupError, "Введіть номер групи");
            valid = false;
        }

        if (!valid) return;

        String role = isStudent ? "STUDENT" : "TEACHER";

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("role", role);

        setLoading(true);

        if (isStudent) {
            submitWithGroup(payload, groupText);
        } else {
            submitPayload(payload);
        }
    }

    private void submitWithGroup(Map<String, Object> payload, String groupName) {
        Thread.ofVirtual().start(() -> {
            try {
                var groups = ApiClient.get().getGroups();
                Long groupId = null;

                for (var g : groups) {
                    if (g.get("name").getAsString().equals(groupName)) {
                        groupId = g.get("id").getAsLong();
                        break;
                    }
                }
                if (groupId != null) {
                    payload.put("groupId", groupId);
                }
                Platform.runLater(() -> submitPayload(payload));
            } catch (ApiException e) {
                Platform.runLater(() -> submitPayload(payload));
            }
        });
    }

    private void submitPayload(Map<String, Object> payload) {
        Thread.ofVirtual().start(() -> {
            try {
                ApiClient.get().completeRegistration(payload);
                ApiClient.get().fetchMe();
                Platform.runLater(this::goToDashboard);
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    showError(errorLabel, "Помилка: " + e.getMessage());
                });
            }
        });
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            setLoading(false);
            showError(errorLabel, "Навігація: " + e.getMessage());
        }
    }

    private void clearErrors() {
        hideError(nameError);
        hideError(groupError);
        hideError(errorLabel);
    }

    private void showError(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideError(Label label) {
        label.setText("");
        label.setVisible(false);
        label.setManaged(false);
    }

    private void setLoading(boolean loading) {
        spinner.setVisible(loading);
        spinner.setManaged(loading);
        submitButton.setDisable(loading);
    }
}
