#include <vector>
#include <iostream>
#include <cmath>
#include <algorithm>

typedef long sum_t;

class Solution
{
public:
    int minSumOfLengths(std::vector<int> &arr, int target)
    {
        int elementCnt = arr.size();

        // Prepare sub results
        std::vector<int> subResults(elementCnt);

        // Calculate for sub results
        minSumOfLengthUptoEachPosition(arr, target, subResults);

        std::reverse(arr.begin(), arr.end());
        std::vector<int> reversedSubResults(elementCnt);
        minSumOfLengthUptoEachPosition(arr, target, reversedSubResults);

        int minSumOfLength = elementCnt + 1;
        // Traverse to find solution
        for (int i = 0; i < elementCnt - 1; i++)
        {
            int subMinLengthUntil = subResults[i];
            int subMinLengthFromNext = reversedSubResults[elementCnt - i - 2];

            if (subMinLengthUntil < 0)
            {
                // Impossible for now, but keep hoping
                continue;
            }
            if (subMinLengthFromNext < 0)
            {
                // Impossible for a result from the right side
                break;
            }

            if (subMinLengthUntil + subMinLengthFromNext < minSumOfLength)
            {
                minSumOfLength = subMinLengthUntil + subMinLengthFromNext;
            }
        }

        return (minSumOfLength > elementCnt) ? -1 : minSumOfLength;
    }

private:
    void minSumOfLengthUptoEachPosition(const std::vector<int> &arr, int target, std::vector<int> &result)
    {
        // Calculate accumulated sum
        std::vector<sum_t> accSums;
        accSums.reserve(arr.size());
        accSums.push_back(arr[0]);

        // Initial result
        result[0] = (arr[0] == target) ? 1 : -1;

        // Go forward with memory
        for (int i = 1; i < arr.size(); i++)
        {
            // Update acc sum
            sum_t accSumUntil = accSums[i - 1] + (sum_t)arr[i];
            accSums.push_back(accSumUntil);

            // Sum of element before the sub array candidate upto i-th element
            sum_t prevResidualSum = accSumUntil - (sum_t)target;
            if (prevResidualSum == 0)
            {
                result[i] = i + 1;
                continue;
            }

            // Find the element corresponding to prevResidualSum
            std::vector<sum_t>::iterator beginIter = accSums.begin();
            std::vector<sum_t>::iterator endIter = accSums.end();

            auto iter = std::lower_bound(beginIter, endIter, prevResidualSum);
            int prevResidualLastIndex = (iter == endIter || *iter != prevResidualSum) ? -1 : std::distance(beginIter, iter);

            int prevResult = result[i - 1];

            if (prevResidualLastIndex < 0)
            {
                // Can not find any sub array end at i-th element
                result[i] = prevResult;
                continue;
            }

            int subArrayLengthUntil = i - prevResidualLastIndex;

            if (prevResult < 0 || subArrayLengthUntil < prevResult)
            {
                result[i] = subArrayLengthUntil;
            }
            else
            {
                result[i] = prevResult;
            }
        }
    }
};

int main()
{
    std::vector<int> vect{2, 2, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    int target = 20;

    Solution sol;
    std::cout << sol.minSumOfLengths(vect, target) << std::endl;

    return 0;
}