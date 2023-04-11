import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Solution {
    public List<List<String>> deleteDuplicateFolder(List<List<String>> paths) {
        PathCollection pathCollection = new PathCollection();
        pathCollection.addPaths(paths);
        return pathCollection.getUniquePaths();
    }

    private static class PathCollection {
        private final SortedSet<String> mOrderedFolderNames = new TreeSet<>();
        private List<List<String>> mPaths = new ArrayList<>();
        private FolderTreeNode mRoot = new FolderTreeNode(-1, -1);
        private static final String PATH_DELIMETER = "/";

        public void addPaths(List<List<String>> paths) {
            // Remember all paths
            mPaths = paths;

            // Assign id for paths
            Map<String, Integer> pathIdMap = new HashMap<>();
            for (List<String> path : paths) {
                String pathKey = path.stream()
                        .collect(Collectors.joining(PATH_DELIMETER, PATH_DELIMETER, ""));
                pathIdMap.put(pathKey, pathIdMap.size());
            }

            // Assigned id for folder name
            for (List<String> path : paths) {
                mOrderedFolderNames.addAll(path);
            }
            Map<String, Integer> folderNameIdMap = new HashMap<>();
            var iter = mOrderedFolderNames.iterator();
            while (iter.hasNext()) {
                String folderName = iter.next();
                folderNameIdMap.put(folderName, folderNameIdMap.size());
            }

            // Build tree structure from root
            for (List<String> path : paths) {
                FolderTreeNode parentNode = mRoot;
                StringJoiner pathJoiner = new StringJoiner(PATH_DELIMETER, PATH_DELIMETER, "");

                // Traverse along path
                for (String folderName : path) {
                    pathJoiner.add(folderName);
                    String nodePath = pathJoiner.toString();

                    int folderPathId = pathIdMap.getOrDefault(nodePath, Integer.MAX_VALUE);
                    int folderNameId = folderNameIdMap.getOrDefault(folderName, Integer.MAX_VALUE);

                    FolderTreeNode childNode = parentNode.findChildNode(folderNameId);
                    if (childNode == null) {
                        childNode = new FolderTreeNode(folderPathId, folderNameId);
                        parentNode.addChildNode(childNode);
                    }
                    parentNode = childNode;
                }
            }
        }

        public List<List<String>> getUniquePaths() {
            List<LeafPath> leafPaths = extractLeafPaths();

            FolderStructureMap folderStructureMap = new FolderStructureMap();
            for (LeafPath leafPath : leafPaths) {
                // Traverse back from leaf
                Iterator<Integer[]> nodeIdIter = leafPath.getNodeIdsItertorFromLeaf();
                List<Integer> folderNameIdsPathToLeaf = new ArrayList<>();
                while (nodeIdIter.hasNext()) {
                    Integer[] nodeIds = nodeIdIter.next();
                    int nodePathId = nodeIds[0];
                    int nodeFolderNameId = nodeIds[2];
                    folderNameIdsPathToLeaf.add(nodeFolderNameId);
                    // Only care for one-level upper from leaf
                    if (folderNameIdsPathToLeaf.size() > 1) {
                        // folderStructureMap.add(nodePathId, folderNameIdsPathToLeaf);
                    }
                }
            }

            List<Integer> uniquedPathIds = folderStructureMap.getUniquePaths();

            return uniquedPathIds.stream()
                    .map(pathId -> mPaths.get(pathId))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private List<LeafPath> extractLeafPaths() {
            List<LeafPath> leafPaths = new ArrayList<>();
            traverseForLeafPaths(mRoot, null, leafPaths);
            return leafPaths;
        }

        private void traverseForLeafPaths(FolderTreeNode node,
                Stack<FolderTreeNode> currentPathFromRoot,
                List<LeafPath> leafPaths) {
            currentPathFromRoot.push(node);

            if (!node.hasChild()) {
                leafPaths.add(createLeafPath(currentPathFromRoot));
                currentPathFromRoot.pop();
                return;
            }
            for (FolderTreeNode childNode : node.getChildNodes()) {
                traverseForLeafPaths(childNode, currentPathFromRoot, leafPaths);
            }

            currentPathFromRoot.pop();
        }

        private LeafPath createLeafPath(Stack<FolderTreeNode> currentPathFromRoot) {
            // TODO Implement
            return new LeafPath();
        }
    }

    private static class LeafPath {
        // private final int folderPathId;
        // private final int folderNameId;

        public List<String> getPath() {
            return null;
        }

        public Iterator<Integer[]> getNodeIdsItertorFromLeaf() {
            // First element -> Id of path
            // Second element -> Distance from leaf (start at 0)
            // Third element -> Id of folder name
            return null;
        }
    }

    private static class FolderTreeNode {
        private final int folderPathId;
        private final int folderNameId;
        private final List<FolderTreeNode> childNodes = new ArrayList<>();

        public FolderTreeNode(int folderPathId, int folderNameId) {
            this.folderPathId = folderPathId;
            this.folderNameId = folderNameId;
        }

        public int getFolderPathId() {
            return folderPathId;
        }

        public int getFolderNameId() {
            return folderNameId;
        }

        public List<FolderTreeNode> getChildNodes() {
            return childNodes;
        }

        public void addChildNode(FolderTreeNode childNode) {
            childNodes.add(childNode);
        }

        public FolderTreeNode findChildNode(int childFolderNameId) {
            return childNodes.stream()
                    .filter(node -> node.getFolderNameId() == childFolderNameId)
                    .findFirst()
                    .orElse(null);
        }

        public boolean hasChild() {
            return !childNodes.isEmpty();
        }
    }

    private static class FolderStructureMap {
        private final Map<Integer, FolderKey> pathToFolderKeyMap = new HashMap<>();

        public void add(int pathId, int distanceFromLeaf, int subFolderNameId) {
            FolderKey folderKey = pathToFolderKeyMap.getOrDefault(pathId, null);
            if (folderKey == null) {
                folderKey = new FolderKey();
                pathToFolderKeyMap.put(pathId, folderKey);
            }
            folderKey.addFolderNameId(distanceFromLeaf, subFolderNameId);
        }

        public List<Integer> getUniquePaths() {
            SortedMap<FolderKey, List<Integer>> folderKeyToPathIdsMaps = new TreeMap<>(new FolderKeyComparator());
            for (var entry : pathToFolderKeyMap.entrySet()) {
                int pathId = entry.getKey();
                FolderKey folderKey = entry.getValue();

                List<Integer> pathIdsWithFolderKey = folderKeyToPathIdsMaps.getOrDefault(folderKey, null);
                if (pathIdsWithFolderKey == null) {
                    pathIdsWithFolderKey = new ArrayList<>();
                    folderKeyToPathIdsMaps.put(folderKey, pathIdsWithFolderKey);
                }
                pathIdsWithFolderKey.add(pathId);
            }

            return folderKeyToPathIdsMaps.values()
                    .stream()
                    .filter(pathIds -> pathIds.size() == 1)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }

    private static class FolderKey {
        public FolderKey() {

        }

        public void addFolderNameId(int distanceFromLeaf, int subFolderNameId) {

        }

        public int getMaxDistanceFromLeaf() {
            return 1;
        }

        public String getSubFoldersKeyAtDistanceFromLeaf(int distanceFromLeaf) {
            return "";
        }
    }

    private static class FolderKeyComparator implements Comparator<FolderKey> {

        @Override
        public int compare(Solution.FolderKey o1, Solution.FolderKey o2) {
            // TODO Auto-generated method stub
            int maxDistanceFromLeaf1 = o1.getMaxDistanceFromLeaf();
            int maxDistanceFromLeaf2 = o2.getMaxDistanceFromLeaf();

            int commonMaxDistanceFromLeaf = Math.min(maxDistanceFromLeaf1, maxDistanceFromLeaf2);
            for (int distanceFromLeaf = 0; distanceFromLeaf < commonMaxDistanceFromLeaf; distanceFromLeaf++) {
                String key1 = o1.getSubFoldersKeyAtDistanceFromLeaf(distanceFromLeaf);
                String key2 = o2.getSubFoldersKeyAtDistanceFromLeaf(distanceFromLeaf);
            }

            return 0;
        }

    }

    public static void main(String[] args) {
        System.out.println("wwww");
    }
}
