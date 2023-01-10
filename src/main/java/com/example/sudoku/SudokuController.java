package com.example.sudoku;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.Generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SudokuController {
    private static final Background unselectedColor =
            new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background selectedMainColor =
            new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background selectedSecondaryColor =
            new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Color regularFontColor = Color.DARKBLUE;
    private static final Color lockedFontColor = Color.BLACK;
    private static final Color regularInvalidFontColor = Color.RED;
    private static final Color lockedInvalidFontColor = Color.DARKRED;
    public GridPane mainGrid;
    public HBox numbers;
    public HBox control;
    private Label[][] labels;
    private int yPrevClicked;
    private int xPrevClicked;
    private int[][] gameState;
    private boolean[][] blocked;

    @FXML
    public void initialize() {
        gameState = new int[9][9];
        blocked = new boolean[9][9];
        Node[] nodes = new Node[9];
        labels = new Label[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Label l = new Label();
                double top = j % 3 == 0 ? 2 : 1;
                double bottom = j % 3 == 2 ? 2 : 1;
                double left = i % 3 == 0 ? 2 : 1;
                double right = i % 3 == 2 ? 2 : 1;
                l.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                        new BorderWidths(top, right, bottom, left))));
                l.alignmentProperty().setValue(Pos.CENTER);
                l.onMouseClickedProperty().setValue(this::gridMouseClick);
                l.idProperty().setValue(String.format("L%s%s", i, j));
                l.setTextFill(regularFontColor);
                nodes[j] = l;
                labels[i][j] = l;
            }
            mainGrid.addColumn(i, nodes);
        }
        for (int i = 0; i < 10; i++) {
            Button b = new Button(i > 0 ? String.valueOf(i) : "\uD83D\uDD19");
            b.idProperty().setValue(String.format("B%s", i));
            b.setOnAction(this::numberButtonClick);
            numbers.getChildren().add(b);
        }
    }

    public void gridMouseClick(MouseEvent e) {
        int pos = Integer.parseInt(((Label)e.getSource()).getId().substring(1));
        int x = pos / 10;
        int y = pos % 10;
        selectCell(x, y);
    }

    public void numberButtonClick(ActionEvent e) {
        int number = Integer.parseInt(((Button)e.getSource()).getId().substring(1));
        setNumber(xPrevClicked, yPrevClicked, number);
    }

    public void keyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code.isDigitKey()) {
            setNumber(xPrevClicked, yPrevClicked, Integer.parseInt(code.getChar()));
            return;
        }
        switch (e.getCode()) {
            case A -> selectCell(Math.max(xPrevClicked - 1, 0), yPrevClicked);
            case D -> selectCell(Math.min(xPrevClicked + 1, 8), yPrevClicked);
            case W -> selectCell(xPrevClicked, Math.max(yPrevClicked - 1, 0));
            case S -> selectCell(xPrevClicked, Math.min(yPrevClicked + 1, 8));
            case BACK_SPACE -> setNumber(xPrevClicked, yPrevClicked, 0);
        }
    }

    private void setNumber(int x, int y, int number) {
        if (blocked[x][y] || number < 0 || number > 9)
            return;
        gameState[x][y] = number;
        labels[x][y].setText(number > 0 ? String.valueOf(number) : "");
        detectInvalid();
    }

    private void selectCell(int x, int y) {
        setBackgrounds(xPrevClicked, yPrevClicked, unselectedColor, unselectedColor);
        setBackgrounds(x, y, selectedMainColor, selectedSecondaryColor);
        yPrevClicked = y;
        xPrevClicked = x;
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

    private void detectInvalid() {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                setFontColor(i, j, false);
        for (int i = 0; i < 9; i++) {
            detectInvalidColumn(i);
            detectInvalidInRow(i);
            detectInvalidSquare(i / 3, i % 3);
        }

    }

    private void detectInvalidInRow(int row) {
        int[] rowArray = new int[9];
        for (int i = 0; i < 9; i++)
            rowArray[i] = gameState[i][row];
        HashSet<Integer> duplicates = findDuplicates(rowArray);
        for (int i = 0; i < 9; i++) {
            if (duplicates.contains(gameState[i][row]))
                setFontColor(i, row, true);
        }
    }

    private void detectInvalidColumn(int column) {
        HashSet<Integer> duplicates = findDuplicates(gameState[column]);
        for (int i = 0; i < 9; i++) {
            if (duplicates.contains(gameState[column][i]))
                setFontColor(column, i, true);
        }
    }

    private void detectInvalidSquare(int x, int y) {
        int[] square = new int[9];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                square[i * 3 + j] = gameState[x * 3 + i][y * 3 + j];
        HashSet<Integer> duplicates = findDuplicates(square);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (duplicates.contains(gameState[x * 3 + i][y * 3 + j]))
                    setFontColor(x * 3 + i, y * 3 + j, true);
    }

    private static HashSet<Integer> findDuplicates(int[] array) {
        HashSet<Integer> used = new HashSet<>();
        HashSet<Integer> duplicates = new HashSet<>();
        for (int i : array)
            if (!used.add(i))
                duplicates.add(i);
        return duplicates;
    }

    private void setFontColor(int x, int y, boolean invalid) {
        if (invalid)
            labels[x][y].setTextFill(blocked[x][y] ? lockedInvalidFontColor : regularInvalidFontColor);
        else
            labels[x][y].setTextFill(blocked[x][y] ? lockedFontColor : regularFontColor);
    }

    private void loadLevel(int[][] state) {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                setNumber(i, j, state[i][j]);
                blocked[i][j] = state[i][j] > 0;
                setFontColor(i, j, false);
            }
    }

    @FXML
    private void easyPressed(ActionEvent e) {
        loadLevel(Generator.generateRandomFilled());
    }

    public void setBinding(Stage stage) {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed);
        ReadOnlyDoubleProperty width = stage.widthProperty();
        ReadOnlyDoubleProperty height = stage.heightProperty();
        NumberBinding gridSide = Bindings.min(width.subtract(20), height.subtract(50).multiply(9).divide(11));
        NumberBinding labelSide = gridSide.divide(9).add(1);
        NumberBinding buttonHeight = gridSide.subtract(110).divide(10);
        NumberBinding bottomButtonWidth = gridSide.subtract(40).divide(4);
        StringExpression buttonFontSize = Bindings.concat("-fx-font-size: ", buttonHeight.multiply(2).divide(5), ";");
        StringExpression labelFontSize = Bindings.concat("-fx-font-size: ", labelSide.multiply(2).divide(3), ";");
        for (Node n : control.getChildren()) {
            Button b = (Button)n;
            b.prefWidthProperty().bind(bottomButtonWidth);
            b.prefHeightProperty().bind(buttonHeight);
            b.styleProperty().bind(buttonFontSize);
        }
        for (Node n : numbers.getChildren()) {
            Button b = (Button)n;
            b.prefWidthProperty().bind(buttonHeight);
            b.prefHeightProperty().bind(buttonHeight);
            b.styleProperty().bind(buttonFontSize);
        }
        for (Node n : mainGrid.getChildren()) {
            Label l = (Label)n;
            l.prefWidthProperty().bind(labelSide);
            l.prefHeightProperty().bind(labelSide);
            l.styleProperty().bind(labelFontSize);
        }
    }
}