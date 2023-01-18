package utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Generator {
    private final IntList[][] availableNumbers;
    private final Random random;
    private final int[][] result;
    private final boolean[][] blocked;
    private final boolean solve;

    private Generator() {
        result = new int[9][9];
        blocked = new boolean[9][9];
        availableNumbers = new IntList[9][9];
        random = new Random();
        solve = false;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                availableNumbers[i][j] = new IntList();
    }

    private Generator(int[][] level) {
        result = new int[9][9];
        blocked = new boolean[9][9];
        availableNumbers = new IntList[9][9];
        random = new Random();
        solve = true;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                result[i][j] = level[i][j];
                blocked[i][j] = level[i][j] > 0;
                availableNumbers[i][j] = new IntList();
            }

    }

    public static void main(String[] args) {
        int[][] result = generateRandomFilled(true);
        for (int[] ints : result) {
            System.out.println(Arrays.toString(ints));
        }
        int[][] solved = solve(result);
        for (int[] ints : solved) {
            System.out.println(Arrays.toString(ints));
        }
    }

    public static int[][] generateRandomFilled(boolean hard) {
        Generator g = new Generator();
        g.generate();
        g.removeSpaces(hard ? 56 : /*40*/10);
        return g.result;
    }

    public static int[][] solve(int[][] level) {
        Generator g = new Generator(level);
        g.generate();
        return g.result;
    }

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

    private void removeSpaces(int amount) {
        IntList indicesLeft = IntList.fromRange(0, 81);
        for (int i = 0; i < amount; i++) {
            int chosen = indicesLeft.get(random.nextInt(indicesLeft.size()));
            indicesLeft.removeValue(chosen);
            result[chosen % 9][chosen / 9] = 0;
        }
    }

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