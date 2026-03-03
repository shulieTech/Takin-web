package io.shulie.takin.web.app.leqi.main;

import io.shulie.takin.cloud.ext.content.response.Response;

import java.util.*;
import java.util.stream.Collectors;

public class MainMock3 {

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> dataList = new ArrayList();
        if(nums.length < 3) {
            return dataList;
        }
        Arrays.sort(nums);
        for(int i = 0; i < nums.length - 2; i++) {
            if(nums[i] > 0) break;
            int front = i + 1;
            int behind = nums.length - 1;
            while(front < behind) {
                int value = nums[i] + nums[front] + nums[behind];
                if(value == 0) {
                    dataList.add(Arrays.asList(nums[i], nums[front], nums[behind]));
                    while(front + 1 < behind && nums[front] == nums[front + 1]) front++;
                    while(behind - 1 > front && nums[behind] == nums[behind - 1]) behind--;
                }
                if(value < 0) front++;
                if(value > 0) behind--;
            }
        }
        return dataList;
    }

    public static void permute(int[] nums, int start, List<Object> result) {
        if (start >= nums.length) {
            result.add(nums);
            return;
        }
        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);
            permute(nums, start + 1, result);
            swap(nums, start, i); // backtrack
        }
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
    public static void main(String[] args) {
        Response response = new Response<>();
        response.setData(true);
    }
}
