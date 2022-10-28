package Leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Solution131 {

    public static List<List<String>> partition(String s) {


        List<List<String>> result = new ArrayList<>();
        LinkedList<String> path = new LinkedList<>();
        back(s, result, path, 0);

        return result;
    }

    public static boolean test(String s) {
        if (s.length() == 0) {
            return true;
        }
        int left = 0;
        int right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) == s.charAt(right)) {
                left++;
                right--;
            } else {
                return false;
            }
        }
        return true;

    }

    public static void back(String s, List<List<String>> result, LinkedList<String> path, int start) {
        if (start >= s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < s.length(); i++) {
            if (test(s.substring(start, i + 1))) {
                String str = s.substring(start, i + 1);
                path.add(str);
            } else {
                continue;
            }

            back(s, result, path, i + 1);
            path.removeLast();
        }


    }

    public static void main(String[] args) {
        System.out.println(partition("aab"));
    }
}
