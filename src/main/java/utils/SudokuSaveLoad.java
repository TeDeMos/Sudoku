package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SudokuSaveLoad {
    public static boolean load(File file, int[][] level, int[][] state, IntList[][] notes) {
        try {
            String content = Files.readString(file.toPath());
            String[] split = content.split(":");
            if (split.length != 3)
                return false;
            return split2DIntArray(split[0], level) && split2DIntArray(split[1], state) &&
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

    public static boolean save(File file, int[][] level, int[][] state, IntList[][] notes) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(join2DIntArray(level) + ":" + join2DIntArray(state) + ":" + join2DIntListArray(notes));
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

    private static String joinIntListArray(IntList[] array) {
        return Arrays.stream(array).map(IntList::join).collect(Collectors.joining(","));
    }

    private static String joinIntArray(int[] array) {
        return Arrays.stream(array).mapToObj(String::valueOf).collect(Collectors.joining(","));
    }

    public static boolean savePDF(File file, int[][] level) {
        return false;
    }
}