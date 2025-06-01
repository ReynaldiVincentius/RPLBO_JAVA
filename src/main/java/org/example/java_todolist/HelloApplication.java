package org.example.java_todolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/java_todolist/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("SmartTask");
        stage.setScene(scene);

        stage.setMaximized(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
