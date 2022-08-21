#include <vector>
#include <utility>
#include <set>
#include <stack>
#include <map>
#include <deque>
#include <iostream>

typedef std::map<int, std::set<int>> EdgeMap;

class DislikeGraph
{
public:
    DislikeGraph(int n, const std::vector<std::vector<int>> &dislikes) : mUnscannedVertices(), mDislikedEdges()
    {
        // Edges
        int maxVertexIndex = -1;
        for (const std::vector<int> &dislike : dislikes)
        {
            int v1 = dislike[0];
            int v2 = dislike[1];
            addDislikeEdge(v1, v2);
            maxVertexIndex = std::max({v1, v2, maxVertexIndex});
        }

        maxVertexIndex = std::min(maxVertexIndex, n);
        // Vertices
        for (int v = 1; v <= maxVertexIndex; v++)
        {
            mUnscannedVertices.insert(v);
        }
    }

    bool scanForBiPartionFromVertex(int vertexIndex)
    {
        std::deque<int> tranversedVertexIndexes;
        tranversedVertexIndexes.push_back(vertexIndex);
        markVertexAsScanned(vertexIndex);

        // Sets of neutral and disliked vertices agaisnt the given vertex
        std::set<int> neutralVertexIndexes;
        neutralVertexIndexes.insert(vertexIndex);

        std::set<int> dislikedVertexIndexes;

        while (!tranversedVertexIndexes.empty())
        {
            int currentVertex = tranversedVertexIndexes.front();

            bool isNeutralNextVertex = (neutralVertexIndexes.find(currentVertex) != neutralVertexIndexes.end());
            std::set<int> &forbidSet = isNeutralNextVertex ? neutralVertexIndexes : dislikedVertexIndexes;
            std::set<int> &nextSet = isNeutralNextVertex ? dislikedVertexIndexes : neutralVertexIndexes;

            // Find disliked-connected vertexes
            auto dislikedVertexIter = mDislikedEdges.find(currentVertex);
            if (dislikedVertexIter != mDislikedEdges.end())
            {
                // Check if is there any contradiction in connected vertexes
                for (int dislikedVertexIndex : dislikedVertexIter->second)
                {
                    // If the dislikedVertex is already determined as neutral (or vice-versa) -> contradiction
                    if (forbidSet.find(dislikedVertexIndex) != forbidSet.end())
                    {
                        return false;
                    }
                }
            }

            // Post-process: Remove edge, vertex from unscanned area
            tranversedVertexIndexes.pop_front();

            if (dislikedVertexIter != mDislikedEdges.end())
            {
                for (int dislikedVertexIndex : dislikedVertexIter->second)
                {
                    if (!hasVertexScanned(dislikedVertexIndex))
                    {
                        nextSet.insert(dislikedVertexIndex);
                        tranversedVertexIndexes.push_back(dislikedVertexIndex);
                        markVertexAsScanned(dislikedVertexIndex);
                    }
                }
            }
        }

        return true;
    }

    int getNextUnscanedVertex() const
    {
        return *mUnscannedVertices.begin();
    }

    bool isEmpty() const
    {
        return mUnscannedVertices.empty();
    }

private:
    void addDislikeEdge(int vertex1, int vertex2)
    {
        // One direction
        {
            EdgeMap::iterator iter1 = mDislikedEdges.find(vertex1);
            if (iter1 != mDislikedEdges.end())
            {
                iter1->second.insert(vertex2);
            }
            else
            {
                mDislikedEdges[vertex1] = std::set<int>{vertex2};
            }
        }

        // And the other
        {
            EdgeMap::iterator iter2 = mDislikedEdges.find(vertex2);
            if (iter2 != mDislikedEdges.end())
            {
                iter2->second.insert(vertex1);
            }
            else
            {
                mDislikedEdges[vertex2] = std::set<int>{vertex1};
            }
        }
    }

    bool hasVertexScanned(int vertex) const
    {
        return mUnscannedVertices.find(vertex) == mUnscannedVertices.end();
    }

    void markVertexAsScanned(int vertex)
    {
        mUnscannedVertices.erase(mUnscannedVertices.find(vertex));
    }

    std::set<int> mUnscannedVertices;
    EdgeMap mDislikedEdges;
};

class Solution
{
public:
    bool possibleBipartition(int n, std::vector<std::vector<int>> &dislikes)
    {
        DislikeGraph graph(n, dislikes);
        int startVertex = 1;

        while (!graph.isEmpty())
        {
            bool scanResult = graph.scanForBiPartionFromVertex(startVertex);
            if (!scanResult)
            {
                return false;
            }
            startVertex = graph.getNextUnscanedVertex();
        }

        return true;
    }
};

int main()
{
    int n = 10;
    std::vector<std::vector<int>> dislikes{{4, 7}, {4, 8}, {5, 6}, {1, 6}, {3, 7}, {2, 5}, {5, 8}, {1, 2}, {4, 9}, {6, 10}, {8, 10}, {3, 6}, {2, 10}, {9, 10}, {3, 9}, {2, 3}, {1, 9}, {4, 6}, {5, 7}, {3, 8}, {1, 8}, {1, 7}, {2, 4}};
    Solution solution;
    std::cout << solution.possibleBipartition(n, dislikes) << std::endl;
    return 1;
}