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
import utils.IntList;
import utils.SudokuGenerator;
import utils.SudokuSaveLoad;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

public class SudokuController {
    public VBox mainVbox;
    public GridPane labelsGrid;
    public HBox numberButtons;
    public HBox controlButtonsA;
    public HBox controlButtonsB;
    public Button bEasy;
    public Button bHard;
    public Button bSolve;
    public Button bPrint;
    public Button bLoad;
    public Button bSave;
    public Button bHelp;
    public Button bDark;
    private Stage stage;
    /**
     * Tablica zawierające wszystkie wygenerowane labelki
     */
    private Label[][] labels;
    /**
     * Tablica przechowująca notatki
     */
    private IntList[][] notes;
    /**
     * Binding dla labelek określający wielkość trzcionki cyfry
     */
    private StringExpression labelFontSize;
    /**
     * Binding dla labelek określający wielkość trzcionki notatek
     */
    private StringExpression labelNoteFontSize;
    /**
     * Czy można edytować planszę
     */
    private boolean playing;
    /**
     * Czy można zapisywać i drukować planzsę
     */
    private boolean gameLoaded;
    /**
     * Pozycja x wybranego pola
     */
    private int xSelected;
    /**
     * Pozycja y wybranego pola
     */
    private int ySelected;
    /**
     * Obecny stan planszy
     */
    private int[][] gameState;
    /**
     * Rozwiązanie obecnej planszy
     */
    private int[][] solution;
    /**
     * Macierz wskazująca, które pola są niezmienne
     */
    private boolean[][] blocked;
    /**
     * Czy pokazane jest rozwiązanie
     */
    private boolean solutionShown;
    /**
     * Czy włączony jest tryb ciemny
     */
    private boolean darkMode;

    /**
     * Znajduję duplikaty w tablicy, zawsze dodając też 0
     *
     * @param array tablica do znalezienia duplikatów
     * @return HashSet liczb całkowitych zawierający duplikaty i 0
     */
    private static HashSet<Integer> findDuplicates(int[] array) {
        HashSet<Integer> used = new HashSet<>();
        HashSet<Integer> duplicates = new HashSet<>();
        for (int i : array)
            if (!used.add(i))
                duplicates.add(i);
        duplicates.add(0);
        return duplicates;
    }

