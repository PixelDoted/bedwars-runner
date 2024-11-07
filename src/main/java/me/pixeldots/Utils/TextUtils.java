package me.pixeldots.Utils;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static String[] getStringWithin(String text, String start, String end) {
        String[] s = text.split(start);
        List<String> output = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            output.add(s[i].substring(0, s[i].indexOf(end)));
        }
        return output.toArray(new String[output.size()]);
    }

    public static String getStringIndex(String s, int index) {
        return s.toUpperCase().substring(index, index+1);
    }

    public static String upperCaseFirst(String s) {
        return s.toUpperCase().substring(0, 1) + s.substring(1, s.length());
    }
    
}
