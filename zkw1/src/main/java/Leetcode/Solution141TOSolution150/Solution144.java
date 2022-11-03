package Leetcode.Solution141TOSolution150;

import Leetcode.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class Solution144 {


    void traversal(TreeNode root, List<Integer> result){
        if (root == null){
            return;
        }
        result.add(root.val);
        traversal(root.left,result);
        traversal(root.right,result);
    }




    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<Integer>();
        traversal(root,result);
        return result;
    }
}
