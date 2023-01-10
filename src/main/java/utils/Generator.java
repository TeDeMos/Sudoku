package utils;

import java.util.*;

public class Generator {
    private final ArrayList<HashSet<Integer>> availableNumbers;
    private final Random random;
    private final int[][] result;

    public static void main(String[] args) {
        int[][] result = generateRandomFilled();
        for (int[] ints : result) {
            System.out.println(Arrays.toString(ints));
        }
    }

    public static int[][] generateRandomFilled() {
        Generator g = new Generator();
        g.generate(0, false);
        return g.result;
    }

    private Generator() {
        availableNumbers = new ArrayList<>(81);
        random = new Random();
        for (int i = 0; i < 81; i++)
            availableNumbers.add(new HashSet<>());
        result = new int[9][9];
    }

    private void generate(int index, boolean backtracked) {
        if (!backtracked)
            getAvailable(index);
        int y = index % 9;
        int x = index / 9;
        if (availableNumbers.get(index).isEmpty()) {
            result[x][y] = 0;
            generate(index - 1, true);
        } else {
            ArrayList<Integer> arrayList = new ArrayList<>(availableNumbers.get(index));
            int chosen = arrayList.get(random.nextInt(arrayList.size()));
            result[x][y] = chosen;
            availableNumbers.get(index).remove(chosen);
            if (index < 80)
                generate(index + 1, false);
        }
    }

    private void getAvailable(int index) {
        int y = index % 9;
        int x = index / 9;
        HashSet<Integer> used = new HashSet<>();
        availableNumbers.get(index).clear();
        for (int i = 0; i < 9; i++) {
            used.add(result[x][i]);
            used.add(result[i][y]);
            availableNumbers.get(index).add(i + 1);
        }
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                used.add(result[x / 3 * 3 + i][y / 3 * 3 + j]);
        availableNumbers.get(index).removeAll(used);
    }
}