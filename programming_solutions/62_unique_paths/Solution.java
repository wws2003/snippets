class Solution {
    public int uniquePaths(int m, int n) {
        long[][] paths = new long[m][n];
        
        for (int r = 1; r < m; r++) {
            paths[r][0] = 1;
        }
        for (int c = 1; c < n; c++) {
            paths[0][c] = 1;
        }

        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                paths[r][c] = paths[r][c - 1] + paths[r - 1][c];
            }
        }

        // Special case
        paths[0][0] = 1;

        return (int)paths[m - 1][n - 1];
    }
}