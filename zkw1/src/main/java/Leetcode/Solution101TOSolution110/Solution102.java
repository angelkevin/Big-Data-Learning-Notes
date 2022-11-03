package Leetcode.Solution101TOSolution110;

import Leetcode.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Solution102 {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> arrayLists = new ArrayList<>();

        Queue<TreeNode> queue = new LinkedList<>();
        if (root == null) {
            return arrayLists;
        }
        queue.add(root);
        while (!queue.isEmpty()) {
            List<Integer> list1 = new ArrayList<>();
            int length = queue.size();
            for (int i = 0; i < length; i++) {
                TreeNode node = queue.remove();
                list1.add(node.val);
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {

                    queue.add(node.right);
                }

            }
            arrayLists.add(list1);

        }
        return arrayLists;
    }
}
