package Leetcode.SwordRefersToAnOffer;

import Leetcode.ListNode;

import java.util.ArrayList;

public class offer06 {
    public int[] reversePrint(ListNode head) {
        ArrayList<Integer> integers = new ArrayList<>();
        while (head!=null){
            integers.add(head.val);
            head=head.next;
        }
        int i = integers.size();
        int[] result = new int[i];
        for (Integer integer : integers) {
            result[i-1]=integer;
            i--;

        }
        return result;

    }
}
