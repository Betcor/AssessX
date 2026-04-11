package com.assessx.assessx.controller;

import com.assessx.assessx.api.ApiClient;
import com.assessx.assessx.api.ApiException;
import com.assessx.assessx.session.SessionManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;

public class GitHubOAuthController {

    @FXML private WebView  webView;
    @FXML private Label    statusLabel;
    @FXML private ProgressIndicator spinner;

    @FXML
    public void initialize() {
        WebEngine engine = webView.getEngine();

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.RUNNING) {
                spinner.setVisible(true);
                statusLabel.setText("Завантаження...");
            } else if (newState == Worker.State.SUCCEEDED) {
                spinner.setVisible(false);
                checkForToken(engine);
            } else if (newState == Worker.State.FAILED) {
                spinner.setVisible(false);
                statusLabel.setText("Помилка завантаження. Перевірте з'єднання з сервером.");
            }
        });

        String authUrl = ApiClient.get().oauthGitHubUrl();
        statusLabel.setText("Відкриваємо GitHub...");

        engine.load(authUrl);
    }

    private void checkForToken(WebEngine engine) {
        String url = engine.getLocation();

        String pageText = (String) engine.executeScript(
                "document.body ? document.body.innerText : ''"
        );

        if (pageText == null || pageText.isBlank()) return;

        try {
            String trimmed = pageText.trim();
            if (!trimmed.startsWith("{")) return;

            JsonObject json = JsonParser.parseString(trimmed).getAsJsonObject();

            if (json.has("token")) {
                String token = json.get("token").getAsString();
                handleTokenReceived(token);
            }
        } catch (Exception ignored) {
            // skip
        }
    }

    private void handleTokenReceived(String token) {
        statusLabel.setText("Авторизація успішна! Завантажуємо профіль...");
        spinner.setVisible(true);
        webView.setVisible(false);

        SessionManager.get().setToken(token);

        Thread.ofVirtual().start(() -> {
            try {
                ApiClient.get().fetchMe();
                Platform.runLater(this::navigateNext);
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    statusLabel.setText("Помилка отримання профілю: " + e.getMessage());
                });
            }
        });
    }

    private void navigateNext() {
        SessionManager s = SessionManager.get();
        String nextFxml;

        nextFxml = "/fxml/complete_registration.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nextFxml));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/sign_up.css").toExternalForm());

            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            statusLabel.setText("Помилка навігації: " + e.getMessage());
        }
    }
}
