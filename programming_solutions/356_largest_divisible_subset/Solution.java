import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public List<Integer> largestDivisibleSubset(int[] nums) {
        Arrays.sort(nums);
        int elementsCount = nums.length;
        int maxIndex = elementsCount - 1;

        int[] nextIndexes = new int[elementsCount];
        int[] subSetSizesFromIndexes = new int[elementsCount];

        nextIndexes[maxIndex] = -1;
        subSetSizesFromIndexes[maxIndex] = 1;

        for (int k = maxIndex - 1; k >=0; k--) {
            int element = nums[k];
            int subSetSizeFromCurrentIndex = 1;
            int nextIndexFromCurrentIndex = -1;

            // Find divisible elements from k-th index
            for (int j = k + 1; j < elementsCount; j++) {
                if (nums[j] % element == 0) {
                    int tmp = subSetSizesFromIndexes[j];
                    if (1 + tmp > subSetSizeFromCurrentIndex) {
                        subSetSizeFromCurrentIndex = 1 + tmp;
                        nextIndexFromCurrentIndex = j;
                    }
                }
            }

            // Detect best subset starting from current index
            nextIndexes[k] = nextIndexFromCurrentIndex;
            subSetSizesFromIndexes[k] = subSetSizeFromCurrentIndex;
        }

        // Find the best index to start with
        int bestStartIndex = 0;
        int maxDivisibleSubsetSize = -1;
        for(int i = 0; i < elementsCount; i++) {
            int tmp = subSetSizesFromIndexes[i];
            if (tmp > maxDivisibleSubsetSize) {
                maxDivisibleSubsetSize = tmp;
                bestStartIndex = i;
            }
        }

        // Collect results
        List<Integer> results = new ArrayList<>(maxDivisibleSubsetSize);
        int currentIndex = bestStartIndex;
        while(currentIndex != -1) {
            results.add(nums[currentIndex]);
            currentIndex = nextIndexes[currentIndex];
        }
        
        return results;
    }
}