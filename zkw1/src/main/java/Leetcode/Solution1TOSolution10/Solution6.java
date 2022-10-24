package Leetcode.Solution1TOSolution10;

import Leetcode.ListNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Solution6 {
    public ListNode mergeKLists(ListNode[] lists) {
        List<ListNode> l = new ArrayList<>();
        for (ListNode tmp : lists) {
            while (tmp != null) {
                l.add(tmp);
                tmp = tmp.next;
            }
        }
        l.sort(new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val - o2.val;
            }
        });
        ListNode head,t;
        if (l.size()!=0){
            t = head=l.get(0);
        }
        else return null;
        for(int i = 1;i < l.size();i ++){
            t.next=l.get(i);
            t = l.get(i);
        }
        return head;
    }
}
