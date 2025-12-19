package com.biblioteca;

import com.biblioteca.utils.LogManager;
import com.biblioteca.utils.NotificationThread;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal da aplicação Biblioteca Digital.
 * @author Sistema Biblioteca Digital
 * @version 1.0
 */
public class App extends Application {
    
    private static Stage primaryStage;
    private static NotificationThread notificationThread;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        LogManager.info("Iniciando aplicação Biblioteca Digital");
        
        // Iniciar thread de notificações
        notificationThread = new NotificationThread();
        notificationThread.start();
        
        // Carregar tela de login
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setTitle("Biblioteca Digital - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public static void changeScene(String fxml, String title, int width, int height) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource("/fxml/" + fxml));
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(App.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setTitle("Biblioteca Digital - " + title);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            LogManager.error("Erro ao trocar cena", e);
        }
    }
    
    public static Stage getPrimaryStage() { return primaryStage; }
    
    @Override
    public void stop() {
        if (notificationThread != null) notificationThread.stopThread();
        LogManager.info("Aplicação encerrada");
        LogManager.close();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
