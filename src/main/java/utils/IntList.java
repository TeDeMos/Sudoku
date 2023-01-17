package utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IntList extends ArrayList<Integer> {
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

    public String join() {
        return size() > 0 ? stream().map(String::valueOf).collect(Collectors.joining(".")) : " ";
    }

    public boolean removeValue(int i) {
        return remove(Integer.valueOf(i));
    }
}