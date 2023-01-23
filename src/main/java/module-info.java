module com.example.sudoku {
    requires javafx.controls;
    requires javafx.fxml;
    //requires itextpdf;

    opens com.example.sudoku to javafx.fxml;
    exports com.example.sudoku;
}