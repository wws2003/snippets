class Solution {
    public int largestRectangleArea(int[] heights) {
        
        int maxHeight = 0;
        int minHeight = Integer.MAX_VALUE;

        for(int height : heights) {
            if (height < minHeight) {
                minHeight = height;
            }
            if (height > maxHeight) {
                maxHeight = height;
            }
        }

        int elementsCount = heights.length;

        // Handle special case
        if (minHeight == maxHeight) {
            return minHeight * elementsCount;
        }
        
        IntermediateResult result = new IntermediateResult(heights[0], maxHeight);
        for (int i = 1; i < elementsCount; i++) {
            result.goToNextCol(heights[i]);
        }
        return result.getLargestRect();
    }

    private static class IntsBufferList {
        private int[][] elementsBuffer;
        private int size = 0;

        public IntsBufferList(int[][] elementsBuffer) {
            this.elementsBuffer = elementsBuffer;
        }

        public void add(int v1, int v2) {
            int[] eles = elementsBuffer[size];
            eles[0] = v1;
            eles[1] = v2;
            size++;
        }

        public int[] get(int index) {
            return elementsBuffer[index];
        }

        public void removeFromIndex(int index) {
            size = index;
        }

        public int size() {
            return size;
        }
    }

    private static class IntermediateResult {
        // Kept in ascending order of height, starting from height = 1
        private IntsBufferList areaOfRectsEndAtLastCol;
        private int maxArea = 0;

        public IntermediateResult(int firstColHeight, int maxHeight) {
            int[][] buffer = new int[maxHeight][2];

            areaOfRectsEndAtLastCol = new IntsBufferList(buffer);

            for (int h = 1; h <= firstColHeight; h++) {
                areaOfRectsEndAtLastCol.add(0, h);
            }
            maxArea = firstColHeight;
        }

        public void goToNextCol(int nextColHeight) {
            int prevColHeight = areaOfRectsEndAtLastCol.size();

            // Divide cases for simple computation
            if (nextColHeight >= prevColHeight) {
                // Next col is higher than or as height as prev col
                for (int h = 1; h <= prevColHeight; h++) {
                    int[] areas = areaOfRectsEndAtLastCol.get(h - 1);
                    areas[0] = Math.max(areas[0], areas[1]) + h;
                    areas[1] = h;
                    tryToUpdateMaxArea(areas[0]);
                }
                for (int h = prevColHeight + 1; h <= nextColHeight; h++) {
                    areaOfRectsEndAtLastCol.add(0, h);
                }
                tryToUpdateMaxArea(nextColHeight);
            } else {
                // Next col is lower than prev col
                for (int h = 1; h <= nextColHeight; h++) {
                    int[] areas = areaOfRectsEndAtLastCol.get(h - 1);
                    areas[0] = Math.max(areas[0], areas[1]) + h;
                    areas[1] = h;
                    tryToUpdateMaxArea(areas[0]);
                }
                areaOfRectsEndAtLastCol.removeFromIndex(nextColHeight);
            }
        }

        public int getLargestRect() {
            return maxArea;
        }

        private void tryToUpdateMaxArea(int area) {
            if (area > maxArea) {
                maxArea = area;
            }
        }
    }
}