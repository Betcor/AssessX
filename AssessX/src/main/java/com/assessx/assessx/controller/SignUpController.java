package com.assessx.assessx.controller;

import com.assessx.assessx.dto.SignUpRequest;
import com.assessx.assessx.utils.SignUpValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class SignUpController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField groupField;
    @FXML private TextField emailField;

    @FXML private Label firstNameError;
    @FXML private Label lastNameError;
    @FXML private Label groupError;
    @FXML private Label emailError;

    private final SignUpValidator validator = new SignUpValidator();

    @FXML
    protected void onSignUpClick() {
        SignUpRequest user = new SignUpRequest(
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            groupField.getText().trim(),
            emailField.getText().trim()
        );

        Map<String, String> errors = validator.validate(user);

        renderErrors(errors);

        if (errors.isEmpty()) {
            System.out.println(
                "Sign up: %s / %s / %s / %s"
                    .formatted(user.firstName(), user.lastName(), user.groupId(), user.email())
            );

            clearFields();
        }
    }

    private void renderErrors(Map<String, String> errors) {
        setError(firstNameError, errors.get("firstName"));
        setError(lastNameError, errors.get("lastName"));
        setError(groupError, errors.get("groupId"));
        setError(emailError, errors.get("email"));
    }

    private void setError(Label label, String message) {
        if (message == null) {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
        } else {
            label.setText(message);
            label.setVisible(true);
            label.setManaged(true);
        }
    }

    @FXML
    protected void onSignInClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
            Stage stage = (Stage) firstNameField.getScene().getWindow();

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        groupField.clear();
        emailField.clear();
    }
}
