#include <map>
#include <vector>
#include <unordered_map>

typedef std::vector<int> NodeIndexes;
typedef NodeIndexes *NodeIndexesPtr;
typedef std::map<int, NodeIndexesPtr> NodeLayers;

typedef std::vector<int> NodeValMap;

struct TreeNode
{
    int val;
    TreeNode *left;
    TreeNode *right;
    TreeNode() : val(0), left(nullptr), right(nullptr) {}
    TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
    TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
};

struct TreeNodeInfo
{
    int val;
    NodeIndexes childNodeIndexes;
    TreeNodeInfo(int v = 0) : val(v) {}
};

class IndexedTree
{
public:
    void addNode(int index, int val)
    {
        TreeNodeInfo info(val);
        mNodesMap[index] = val;
    }

    void connectNodes(int parentIndex, int childIndex)
    {
        auto iter = mNodesMap.find(parentIndex);
        if (iter != mNodesMap.end())
        {
            iter->second.childNodeIndexes.push_back(childIndex);
        }
    }

    int getNodeVal(int nodeIndex)
    {
        auto iter = mNodesMap.find(nodeIndex);
        if (iter == mNodesMap.end())
        {
            return -1;
        }
        return iter->second.val;
    }

    void getChildNodeIndexes(int nodeIndex, NodeIndexes &childNodes)
    {
        childNodes.clear();
        auto iter = mNodesMap.find(nodeIndex);
        if (iter != mNodesMap.end())
        {
            for (int childIndex : iter->second.childNodeIndexes)
            {
                childNodes.push_back(childIndex);
            }
        }
    }

    int getNodesCount()
    {
        return mNodesMap.size();
    }

private:
    std::map<int, TreeNodeInfo> mNodesMap;
};

class Solution
{
public:
    int rob(TreeNode *root)
    {
        NodeLayers nodeLayers;
        IndexedTree indexedTree;

        // Parse to convenient data structures
        int rootIndex = analyze(root, &nodeLayers, &indexedTree);

        // Options map
        int nodesCounts = indexedTree.getNodesCount();
        NodeValMap bestIncludingValsMap(nodesCounts);
        NodeValMap bestExcludingValsMap(nodesCounts);

        // Actual logic: Counting from the lower layers to root layer
        for (auto iter = nodeLayers.rbegin(); iter != nodeLayers.rend(); iter++)
        {
            NodeIndexesPtr pNodeIndexes = iter->second;

            for (auto iter2 = pNodeIndexes->begin(); iter2 != pNodeIndexes->end(); iter2++)
            {
                int nodeIndex = *iter2;
                int val = indexedTree.getNodeVal(nodeIndex);
                NodeIndexes childNodeIndexes;
                childNodeIndexes.reserve(2);
                indexedTree.getChildNodeIndexes(nodeIndex, childNodeIndexes);

                // Counting from val of child nodes
                // // Options including current node
                bestIncludingValsMap[nodeIndex] = val;
                for (int childNodeIndex : childNodeIndexes)
                {
                    bestIncludingValsMap[nodeIndex] += bestExcludingValsMap[childNodeIndex];
                }
                // // Options excluding current node
                bestExcludingValsMap[nodeIndex] = 0;
                for (int childNodeIndex : childNodeIndexes)
                {
                    bestExcludingValsMap[nodeIndex] += std::max(bestIncludingValsMap[childNodeIndex],
                                                                bestExcludingValsMap[childNodeIndex]);
                }
            }
        }

        int ret = std::max(bestExcludingValsMap[rootIndex], bestIncludingValsMap[rootIndex]);

        // Free up space
        for (auto iter = nodeLayers.begin(); iter != nodeLayers.end();)
        {
            NodeIndexesPtr pNodeIndexes = iter->second;
            delete pNodeIndexes;
            iter = nodeLayers.erase(iter);
        }

        return ret;
    }

private:
    int analyze(TreeNode *root,
                NodeLayers *pNodeLayers,
                IndexedTree *pNodeIndexTree,
                int distanceFromRoot = 0)
    {
        // Process current node
        int currentNodeCount = pNodeIndexTree->getNodesCount();
        int nodeIndex = currentNodeCount;
        int nodeVal = root->val;

        pNodeIndexTree->addNode(nodeIndex, root->val);

        auto nodeLayerIter = pNodeLayers->find(distanceFromRoot);

        NodeIndexesPtr pNodeIndexes = nullptr;
        if (nodeLayerIter == pNodeLayers->end())
        {
            pNodeIndexes = new NodeIndexes();
            pNodeLayers->insert(std::make_pair(distanceFromRoot, pNodeIndexes));
        }
        else
        {
            pNodeIndexes = nodeLayerIter->second;
        }
        pNodeIndexes->push_back(nodeIndex);

        // Recursive calls
        if (root->left != nullptr)
        {
            int childNodeIndex = analyze(root->left,
                                         pNodeLayers,
                                         pNodeIndexTree,
                                         distanceFromRoot + 1);

            pNodeIndexTree->connectNodes(nodeIndex, childNodeIndex);
        }

        if (root->right != nullptr)
        {
            int childNodeIndex = analyze(root->right,
                                         pNodeLayers,
                                         pNodeIndexTree,
                                         distanceFromRoot + 1);

            pNodeIndexTree->connectNodes(nodeIndex, childNodeIndex);
        }

        return nodeIndex;
    }
};

int main()
{
    return 1;
}