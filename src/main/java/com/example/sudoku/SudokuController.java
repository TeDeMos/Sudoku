package com.example.sudoku;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.Generator;
import utils.IntList;
import utils.SudokuSaveLoad;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

public class SudokuController {
    public GridPane mainGrid;
    public HBox numbers;
    public HBox controlA;
    public HBox controlB;
    public Button bEasy;
    public Button bHard;
    public Button bSolve;
    public Button bPrint;
    public Button bLoad;
    public Button bSave;
    public Button bHelp;
    public Button bDark;
    public VBox mainVbox;
    private Label[][] labels;
    private IntList[][] notes;
    private Stage stage;

    private StringExpression labelFontSize;
    private StringExpression labelNoteFontSize;

    private boolean playing;
    private boolean levelLoaded;
    private int xSelected;
    private int ySelected;
    private int[][] gameState;
    private int[][] solution;
    private boolean[][] blocked;
    private boolean solutionShown;
    private boolean darkMode;

    private static HashSet<Integer> findDuplicates(int[] array) {
        HashSet<Integer> used = new HashSet<>();
        HashSet<Integer> duplicates = new HashSet<>();
        for (int i : array)
            if (!used.add(i))
                duplicates.add(i);
        duplicates.add(0);
        return duplicates;
    }

    private static void bindNode(Node button, NumberBinding width, NumberBinding height, StringExpression style,
            boolean minHeight) {
        Region r = (Region)button;
        r.styleProperty().bind(style);
        r.prefWidthProperty().bind(width);
        r.prefHeightProperty().bind(height);
        if (minHeight)
            r.minHeightProperty().bind(height);
    }

    @FXML
    public void initialize() {
        gameState = new int[9][9];
        blocked = new boolean[9][9];
        notes = new IntList[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                notes[i][j] = new IntList();
        createNodes();
    }

    private void createNodes() {
        Node[] nodes = new Node[9];
        labels = new Label[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Label l = new Label();
                l.setBorder(SudokuColors.getLabelBorder(darkMode, i, j));
                l.alignmentProperty().setValue(Pos.CENTER);
                l.onMouseClickedProperty().setValue(this::gridMouseClick);
                l.idProperty().setValue(String.format("L%s%s", i, j));
                l.setTextFill(SudokuColors.getFontColor(darkMode, false, false));
                nodes[j] = l;
                labels[i][j] = l;
            }
            mainGrid.addColumn(i, nodes);
        }
        for (int i = 0; i < 10; i++) {
            Button b = new Button(i > 0 ? String.valueOf(i) : "\uD83D\uDD19");
            b.idProperty().setValue(String.format("B%s", i));
            b.setOnMouseClicked(this::numberButtonClick);
            b.setOnAction(this::numberButtonAction);
            numbers.getChildren().add(b);
        }
    }

    private void showWin() {
        playing = false;
        levelLoaded = false;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                labels[i][j].setTextFill(SudokuColors.getFontColor(darkMode, true, false));
        Background unselected = SudokuColors.getUnselectedColor(darkMode);
        setBackgrounds(xSelected, ySelected, unselected, unselected);
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Wygrana");
        a.setHeaderText("Wygrana!");
        a.setContentText("Gratulacje");
        a.show();
    }

    private void selectCell(int x, int y) {
        Background unselected = SudokuColors.getUnselectedColor(darkMode);
        Background selectedMain = SudokuColors.getSelectedColor(darkMode, false);
        Background selectedSecondary = SudokuColors.getSelectedColor(darkMode, true);
        setBackgrounds(xSelected, ySelected, unselected, unselected);
        setBackgrounds(x, y, selectedMain, selectedSecondary);
        ySelected = y;
        xSelected = x;
    }

    private void setBackgrounds(int x, int y, Background main, Background secondary) {
        for (int i = 0; i < 9; i++) {
            labels[x][i].setBackground(secondary);
            labels[i][y].setBackground(secondary);
        }
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                labels[x / 3 * 3 + i][y / 3 * 3 + j].setBackground(secondary);
        labels[x][y].setBackground(main);
    }

