import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        int totalLen = len1 + len2;

        if (len1 == 0) {
            return getMedian(nums2);
        }

        if (len2 == 0) {
            return getMedian(nums1);
        }

        int roundedHalfLen = totalLen / 2;
        int crossingListMinSize = roundedHalfLen + 1;

        CrossingSortedList crossingSortedList = new CrossingSortedList(nums1, nums2, crossingListMinSize);

        if (totalLen % 2 == 0) {
            int lower = crossingSortedList.get(roundedHalfLen - 1);
            int upper = crossingSortedList.get(roundedHalfLen);
            return (double) (lower + upper) / 2;
        } else {
            return crossingSortedList.get(roundedHalfLen);
        }
    }

    private double getMedian(int[] nums) {
        int totalLen = nums.length;
        int roundedHalfLen = totalLen / 2;
        if (totalLen % 2 == 0) {
            int lower = nums[roundedHalfLen - 1];
            int upper = nums[roundedHalfLen];
            return (double) (lower + upper) / 2;
        } else {
            return nums[roundedHalfLen];
        }
    }

    private class CrossingSortedList {
        private final ListWinder[] mListWinders = new ListWinder[2];
        private final List<DataSegment> mDataSegments = new ArrayList<>();

        public CrossingSortedList(int[] nums1, int[] nums2, int minSize) {
            // Store info
            mListWinders[0] = new ListWinder(nums1);
            mListWinders[1] = new ListWinder(nums2);

            int currentListWinderIndex = 0;

            int totalSize = 0;
            while (totalSize < minSize) {
                int otherListWinderIndex = (currentListWinderIndex == 0) ? 1 : 0;
                ListWinder currentListWinder = mListWinders[currentListWinderIndex];
                ListWinder otherListWinder = mListWinders[otherListWinderIndex];

                int currentElement = currentListWinder.getCurrentElement();

                int lowerBoundIndexInOtherListWinder = otherListWinder
                        .lowerBoundIndexFromCurrentSegment(currentElement);

                if (lowerBoundIndexInOtherListWinder < otherListWinder.getCurrentEleIndex()) {
                    // Move in current list winder until find new element or end
                    int lastIndexOfCurrentElement = currentListWinder.getLastIndexOfCurrentElement();
                    int firstIndexOfLargerElement = lastIndexOfCurrentElement + 1;
                    int step = firstIndexOfLargerElement - currentListWinder.getCurrentEleIndex();

                    // Move forward current (not a good solution though...)
                    moveListWinderAndStoreDataSegment(currentListWinder, step);
                    totalSize += step;

                    // If done -> return
                    if (currentListWinder.getCurrentEleIndex() > currentListWinder.getLastEleIndex()) {
                        storeSegmentForRemainingEles(otherListWinder);
                        return;
                    }

                    continue;
                }
                if ((0 <= lowerBoundIndexInOtherListWinder)
                        && (lowerBoundIndexInOtherListWinder < otherListWinder.getLastEleIndex())) {

                    int currentIndexInOtherListWinder = otherListWinder.getCurrentEleIndex();
                    int stepToMoveInOtherListWinder = lowerBoundIndexInOtherListWinder
                            - currentIndexInOtherListWinder
                            + 1;

                    // Store data segment from other list winder
                    moveListWinderAndStoreDataSegment(otherListWinder, stepToMoveInOtherListWinder);

                    // Switch list winder
                    currentListWinderIndex = otherListWinderIndex;

                    // Update total size
                    totalSize += stepToMoveInOtherListWinder;

                    continue;
                }
                if (lowerBoundIndexInOtherListWinder == otherListWinder.getLastEleIndex()) {
                    // The closing case: Remaining of current list winder is larger than any of
                    // other list winder
                    // -> The remaining of current list winder becomes the final segment
                    // Store data segment from other list winder
                    storeSegmentForRemainingEles(otherListWinder);
                    // Store data segment from current list winder
                    storeSegmentForRemainingEles(currentListWinder);
                    return;
                }
            }
        }

        public int get(int index) {
            int currenAbsoluteIndex = 0;

            for (DataSegment currentSegment : mDataSegments) {
                int currentSegmentSize = currentSegment.getSize();

                int maxAbsoluteIndex = currenAbsoluteIndex + currentSegmentSize - 1;
                if (maxAbsoluteIndex < index) {
                    // Update, need to go further
                    currenAbsoluteIndex = maxAbsoluteIndex + 1;
                    continue;
                }

                // Get the result from current partition
                int relativeIndex = index - currenAbsoluteIndex;
                return currentSegment.getAtRelativeIndex(relativeIndex);
            }

            return 0;
        }

        private void moveListWinderAndStoreDataSegment(ListWinder listWinder, int step) {
            DataSegment dataSegment = listWinder.moveForwardAndGetLastSegment(step);
            mDataSegments.add(dataSegment);
        }

        private void storeSegmentForRemainingEles(ListWinder listWinder) {
            int currentIndexInCurrentListWinder = listWinder.getCurrentEleIndex();
            int lastIndexOfCurrentListWinder = listWinder.getLastEleIndex();

            int stepToMoveInCurrentListWinder = lastIndexOfCurrentListWinder
                    - currentIndexInCurrentListWinder
                    + 1;

            DataSegment dataSegmentFromCurrentListWinder = listWinder
                    .moveForwardAndGetLastSegment(stepToMoveInCurrentListWinder);
            mDataSegments.add(dataSegmentFromCurrentListWinder);
        }
    }

    private class ListWinder {
        private int[] mData;

        private int mCurrentEleIndex = 0;

        public ListWinder(int[] data) {
            mData = data;
        }

        /**
         * Find index of the largest element from current index having value <= val
         * 
         * @param val
         * @return
         */
        public int lowerBoundIndexFromCurrentSegment(int val) {
            int upperBound = Arrays.binarySearch(mData, mCurrentEleIndex, mData.length, val);
            if (upperBound >= 0) {
                // Val is in the data list
                // Small trick here
                while (upperBound >= 0 && mData[upperBound] == val) {
                    upperBound -= 1;
                }
                return upperBound;
            } else {
                // Val is not in the data list
                int insertionIndex = -upperBound - 1;
                return insertionIndex - 1;
            }
        }

        public int getLastIndexOfCurrentElement() {
            return last(mCurrentEleIndex, mData.length - 1, mData[mCurrentEleIndex], mData.length);
        }

        private int last(int low, int high, int x, int n) {
            if (high >= low) {
                int mid = low + (high - low) / 2;
                if ((mid == n - 1 || x < mData[mid + 1])
                        && mData[mid] == x)
                    return mid;
                else if (x < mData[mid])
                    return last(low, (mid - 1), x, n);
                else
                    return last((mid + 1), high, x, n);
            }
            return -1;
        }

        public int getCurrentEleIndex() {
            return mCurrentEleIndex;
        }

        public int getLastEleIndex() {
            return mData.length - 1;
        }

        public DataSegment moveForwardAndGetLastSegment(int step) {
            DataSegment dataSegment = new DataSegment(mData,
                    mCurrentEleIndex,
                    mCurrentEleIndex + step - 1);

            mCurrentEleIndex += step;

            return dataSegment;
        }

        public int getCurrentElement() {
            return mData[mCurrentEleIndex];
        }
    }

    private class DataSegment {
        private int[] mData;
        private int mStartIndex;
        private int mEndIndex;

        public DataSegment(int[] data, int startIndex, int endIndex) {
            mData = data;
            mStartIndex = startIndex;
            mEndIndex = endIndex;
        }

        public int getSize() {
            return mEndIndex - mStartIndex + 1;
        }

        public int getAtRelativeIndex(int relativeIndex) {
            return mData[mStartIndex + relativeIndex];
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        // int[] nums1 = { 1, 3 };
        // int[] nums2 = { 2 };

        // int[] nums1 = { 0, 0 };
        // int[] nums2 = { 0, 0 };

        int[] nums1 = { 1, 2 };
        int[] nums2 = { 3, 4 };

        // int[] nums1 = { 2, 2, 4, 4 };
        // int[] nums2 = { 2, 2, 4, 4 };

        System.out.println(solution.findMedianSortedArrays(nums1, nums2));
    }

}
