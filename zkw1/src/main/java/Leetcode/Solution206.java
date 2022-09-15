package Leetcode;

public class Solution206 {
    public ListNode reverseList(ListNode head) {

        ListNode pre = null;
        ListNode cur = head;

        while (head!=null){
            ListNode tmp  = cur.next;
            cur.next = pre;
            pre = cur;
            cur= tmp;
        }
        return pre;
    }


}
