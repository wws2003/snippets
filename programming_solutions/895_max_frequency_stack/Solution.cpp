#include <map>
#include <list>

typedef short FreqType;

typedef std::list<int> ValStack;
typedef std::map<FreqType, ValStack> FrequencyMap;

typedef std::map<int, FreqType> ValFreqMap;

class FreqStack
{
public:
    FreqStack() : mFreqMap(), mValFreqMap()
    {
    }

    void push(int val)
    {
        // Add to position map: val -> freq, indexes
        auto valPosIter = mValFreqMap.find(val);
        FreqType freq = 0;
        if (valPosIter != mValFreqMap.end())
        {
            freq = ++mValFreqMap[val];
        }
        else
        {
            mValFreqMap[val] = (FreqType)1;
            freq = 1;
        }

        // Update frequency map: freq -> {index -> val}
        auto freqValIter = mFreqMap.find(freq);
        if (freqValIter != mFreqMap.end())
        {
            freqValIter->second.push_front(val);
        }
        else
        {
            ValStack indexToValMap;
            indexToValMap.push_front(val);
            mFreqMap[freq] = indexToValMap;
        }
    }

    int pop()
    {
        // Greatest frequency
        auto greatestFreqValIter = mFreqMap.rbegin();
        FreqType highestFreq = greatestFreqValIter->first;
        ValStack &valsWithGreatestFreq = greatestFreqValIter->second;

        // Find value with highest index and greatest frequency
        int retVal = valsWithGreatestFreq.front();

        // Update frequency map
        valsWithGreatestFreq.pop_front();
        if (valsWithGreatestFreq.empty())
        {
            mFreqMap.erase(highestFreq);
        }

        // Update position map
        mValFreqMap[retVal]--;

        return retVal;
    }

private:
    FrequencyMap mFreqMap;
    ValFreqMap mValFreqMap;
};

int main()
{
    FreqStack *obj = new FreqStack();
    obj->push(1);
}