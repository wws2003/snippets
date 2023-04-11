import java.util.ArrayList;
import java.util.List;

public class Solution {

    @SuppressWarnings("unchecked")
    public int[] topKFrequent(int[] nums, int k) {
        final int MAX_VAL = 10000;

        int maxRange = 2 * MAX_VAL;
        int[] freqOverEles = new int[maxRange + 1];

        int minEleKey = Integer.MAX_VALUE;
        int maxEleKey = Integer.MIN_VALUE;
        int maxFreq = Integer.MIN_VALUE;

        for (int n : nums) {
            int eleKey = n + MAX_VAL;
            freqOverEles[eleKey] += 1;

            // Narrowing the search scope
            if (eleKey < minEleKey) {
                minEleKey = eleKey;
            }
            if (eleKey > maxEleKey) {
                maxEleKey = eleKey;
            }
            if (freqOverEles[eleKey] > maxFreq) {
                maxFreq = freqOverEles[eleKey];
            }
        }

        ArrayList<Integer>[] elesOverFreqs = new ArrayList[maxFreq + 1];

        for (int eleKey = minEleKey; eleKey <= maxEleKey; eleKey++) {
            int freq = freqOverEles[eleKey];
            if (freq == 0) {
                continue;
            }

            // Add new element of frequency = freq
            if (elesOverFreqs[freq] == null) {
                elesOverFreqs[freq] = new ArrayList<>();
            }
            int n = eleKey - MAX_VAL;
            elesOverFreqs[freq].add(n);
        }

        List<Integer> topKFreqElements = new ArrayList<>();
        for (int freq = maxFreq; freq >= 0 && topKFreqElements.size() < k; freq--) {
            ArrayList<Integer> elesWithFreq = elesOverFreqs[freq];
            if (elesWithFreq != null) {
                topKFreqElements.addAll(elesWithFreq);
            }
        }

        int[] rets = new int[topKFreqElements.size()];
        for (int i = 0; i < rets.length; i++) {
            rets[i] = topKFreqElements.get(i);
        }
        return rets;
    }

    public static void main(String[] args) {
        System.out.println("wwww");
    }
}
