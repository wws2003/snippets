import java.util.Comparator;
import java.util.PriorityQueue;

class MedianFinder {

    private PriorityQueue<Integer> lowerPartAscQueue = new PriorityQueue<Integer>(Comparator.reverseOrder());
    private PriorityQueue<Integer> upperPartDescQueue = new PriorityQueue<Integer>();
    
    private Integer middleVal = null;
    
    private int numCount = 0;

    public MedianFinder() {
        
    }
    
    public void addNum(int num) {
        // Special cases for simplicity
        if (numCount == 0) {
            // First element
            middleVal = num;
            numCount = 1;
            return;
        }
        if (numCount == 1) {
            // Second element
            lowerPartAscQueue.add(Math.min(middleVal, num));
            upperPartDescQueue.add(Math.max(middleVal, num));
            middleVal = null;
            numCount = 2;
            return;
        }
        
        if (numCount % 2 == 0) {
            // There is being an even number of elements
            numCount++;
            int lowerMax = lowerPartAscQueue.peek();
            int upperMin = upperPartDescQueue.peek();
            if (num <= lowerMax) {
                lowerPartAscQueue.add(num);
                middleVal = lowerPartAscQueue.poll();
                return;
            }
            if (lowerMax < num && num < upperMin) {
                middleVal = num;
                return;
            }
            if (num >= upperMin) {
                upperPartDescQueue.add(num);
                middleVal = upperPartDescQueue.poll();
                return;
            }
        } else {
            // There is being an odd number of elements
            numCount++;
            int lowerMax = lowerPartAscQueue.peek();
            int upperMin = upperPartDescQueue.peek();
            if (num <= lowerMax) {
                lowerPartAscQueue.add(num);
                upperPartDescQueue.add(middleVal);
                middleVal = null;
                return;
            }
            if (lowerMax < num && num < upperMin) {
                lowerPartAscQueue.add(Math.min(middleVal, num));
                upperPartDescQueue.add(Math.max(middleVal, num));
                middleVal = null;
                return;
            }
            if (num >= upperMin) {
                lowerPartAscQueue.add(middleVal);
                upperPartDescQueue.add(num);
                middleVal = null;
                return;
            }
        }
    }
    
    public double findMedian() {
        if (numCount % 2 == 0) {
            // Skip validation check for now
            return ((double)lowerPartAscQueue.peek() + (double)upperPartDescQueue.peek()) / 2;
        } else {
            return middleVal;
        }
    }
}