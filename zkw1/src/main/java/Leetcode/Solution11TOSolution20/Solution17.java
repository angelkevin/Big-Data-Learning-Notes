package Leetcode.Solution11TOSolution20;

import java.util.ArrayList;
import java.util.List;

public class Solution17 {
    public static List<String> letterCombinations(String digits) {
        String[] strings = new String[digits.length()];
        StringBuilder list = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        if (digits == null || digits.length() == 0) {
            return result;
        }
        String[] mappings = new String[]{"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        for (int i = 0; i < digits.length(); i++) {
            int i1 = Integer.parseInt(digits.substring(i, i + 1));
            strings[i] = mappings[i1];

        }
        show(strings, list, result, 0);
        return result;

    }

    public static void show(String[] s, StringBuilder list, ArrayList<String> result, int num) {
        if (s.length == list.length()) {
            result.add(list.toString());
            return;

        }
        for (int i = 0; i < s[num].length(); i++) {
            list.append(s[num].charAt(i));
            show(s, list, result, num + 1);
            list.deleteCharAt(list.length() - 1);
        }

    }

    public static void main(String[] args) {
        System.out.println(letterCombinations("23"));
    }
}

