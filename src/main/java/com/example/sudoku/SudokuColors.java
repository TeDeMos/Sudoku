package com.example.sudoku;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SudokuColors {
    private static final String vBoxStyleLight = "-fx-background-color: white;";
    private static final String vBoxStyleDark = "-fx-background-color: rgb(40, 40, 40);";
    private static final Background whiteBackground =
            new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background darkestGrayBackground =
            new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background darkerGrayBackground =
            new Background(new BackgroundFill(Color.rgb(60, 60, 60), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background darkGrayBackground =
            new Background(new BackgroundFill(Color.rgb(90, 90, 90), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background grayBackground =
            new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background lightGrayBackground =
            new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Border[][] labelBordersLight;
    private static final Border[][] labelBordersDark;

    static {
        labelBordersLight = new Border[9][9];
        labelBordersDark = new Border[9][9];
        for (int i = 0; i < 3; i++) {
            double left = i == 0 ? 2 : 1;
            double right = i == 2 ? 2 : 1;
            for (int j = 0; j < 3; j++) {
                double top = j == 0 ? 2 : 1;
                double bottom = j == 2 ? 2 : 1;
                labelBordersLight[i][j] = new Border(
                        new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                                new BorderWidths(top, right, bottom, left)));
                labelBordersDark[i][j] = new Border(
                        new BorderStroke(Color.rgb(100, 100, 100), BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                                new BorderWidths(top, right, bottom, left)));
            }
        }
    }

    public static String getVBoxStyle(boolean darkMode) {
        return darkMode ? vBoxStyleDark : vBoxStyleLight;
    }

    public static Background getUnselectedColor(boolean darkMode) {
        return darkMode ? darkestGrayBackground : whiteBackground;
    }

    public static Background getSelectedColor(boolean darkMode, boolean secondary) {
        if (secondary)
            return darkMode ? darkerGrayBackground : lightGrayBackground;
        return darkMode ? darkGrayBackground : grayBackground;
    }

    public static Color getFontColor(boolean darkMode, boolean blocked, boolean invalid) {
        if (invalid) {
            if (blocked)
                return Color.DARKRED; //any, blocked, invalid
            return Color.RED; //any, regular, invalid
        }
        if (blocked) {
            if (darkMode)
                return Color.GRAY; //dark, blocked, valid
            return Color.BLACK; //light, blocked, valid
        }
        if (darkMode)
            return Color.WHITE; //dark, regular, valid
        return Color.DARKBLUE; //light, regular, valid
    }

    public static Border getLabelBorder(boolean darkMode, int x, int y) {
        return darkMode ? labelBordersDark[x % 3][y % 3] : labelBordersLight[x % 3][y % 3];
    }
}