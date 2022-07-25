package Leetcode;

public class Solution21 {

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode l1 = new ListNode(0);
        ListNode head = l1;
        while (list1 != null && list2 != null) {
            if (list1.val > list2.val) {
                head.next = list2;
                head = head.next;
                list2 = list2.next;
            } else {
                head.next = list1;
                head = head.next;
                list1 = list1.next;
            }
        }
        if (list1 == null) {
            head.next = list2;
        } else {
            head.next = list1;
        }
        return l1.next;

    }
}
