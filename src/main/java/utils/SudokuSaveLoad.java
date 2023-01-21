package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SudokuSaveLoad {
    public static boolean load(File file, boolean[][] blocked, int[][] state, IntList[][] notes) {
        try {
            String content = Files.readString(file.toPath());
            String[] split = content.split(":");
            if (split.length != 3)
                return false;
            return split2DBoolArray(split[0], blocked) && split2DIntArray(split[1], state) &&
                    split2DIntListArray(split[2], notes);
        } catch (IOException ex) {
            return false;
        }
    }

    private static boolean split2DIntListArray(String s, IntList[][] array) {
        String[] split = s.split(";");
        if (split.length != 9)
            return false;
        for (int i = 0; i < 9; i++)
            if (!splitIntListArray(split[i], array[i]))
                return false;
        return true;
    }

    private static boolean split2DIntArray(String s, int[][] array) {
        String[] split = s.split(";");
        if (split.length != 9)
            return false;
        for (int i = 0; i < 9; i++)
            if (!splitIntArray(split[i], array[i]))
                return false;
        return true;
    }

    private static boolean split2DBoolArray(String s, boolean[][] array) {
        String[] split = s.split(";");
        if (split.length != 9)
            return false;
        for (int i = 0; i < 9; i++)
            if (!splitBoolArray(split[i], array[i]))
                return false;
        return true;
    }

    private static boolean splitIntListArray(String s, IntList[] array) {
        String[] split = s.split(",");
        if (split.length != 9)
            return false;
        for (int i = 0; i < 9; i++)
            try {
                array[i] = IntList.fromJoined(split[i]);
            } catch (IllegalArgumentException e) {
                return false;
            }
        return true;
    }

    private static boolean splitIntArray(String s, int[] array) {
        String[] split = s.split(",");
        if (split.length != 9)
            return false;
        for (int i = 0; i < 9; i++)
            try {
                array[i] = Integer.parseInt(split[i]);
                if (array[i] < 0 || array[i] > 9)
                    return false;
            } catch (NumberFormatException e) {
                return false;
            }
        return true;
    }

    private static boolean splitBoolArray(String s, boolean[] array) {
        String[] split = s.split(",");
        if (split.length != 9)
            return false;
        for (int i = 0; i < 9; i++)
            try {
                int value = Integer.parseInt(split[i]);
                if (value == 0)
                    array[i] = false;
                else if (value == 1)
                    array[i] = true;
                else
                    return false;
            } catch (NumberFormatException e) {
                return false;
            }
        return true;
    }

    public static boolean save(File file, boolean[][] blocked, int[][] state, IntList[][] notes) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(join2DBoolArray(blocked) + ":" + join2DIntArray(state) + ":" + join2DIntListArray(notes));
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        }
    }

    private static String join2DIntListArray(IntList[][] array) {
        return Arrays.stream(array).map(SudokuSaveLoad::joinIntListArray).collect(Collectors.joining(";"));
    }

    private static String join2DIntArray(int[][] array) {
        return Arrays.stream(array).map(SudokuSaveLoad::joinIntArray).collect(Collectors.joining(";"));
    }

    private static String join2DBoolArray(boolean[][] array) {
        return Arrays.stream(array).map(SudokuSaveLoad::joinBoolArray).collect(Collectors.joining(";"));
    }

    private static String joinIntListArray(IntList[] array) {
        return Arrays.stream(array).map(IntList::join).collect(Collectors.joining(","));
    }

    private static String joinIntArray(int[] array) {
        return Arrays.stream(array).mapToObj(String::valueOf).collect(Collectors.joining(","));
    }

    private static String joinBoolArray(boolean[] array) {
        return IntStream.range(0, array.length).mapToObj(x -> array[x] ? "1" : "0").collect(Collectors.joining(","));
    }

    public static boolean savePDF(int[][] level) {
        try {
            PrintWriter writer = new PrintWriter("sudoku.txt");
            for (int i = 0; i < level.length; i++) {
                for (int j = 0; j < level[i].length; j++) {
                    writer.print(level[j][i] + " ");
                }
                writer.println();
            }
            writer.close();
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "PDF.jar");
            pb.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}