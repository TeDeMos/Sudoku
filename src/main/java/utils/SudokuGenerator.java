package utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class SudokuGenerator {
    /**
     * Tablica dostępnych cyfr dla każdego pola
     */
    private final IntList[][] availableNumbers;
    /**
     * Zmienna losowa
     */
    private final Random random;
    /**
     * Wynik generowania lub rozwiązywania
     */
    private final int[][] result;
    /**
     * Zablokowane pola
     */
    private final boolean[][] blocked;
    /**
     * Czy rozwiązuje zamiast generować nową planszę
     */
    private final boolean solve;

    /**
     * Konstruktor generatora nowego poziomu
     */
    private SudokuGenerator() {
        result = new int[9][9];
        blocked = new boolean[9][9];
        availableNumbers = new IntList[9][9];
        random = new Random();
        solve = false;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                availableNumbers[i][j] = new IntList();
    }

    /**
     * Konstruktor generatora rozwiązania
     *
     * @param game plansza do rozwiązania
     */
    private SudokuGenerator(int[][] game) {
        result = new int[9][9];
        blocked = new boolean[9][9];
        availableNumbers = new IntList[9][9];
        random = new Random();
        solve = true;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                result[i][j] = game[i][j];
                blocked[i][j] = game[i][j] > 0;
                availableNumbers[i][j] = new IntList();
            }

    }

    public static void main(String[] args) {
        int[][] result = generateRandomFilled(true);
        for (int[] ints : result) {
            System.out.println(Arrays.toString(ints));
        }
        System.out.println();
        int[][] solved = solve(result);
        for (int[] ints : solved) {
            System.out.println(Arrays.toString(ints));
        }
    }

    /**
     * Generuje nowa planszę o danym poziomie trudności
     *
     * @param hard poziom trudności
     * @return wygenerowaną plansza
     */
    public static int[][] generateRandomFilled(boolean hard) {
        SudokuGenerator g = new SudokuGenerator();
        g.generate();
        g.removeSpaces(hard ? 50 : 35);
        return g.result;
    }

    /**
     * Generuje rozwiązanie planszy
     *
     * @param game plansza do rozwiązania
     * @return rozwiązana plansza
     */
    public static int[][] solve(int[][] game) {
        SudokuGenerator g = new SudokuGenerator(game);
        g.generate();
        return g.result;
    }

    /**
     * Generuje plansze / rozwiązanie
     */
    private void generate() {
        int x = 0, y = 0;
        findAvailable(0, 0);
        while (x != 9) {
            if (availableNumbers[x][y].isEmpty() || blocked[x][y] && !availableNumbers[x][y].contains(result[x][y])) {
                if (!blocked[x][y])
                    result[x][y] = 0;
                if (--y == -1) {
                    y = 8;
                    x--;
                }
            } else {
                int chosen;
                if (solve)
                    chosen = blocked[x][y] ? result[x][y] : availableNumbers[x][y].get(0);
                else
                    chosen = availableNumbers[x][y].get(random.nextInt(availableNumbers[x][y].size()));
                result[x][y] = chosen;
                availableNumbers[x][y].removeValue(chosen);
                if (++y == 9) {
                    y = 0;
                    x++;
                }
                findAvailable(x, y);
            }
        }
    }

    /**
     * Usuwa określoną ilość pól
     *
     * @param amount ilość pól do usunięcia
     */
    private void removeSpaces(int amount) {
        IntList indicesLeft = IntList.fromRange(0, 81);
        for (int i = 0; i < amount; i++) {
            int chosen = indicesLeft.get(random.nextInt(indicesLeft.size()));
            indicesLeft.removeValue(chosen);
            result[chosen % 9][chosen / 9] = 0;
        }
    }

    /**
     * Znajduję dostępne cyfry dla danego pola
     *
     * @param x pozycja x pola
     * @param y pozycja y pola
     */
    private void findAvailable(int x, int y) {
        if (x > 8 || y > 8)
            return;
        HashSet<Integer> used = new HashSet<>();
        availableNumbers[x][y].clear();
        for (int i = 0; i < 9; i++) {
            if (i != y)
                used.add(result[x][i]);
            if (i != x)
                used.add(result[i][y]);
            availableNumbers[x][y].add(i + 1);
        }
        int xSquareStart = x / 3 * 3, ySquareStart = y / 3 * 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (xSquareStart + i != x && ySquareStart + j != y)
                    used.add(result[xSquareStart + i][ySquareStart + j]);
        availableNumbers[x][y].removeAll(used);
    }
}