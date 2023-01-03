package com.example.sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SudokuApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SudokuApplication.class.getResource("sudoku-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ((SudokuController)fxmlLoader.getController()).setBinding(stage);
        stage.setTitle("Sudoku");
        stage.setMinHeight(520);
        stage.setMinWidth(420);
        stage.setScene(scene);
        stage.show();
    }
}