    /**
     * Binduje przyciski i labelki
     *
     * @param node      node do zbindowania
     * @param width     szerokość nodu
     * @param height    wysokość nodu
     * @param style     styl nodu
     * @param minHeight czy bindować też minimalną wysokość (labelki z notatkami rozjeżdżają się bez tego)
     */
    private static void bindNode(Node node, NumberBinding width, NumberBinding height, StringExpression style,
            boolean minHeight) {
        Region r = (Region)node;
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

    /**
     * Generowanie wszystkich nodów
     */
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
            labelsGrid.addColumn(i, nodes);
        }
        for (int i = 0; i < 10; i++) {
            Button b = new Button(i > 0 ? String.valueOf(i) : "\uD83D\uDD19");
            b.idProperty().setValue(String.format("B%s", i));
            b.setOnMouseClicked(this::numberButtonClick);
            b.setOnAction(this::numberButtonAction);
            numberButtons.getChildren().add(b);
        }
    }

    /**
     * Zablokowanie edycji i pokazanie Alertu z gratulacjami
     */
    private void showWin() {
        playing = false;
        gameLoaded = false;
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

    /**
     * Zaznaczenie nowego pola i odznaczenie starego
     *
     * @param x pozycja x pola
     * @param y pozycja y pola
     */
    private void selectCell(int x, int y) {
        Background unselected = SudokuColors.getUnselectedColor(darkMode);
        Background selectedMain = SudokuColors.getSelectedColor(darkMode, false);
        Background selectedSecondary = SudokuColors.getSelectedColor(darkMode, true);
        setBackgrounds(xSelected, ySelected, unselected, unselected);
        setBackgrounds(x, y, selectedMain, selectedSecondary);
        ySelected = y;
        xSelected = x;
    }

    /**
     * Zmiana tła labelek
     *
     * @param x         pozycja x pola
     * @param y         pozycja y pola
     * @param main      kolor pola na tej pozycji
     * @param secondary kolor pól, które zaznaczą się gdy będzie wybrana tamta pozycja (kolumna, rząd, kwadrat)
     */
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

    /**
     * Wstawia cyfrę
     *
     * @param x            pozycja x pola
     * @param y            pozycja y pola
     * @param number       cyfra do wstawienia (0 usuwa)
     * @param force        czy ignorować zablokowanie pola
     * @param checkInvalid czy znaleźć niedozwolone pola po dodaniu cyfry
     */
    private void setNumber(int x, int y, int number, boolean force, boolean checkInvalid) {
        if (!force && blocked[x][y])
            return;
        gameState[x][y] = number;
        displayNumber(x, y, number);
        notes[x][y].clear();
        if (checkInvalid)
            detectInvalid(false);
    }

    /**
     * Wyświetla cyfrę bez zmiany stanu planszy
     *
     * @param x      pozycja x pola
     * @param y      pozycja y pola
     * @param number cyfra do wyświetlenia
     */
    private void displayNumber(int x, int y, int number) {
        labels[x][y].setText(number > 0 ? String.valueOf(number) : "");
        labels[x][y].styleProperty().bind(labelFontSize);
    }

    /**
     * Zmienia notatkę
     *
     * @param x      pozycja x pola
     * @param y      pozycja y pola
     * @param number cyfra do dodania / usunięcia (0 usuwa wszystkie)
     */
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

    /**
     * Wyświetla notatki
     *
     * @param x pozycja x pola
     * @param y pozycja y pola
     */
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

    /**
     * Zaznacza pola z duplikatami cyfr
     *
     * @param blockWinDetection czy nie sprawdzać wygranej
     */
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

    /**
     * Zaznacza pola z duplikatami w danym wierszu
     *
     * @param row indeks wiersza do sprawdzenia
     * @return czy wiersz zawiera wszystkie cyfry od 1 do 9
     */
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

    /**
     * Zaznacza pola z duplikatami w danej kolumnie
     *
     * @param column indeks kolumny do sprawdzenia
     * @return czy kolumna zawiera wszystkie cyfry od 1 do 9
     */
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

    /**
     * Zaznacza pola z duplikatami w danym kwadracie
     *
     * @param x pozycja x kwadratu (0-2)
     * @param y pozycja y kwadratu (0-2)
     * @return czy kwadrat zawiera wszystkie cyfry od 1 do 9
     */
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

    /**
     * Ładuje nową planszę i rozpoczyna nową grę
     *
     * @param state   plansza do załadowania
     * @param blocked zablokowane pola (gdy null zablokowane staną się wszystkie pola na którym są cyfry)
     */
    private void loadGame(int[][] state, boolean[][] blocked) {
        int[][] level = new int[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                setNumber(i, j, state[i][j], true, false);
                this.blocked[i][j] = blocked == null ? state[i][j] > 0 : blocked[i][j];
                level[i][j] = this.blocked[i][j] ? state[i][j] : 0;
            }
        solution = SudokuGenerator.solve(level);
        playing = true;
        gameLoaded = true;
        bSolve.setText("Rozwiązanie");
        selectCell(0, 0);
        detectInvalid(true);
    }

    /**
     * Ładuje notatki
     *
     * @param notes notatki
     */
    private void loadNotes(IntList[][] notes) {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                for (int n : notes[i][j])
                    setNote(i, j, n);
    }

    /**
     * Wyświetla Alert z błędem
     *
     * @param header  header Alertu
     * @param content content Alertu
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Bład");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    /**
     * Pokazuje lub ukrywa rozwiązanie
     */
    private void showHideSolution() {
        if (!gameLoaded)
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

    /**
     * Obsługuje kliknięcie myszą na labelkę
     *
     * @param e MouseEvent
     */
    private void gridMouseClick(MouseEvent e) {
        if (!playing)
            return;
        int pos = Integer.parseInt(((Label)e.getSource()).getId().substring(1));
        int x = pos / 10;
        int y = pos % 10;
        selectCell(x, y);
    }

    /**
     * Obsługuję kliknięcie prawym przyciskiem na przyciski z cyframi (wstawienie notatki)
     *
     * @param e MouseEvent
     */
    private void numberButtonClick(MouseEvent e) {
        if (!playing || e.getButton() != MouseButton.SECONDARY)
            return;
        setNote(xSelected, ySelected, Integer.parseInt(((Button)e.getSource()).getId().substring(1)));
    }

    /**
     * Obsługuje wciśnięcie przycisku z cyframi (wstawienie cyfry)
     *
     * @param e MouseEvent
     */
    private void numberButtonAction(ActionEvent e) {
        if (!playing)
            return;
        int number = Integer.parseInt(((Button)e.getSource()).getId().substring(1));
        setNumber(xSelected, ySelected, gameState[xSelected][ySelected] == number ? 0 : number, false, true);
    }

    @FXML
    private void controlAButtonPressed(ActionEvent e) {
        String id = ((Button)e.getSource()).getId();
        if (id.equals("bSolve"))
            showHideSolution();
        else {
            Optional<ButtonType> result = Optional.empty();
            if (gameLoaded) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Uwaga!");
                alert.setHeaderText("Uwaga");
                alert.setContentText("Czy na pewno chcesz zacząć poziom o innej trudności?");
                result = alert.showAndWait();
            }
            if (!gameLoaded || result.isPresent() && result.get() == ButtonType.OK)
                loadGame(SudokuGenerator.generateRandomFilled(id.equals("bHard")), null);
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
                if (!gameLoaded)
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
                    loadGame(loadState, loadBlocked);
                    loadNotes(loadNotes);
                } else {
                    showError("Błąd w ładowaniu pliku",
                            "Upewnij się, że ładujesz niezmodyfikowany plik z zapisem stanu gry");
                }
            }
            case "bPrint" -> {
                if (!gameLoaded)
                    return;
                selected = chooser.showSaveDialog(stage);
                if (selected == null || !SudokuSaveLoad.savePDF(selected, blocked, gameState)) {
                    showError("Błąd w zapisie pliku",
                            "Upewnij się, że wybrałeś prawidłową ścieżkę, która nie ma ograniczonego dostępu");
                }
            }
        }
    }

    /**
     * Obsługuję klawiaturę
     *
     * @param e KeyEvent
     */
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
        bDark.setText(darkMode ? "\u263C" : "\u263E");
    }

    /**
     * Binduje wszystkie nody, żeby poprawnie zmieniały wielkość przy zmiany wielkości okna
     *
     * @param stage Stage przekazany z SudokuApplication
     */
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
        for (HBox hBox : new HBox[] {controlButtonsA, controlButtonsB}) {
            ObservableList<Node> children = hBox.getChildren();
            for (int i = 0; i < 3; i++)
                bindNode(children.get(i), controlButtonWidth, buttonHeight, buttonFontSize, false);
            bindNode(children.get(3), buttonHeight, buttonHeight, buttonFontSize, false);
        }
        for (Node n : numberButtons.getChildren())
            bindNode(n, buttonHeight, buttonHeight, buttonFontSize, false);
        for (Node n : labelsGrid.getChildren())
            bindNode(n, labelSide, labelSide, labelFontSize, true);
    }
}