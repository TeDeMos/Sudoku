package com.example.sudoku;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SudokuColors {
    private static final String vBoxStyleLight = "-fx-background-color: white;";
    private static final String vBoxStyleDark = "-fx-background-color: black;";
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
        double top = y % 3 == 0 ? 2 : 1;
        double bottom = y % 3 == 2 ? 2 : 1;
        double left = x % 3 == 0 ? 2 : 1;
        double right = x % 3 == 2 ? 2 : 1;
        return new Border(new BorderStroke(darkMode ? Color.rgb(100, 100, 100) : Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
    }

    public static Border getButtonBorder(boolean darkMode) {
        //TODO
        return null;
    }

    public static Background getButtonBackground(boolean darkMode) {
        //TODO
        return null;
    }
}