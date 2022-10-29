#include <vector>
#include <iostream>
#include <cmath>

class Solution
{
public:
    int lastStoneWeightII(std::vector<int> &stones)
    {
        // Sort
        sort(stones.begin(), stones.end(), std::greater<int>());

        // Prepare forward sums
        std::vector<int> forwardSums(stones.size());
        int tmpSum = 0;
        for (int i = stones.size() - 1; i >= 0; i--)
        {
            tmpSum += stones[i];
            forwardSums[i] = tmpSum;
        }

        int tmpResult = -1;
        try
        {
            traverse(stones,
                     0,
                     0,
                     forwardSums,
                     tmpResult);
            return tmpResult;
        }
        catch (const std::runtime_error &e)
        {
            return 0;
        }
    }

private:
    void traverse(const std::vector<int> &stones,
                  int previousSum,
                  int startIndex,
                  const std::vector<int> &forwardSums,
                  int &tmpResult)
    {
        // +stones[startIndex]
        int sumIfAddStartIndex = previousSum + stones[startIndex];

        // -stones[startIndex]
        int sumIfSubtractStartIndex = previousSum - stones[startIndex];

        // Check termination
        if (startIndex == stones.size() - 1)
        {
            int candidateDeltaWeight = std::min(std::abs(sumIfAddStartIndex),
                                                std::abs(sumIfSubtractStartIndex));

            if (tmpResult < 0 || candidateDeltaWeight < tmpResult)
            {
                tmpResult = candidateDeltaWeight;
            }

            return;
        }

        int remainingSum = forwardSums[startIndex + 1];

        // Recursive call
        //// Add stone at startIndex
        int possibleLowerSum = sumIfAddStartIndex - remainingSum;
        int possibleUpperSum = sumIfAddStartIndex + remainingSum;

        //// Short-circuit
        if (possibleLowerSum == 0 || possibleUpperSum == 0)
        {
            throw std::runtime_error("Short-circuit ending");
        }

        //// Impossible range
        if (!(tmpResult > 0 && (possibleUpperSum < -tmpResult || possibleLowerSum > tmpResult)))
        {
            traverse(stones,
                     sumIfAddStartIndex,
                     startIndex + 1,
                     forwardSums,
                     tmpResult);
        }

        // Recursive call
        //// Subtract stone at startIndex
        possibleLowerSum = sumIfSubtractStartIndex - remainingSum;
        possibleUpperSum = sumIfSubtractStartIndex + remainingSum;

        //// Short-circuit
        if (possibleLowerSum == 0 || possibleUpperSum == 0)
        {
            throw std::runtime_error("Short-circuit ending");
        }

        //// Impossible range
        if (!(tmpResult > 0 && (possibleUpperSum < -tmpResult || possibleLowerSum > tmpResult)))
        {
            traverse(stones,
                     sumIfSubtractStartIndex,
                     startIndex + 1,
                     forwardSums,
                     tmpResult);
        }
    }
};

int main()
{
    std::vector<int> vect{21, 16, 23, 32, 25, 13, 20, 18, 22, 21, 84, 35, 33, 17, 27, 24, 10, 19, 31, 26, 94, 37, 31, 25, 24, 25, 15, 23, 17, 13};

    Solution sol;
    std::cout << sol.lastStoneWeightII(vect) << std::endl;

    return 0;
}