    private void setNumber(int x, int y, int number, boolean force, boolean checkInvalid) {
        if (!force && blocked[x][y])
            return;
        gameState[x][y] = number;
        displayNumber(x, y, number);
        notes[x][y].clear();
        if (checkInvalid)
            detectInvalid(false);
    }

    private void displayNumber(int x, int y, int number) {
        labels[x][y].setText(number > 0 ? String.valueOf(number) : "");
        labels[x][y].styleProperty().bind(labelFontSize);
    }

    private void setNote(int x, int y, int number) {
        if (blocked[x][y])
            return;
        if (gameState[x][y] != 0) {
            gameState[x][y] = 0;
            detectInvalid(false);
        }
        if (number == 0)
            notes[x][y].clear();
        else if (notes[x][y].contains(number))
            notes[x][y].removeValue(number);
        else {
            notes[x][y].add(number);
            Collections.sort(notes[x][y]);
        }
        displayNotes(x, y);
    }

    private void displayNotes(int x, int y) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < notes[x][y].size(); i++) {
            builder.append(notes[x][y].get(i));
            if (i % 3 == 2)
                builder.append('\n');
            else
                builder.append(' ');
        }
        labels[x][y].setText(builder.toString());
        labels[x][y].styleProperty().bind(labelNoteFontSize);
    }

    private void detectInvalid(boolean blockWinDetection) {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                labels[i][j].setTextFill(SudokuColors.getFontColor(darkMode, blocked[i][j], false));
        boolean win = true;
        for (int i = 0; i < 9; i++)
            if (detectInvalidColumn(i) | detectInvalidRow(i) | detectInvalidSquare(i / 3, i % 3))
                win = false;
        if (!blockWinDetection && win)
            showWin();
    }

    private boolean detectInvalidRow(int row) {
        int[] rowArray = new int[9];
        boolean result = false;
        for (int i = 0; i < 9; i++)
            rowArray[i] = gameState[i][row];
        HashSet<Integer> duplicates = findDuplicates(rowArray);
        for (int i = 0; i < 9; i++)
            if (duplicates.contains(gameState[i][row])) {
                labels[i][row].setTextFill(
                        SudokuColors.getFontColor(darkMode, blocked[i][row], gameState[i][row] != 0));
                result = true;
            }
        return result;
    }

    private boolean detectInvalidColumn(int column) {
        HashSet<Integer> duplicates = findDuplicates(gameState[column]);
        boolean result = false;
        for (int i = 0; i < 9; i++)
            if (duplicates.contains(gameState[column][i])) {
                labels[column][i].setTextFill(
                        SudokuColors.getFontColor(darkMode, blocked[column][i], gameState[column][i] != 0));
                result = true;
            }
        return result;
    }

    private boolean detectInvalidSquare(int x, int y) {
        int[] square = new int[9];
        for (int i = 0; i < 3; i++)
            System.arraycopy(gameState[x * 3 + i], y * 3, square, i * 3, 3);
        boolean result = false;
        HashSet<Integer> duplicates = findDuplicates(square);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                int cellX = 3 * x + i, cellY = 3 * y + j;
                if (duplicates.contains(gameState[cellX][cellY])) {
                    labels[cellX][cellY].setTextFill(
                            SudokuColors.getFontColor(darkMode, blocked[cellX][cellY], gameState[cellX][cellY] != 0));
                    result = true;
                }
            }
        return result;
    }

    private void loadLevel(int[][] state, boolean[][] blocked) {
        int[][] level = new int[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                setNumber(i, j, state[i][j], true, false);
                this.blocked[i][j] = blocked == null ? state[i][j] > 0 : blocked[i][j];
                level[i][j] = this.blocked[i][j] ? state[i][j] : 0;
            }
        solution = Generator.solve(level);
        playing = true;
        levelLoaded = true;
        bSolve.setText("Rozwiązanie");
        selectCell(0, 0);
        detectInvalid(true);
    }

    private void loadNotes(IntList[][] notes) {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                for (int n : notes[i][j])
                    setNote(i, j, n);
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Bład");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private void swapSolution() {
        if (!levelLoaded)
            return;
        solutionShown = !solutionShown;
        if (solutionShown) {
            playing = false;
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    displayNumber(i, j, solution[i][j]);
            bSolve.setText("Ukryj");
            Background unselected = SudokuColors.getUnselectedColor(darkMode);
            setBackgrounds(xSelected, ySelected, unselected, unselected);
        } else {
            playing = true;
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    if (notes[i][j].isEmpty())
                        displayNumber(i, j, gameState[i][j]);
                    else
                        displayNotes(i, j);
            bSolve.setText("Rozwiązanie");
            selectCell(xSelected, ySelected);
        }
    }

    private void gridMouseClick(MouseEvent e) {
        if (!playing)
            return;
        int pos = Integer.parseInt(((Label)e.getSource()).getId().substring(1));
        int x = pos / 10;
        int y = pos % 10;
        selectCell(x, y);
    }

    private void numberButtonClick(MouseEvent e) {
        if (!playing)
            return;
        int number = Integer.parseInt(((Button)e.getSource()).getId().substring(1));
        if (e.getButton() == MouseButton.PRIMARY)
            setNumber(xSelected, ySelected, gameState[xSelected][ySelected] == number ? 0 : number, false, true);
        else if (e.getButton() == MouseButton.SECONDARY)
            setNote(xSelected, ySelected, number);
    }

    private void numberButtonAction(ActionEvent e){
        if (!playing)
            return;
        int number = Integer.parseInt(((Button)e.getSource()).getId().substring(1));
        setNumber(xSelected, ySelected, gameState[xSelected][ySelected] == number ? 0 : number, false, true);
    }

    @FXML
    private void controlAButtonPressed(ActionEvent e) {
        String id = ((Button)e.getSource()).getId();
        if (id.equals("bSolve"))
            swapSolution();
        else {
            Optional<ButtonType> result = null;
            if (levelLoaded) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Uwaga!");
                alert.setHeaderText("Uwaga");
                alert.setContentText("Czy na pewno chcesz zacząć poziom o innej trudności?");
                result = alert.showAndWait();
            }
            if (!levelLoaded || result.isPresent() && result.get() == ButtonType.OK)
                loadLevel(Generator.generateRandomFilled(id.equals("bHard")), null);
        }
    }

    @FXML
    private void controlBButtonPressed(ActionEvent e) {
        String id = ((Button)e.getSource()).getId();
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter;
        if (id.equals("bPrint")) {
            filter = new FileChooser.ExtensionFilter("Plik pdf", "*.pdf");
            chooser.setInitialFileName("sudoku.pdf");
        } else {
            filter = new FileChooser.ExtensionFilter("Plik tekstowy ze stanem gry", "*.txt");
            chooser.setInitialFileName("sudoku.txt");
        }
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selected;
        switch (id) {
            case "bSave" -> {
                if (!levelLoaded)
                    return;
                selected = chooser.showSaveDialog(stage);
                if (selected == null || !SudokuSaveLoad.save(selected, blocked, gameState, notes)) {
                    showError("Błąd w zapisie pliku",
                            "Upewnij się, że wybrałeś prawidłową ścieżkę, która nie ma ograniczonego dostępu");
                }
            }
            case "bLoad" -> {
                selected = chooser.showOpenDialog(stage);
                boolean[][] loadBlocked = new boolean[9][9];
                int[][] loadState = new int[9][9];
                IntList[][] loadNotes = new IntList[9][9];
                if (selected != null && SudokuSaveLoad.load(selected, loadBlocked, loadState, loadNotes)) {
                    loadLevel(loadState, loadBlocked);
                    loadNotes(loadNotes);
                } else {
                    showError("Błąd w ładowaniu pliku",
                            "Upewnij się, że ładujesz niezmodyfikowany plik z zapisem stanu gry");
                }
            }
            case "bPrint" -> {
                if (!levelLoaded)
                    return;
                selected = chooser.showSaveDialog(stage);
                if (selected == null || !SudokuSaveLoad.savePDF(selected, blocked, gameState)) {
                    showError("Błąd w zapisie pliku",
                            "Upewnij się, że wybrałeś prawidłową ścieżkę, która nie ma ograniczonego dostępu");
                }
            }
        }
    }

    private void keyPressed(KeyEvent e) {
        if (!playing)
            return;
        KeyCode code = e.getCode();
        if (code.isDigitKey()) {
            String name = code.getName();
            int number = Integer.parseInt(name.substring(name.length() - 1));
            if (e.isShiftDown())
                setNote(xSelected, ySelected, number);
            else
                setNumber(xSelected, ySelected, gameState[xSelected][ySelected] == number ? 0 : number, false, true);
            return;
        }
        switch (e.getCode()) {
            case A -> selectCell(Math.max(xSelected - 1, 0), ySelected);
            case D -> selectCell(Math.min(xSelected + 1, 8), ySelected);
            case W -> selectCell(xSelected, Math.max(ySelected - 1, 0));
            case S -> selectCell(xSelected, Math.min(ySelected + 1, 8));
            case BACK_SPACE -> setNumber(xSelected, ySelected, 0, false, true);
        }
    }
    @FXML
    public void helpPressed(ActionEvent ignored) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Pomoc");
        a.setHeaderText("Instrukcja gry:");
        a.setContentText("""
                Celem gry jest wstawienie cyfr w pola, żeby w każdej kolumnie, każdym rzędzie i każdym wyróżnionym kwadracie trzy na trzy znajdywały się wszystkie cyfry od 1 do 9 włącznie.
                                
                Sterowanie myszą:
                -Zaznaczenie pola - kliknięcie na pole
                -Wpisanie / usunięcie cyfry - kliknięcie lewym przyciskiem na przycisk z cyfrą / znakiem backspace
                -Wpisanie / usunięcie notatki - kliknięcie prawym przyciskiem na przycisk z cyfrą / znakiem backspace
                                
                Sterowanie klawiaturą:
                -Przesunięcie zaznaczenia - przyciski W (w górę), A (w lewo), S (w dół), D (w prawo)
                -Wpisanie / usunięcie cyfry - przyciski od 1 do 9 / backspace lub 0
                -Wpisanie / usunięcie notataki - przyciski od 1 do 9 z wciśniętym shiftem / backspace lub 0
                """);
        a.show();
    }
    @FXML
    public void darkModePressed(ActionEvent ignored) {
        darkMode = !darkMode;
        mainVbox.setStyle(SudokuColors.getVBoxStyle(darkMode));
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                labels[i][j].setBackground(SudokuColors.getUnselectedColor(darkMode));
                labels[i][j].setBorder(SudokuColors.getLabelBorder(darkMode, i, j));
            }
        detectInvalid(true);
        bDark.setText(darkMode ? "\u263C" : "\u263E" );
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setBinding(Stage stage) {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed);
        this.stage = stage;
        ReadOnlyDoubleProperty width = stage.widthProperty();
        ReadOnlyDoubleProperty height = stage.heightProperty();
        NumberBinding gridSide = Bindings.min(width.subtract(20), height.subtract(50).multiply(3).divide(4));
        NumberBinding labelSide = gridSide.divide(9).add(1);
        NumberBinding buttonHeight = gridSide.subtract(90).divide(10);
        NumberBinding controlButtonWidth = gridSide.subtract(25).subtract(buttonHeight).divide(3);
        StringExpression buttonFontSize = Bindings.concat("-fx-font-size: ", buttonHeight.multiply(2).divide(5), ";");
        labelFontSize = Bindings.concat("-fx-font-size: ", labelSide.multiply(2).divide(3), ";");
        labelNoteFontSize = Bindings.concat("-fx-font-size: ", labelSide.divide(5), ";");
        for (HBox hBox : new HBox[] {controlA, controlB}) {
            ObservableList<Node> children = hBox.getChildren();
            for (int i = 0; i < 3; i++)
                bindNode(children.get(i), controlButtonWidth, buttonHeight, buttonFontSize, false);
            bindNode(children.get(3), buttonHeight, buttonHeight, buttonFontSize, false);
        }
        for (Node n : numbers.getChildren())
            bindNode(n, buttonHeight, buttonHeight, buttonFontSize, false);
        for (Node n : mainGrid.getChildren())
            bindNode(n, labelSide, labelSide, labelFontSize, true);
    }
}