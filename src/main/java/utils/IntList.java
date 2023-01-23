package utils;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntList extends ArrayList<Integer> {
    /**
     * Tworzy IntListę z tekstu
     *
     * @param joined tekst do zamiany (spacja odpowiada pustej IntLiście)
     * @return IntListę
     * @throws IllegalArgumentException jeśli tablica zawiera nie zawiera tylko cyfr od 1 do 9
     */
    public static IntList fromJoined(String joined) throws IllegalArgumentException {
        if (joined.equals(" "))
            return new IntList();
        String[] split = joined.split("\\.");
        IntList result = new IntList();
        for (String s : split) {
            int value = Integer.parseInt(s);
            if (value < 1 || value > 9)
                throw new IllegalArgumentException();
            result.add(value);
        }
        return result;
    }

    /**
     * Tworzy IntListę zawierającą wszystkie cyfry z przedziału
     *
     * @param start początek przedziału
     * @param end   koniec przedziału
     * @return IntListę
     */
    public static IntList fromRange(int start, int end) {
        return IntStream.range(start, end).boxed().collect(Collectors.toCollection(IntList::new));
    }

    /**
     * Zamienia Intlistę na tekst
     *
     * @return tekst (spacja jeśli IntLista jest pusta)
     */
    public String join() {
        return size() > 0 ? stream().map(String::valueOf).collect(Collectors.joining(".")) : " ";
    }

    /**
     * Usuwa określoną liczbę
     *
     * @param i liczba do usunięcia (niepotrzeba boksować)
     */
    public void removeValue(int i) {
        remove(Integer.valueOf(i));
    }
}