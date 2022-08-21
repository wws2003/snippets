#include <cstdio>
#include <vector>

struct TreeNode
{
    int val;
    TreeNode *left;
    TreeNode *right;
    TreeNode(int x) : val(x), left(NULL), right(NULL) {}
};

class Solution
{
public:
    std::vector<int> distanceK(TreeNode *root, TreeNode *target, int k)
    {
        std::vector<int> rets;

        // Collect from child nodes first
        collectDistanceKSubTree(target, k, rets);

        // Then back to parent
        std::vector<TreeNode *> pathFromRootToTarget;
        pathFromRootToTarget.push_back(root);

        // The target node is in the path at the last position
        findTargetParent(target, pathFromRootToTarget);
        // If no parent found (i.e., root = target or target is not in the tree), finish here
        if (pathFromRootToTarget.size() <= 1)
        {
            return rets;
        }

        // Rotate
        rotateParentBranch(pathFromRootToTarget);

        // // Access from the direct parent node
        collectDistanceKSubTree(pathFromRootToTarget[pathFromRootToTarget.size() - 2], k - 1, rets);

        return rets;
    }

private:
    bool findTargetParent(TreeNode *target, std::vector<TreeNode *> &pathFromRoot)
    {
        TreeNode *pFarthestNode = pathFromRoot[pathFromRoot.size() - 1];
        if (pFarthestNode == target)
        {
            return true;
        }
        // Try left
        if (pFarthestNode->left != NULL)
        {
            pathFromRoot.push_back(pFarthestNode->left);
            // Target found in the left branch
            if (findTargetParent(target, pathFromRoot))
            {
                return true;
            }
            pathFromRoot.pop_back();
        }

        // Try right
        if (pFarthestNode->right != NULL)
        {
            pathFromRoot.push_back(pFarthestNode->right);
            // Target found in the right branch
            if (findTargetParent(target, pathFromRoot))
            {
                return true;
            }
            pathFromRoot.pop_back();
        }

        return false;
    }

    void rotateParentBranch(std::vector<TreeNode *> &pathFromRoot)
    {
        for (int i = pathFromRoot.size() - 2; i >= 0; i--)
        {
            TreeNode *pPrevNode = (i == 0) ? NULL : pathFromRoot[i - 1];
            TreeNode *pNode = pathFromRoot[i];
            TreeNode *pNextNode = pathFromRoot[i + 1];

            bool fromLeftBranch = (pNode->left == pNextNode);
            if (fromLeftBranch)
            {
                // Left <-| parent
                pNode->left = pPrevNode;
            }
            else
            {
                // Right <-| parent
                pNode->right = pPrevNode;
            }
        }
    }

    void collectDistanceKSubTree(TreeNode *pChildNode, int k, std::vector<int> &rets)
    {
        if (pChildNode == NULL)
        {
            return;
        }
        if (k < 0)
        {
            return;
        }
        if (k == 0)
        {
            rets.push_back(pChildNode->val);
            return;
        }
        collectDistanceKSubTree(pChildNode->left, k - 1, rets);
        collectDistanceKSubTree(pChildNode->right, k - 1, rets);
    }
};

int main()
{
    TreeNode node3(3), node2(2), node1(1), node0(0);

    node0.left = &node1;
    node1.left = &node3;
    node1.right = &node2;

    Solution s;

    std::vector<int> rets = s.distanceK(&node0, &node2, 1);
    for (int v : rets)
    {
        printf("Val: %d\n", v);
    }
}