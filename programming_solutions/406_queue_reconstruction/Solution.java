import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

class Solution {
    public int[][] reconstructQueue(int[][] people) {
        int peopleCount = people.length;

        // Sort people records by h-value, in descending order
        // // Linked list for efficient removal
        LinkedList<PeopleInfo> unqueuedPeopleInfos = new LinkedList<>();
        for (int i = 0; i < peopleCount; i++) {
            PeopleInfo peopleInfo = new PeopleInfo(people[i][0], people[i][1], i);
            unqueuedPeopleInfos.add(peopleInfo);
        }
        unqueuedPeopleInfos.sort(Comparator.comparing(PeopleInfo::getH).thenComparing(PeopleInfo::getK));

        // Repeat: Add to results the record with k-value = 0, then updates k-value for other records
        int queueIndex = 0;
        int[][] queue = new int[people.length][];

        while(!unqueuedPeopleInfos.isEmpty()) {
            ListIterator<PeopleInfo> iter = unqueuedPeopleInfos.listIterator(0);
            PeopleInfo nextPeopleInfo = null;

            // Find the person with no shorter people in front of
            while(iter.hasNext()) {
                PeopleInfo peopleInfo = iter.next();
                if (peopleInfo.getK() == 0) {
                    nextPeopleInfo = peopleInfo;
                    iter.remove();
                    break;
                }
            }

            if (nextPeopleInfo != null) {
                // Add people to queue
                int nextPersonIndex = nextPeopleInfo.getOrgIndex();
                queue[queueIndex] = people[nextPersonIndex];    

                queueIndex += 1;

                // Update other people k-value
                int nextHInQueue = nextPeopleInfo.getH();
                for (PeopleInfo peopleInfo : unqueuedPeopleInfos) {
                    if (peopleInfo.getH() <= nextHInQueue) {
                        peopleInfo.reduceK();
                    }
                }

            } else {
                throw new RuntimeException("Invalid data");
            }
        }

        return queue;
    }

    private class PeopleInfo {
        private int h;
        private int k;
        private int orgIndex;

        public PeopleInfo(int h, int k, int orgIndex) {
            this.h = h;
            this.k = k;
            this.orgIndex = orgIndex;
        }

        public int getH() {
            return h;
        }

        public int getK() {
            return k;
        }

        public int getOrgIndex() {
            return orgIndex;
        }

        public void reduceK() {
            this.k -= 1;
        }
    }

    // public static void main(String[] args) {
    // }
}