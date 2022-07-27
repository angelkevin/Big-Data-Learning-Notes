package Leetcode;
// 快慢指针
public class Soultion26 {
    public int removeDuplicates(int[] nums) {
        int slow = 0;
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];

            }

        }
        return slow + 1;
    }
}
