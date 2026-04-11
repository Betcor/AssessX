package com.assessx.assessx.controller;

import com.assessx.assessx.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private Label    userLabel;
    @FXML private StackPane contentPane;
    @FXML private VBox      teacherSection;

    @FXML private Button btnDashboard;
    @FXML private Button btnAssignments;
    @FXML private Button btnTests;
    @FXML private Button btnPractices;
    @FXML private Button btnResults;
    @FXML private Button btnGroups;
    @FXML private Button btnAllResults;

    private Button activeBtn;

    @FXML
    public void initialize() {
        SessionManager s = SessionManager.get();

        String role = s.isTeacher() ? "Викладач" : "Студент";
        userLabel.setText(s.getName() + " · " + role);

        if (s.isTeacher()) {
            teacherSection.setVisible(true);
            teacherSection.setManaged(true);
        }

        activeBtn = btnDashboard;
        showDashboard();
    }

    @FXML public void showDashboard()   { loadPage("/fxml/pages/home.fxml",        btnDashboard); }
    @FXML public void showAssignments() { loadPage("/fxml/pages/assignments.fxml", btnAssignments); }
    @FXML public void showTests()       { loadPage("/fxml/pages/tests.fxml",       btnTests); }
    @FXML public void showPractices()   { loadPage("/fxml/pages/practices.fxml",   btnPractices); }
    @FXML public void showResults()     { loadPage("/fxml/pages/results.fxml",     btnResults); }
    @FXML public void showGroups()      { loadPage("/fxml/pages/groups.fxml",      btnGroups); }
    @FXML public void showAllResults()  { loadPage("/fxml/pages/all_results.fxml", btnAllResults); }

    @FXML
    public void onLogout() {
        SessionManager.get().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlPath, Button btn) {
        if (activeBtn != null) {
            activeBtn.getStyleClass().remove("active");
        }
        btn.getStyleClass().add("active");
        activeBtn = btn;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node page = loader.load();

            contentPane.getChildren().setAll(page);
        } catch (IOException e) {
            Label err = new Label("Помилка завантаження: " + fxmlPath + "\n" + e.getMessage());
            err.setStyle("-fx-text-fill: #f85149; -fx-font-size: 14px; -fx-wrap-text: true;");

            contentPane.getChildren().setAll(err);
        }
    }
}
