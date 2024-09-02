import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Solution {

    public int minimumTeachings(int n, int[][] languages, int[][] friendships) {
        int min = Integer.MAX_VALUE;

        // Build the community (since no way for easy copy..)
        Community community = new Community();

        for (int i = 0; i < languages.length; i++) {
            int personId = i + 1;
            community.addPerson(personId, languages[i]);
        }

        for (int[] friendship : friendships) {
            community.addFriendship(friendship);
        }

        // Calculate for each language
        Set<Integer> allLanguages = community.getAllLanguages();
        for (int language : allLanguages) {
            int teach = minimumTeaching(community, language, languages, friendships);
            if (teach <= 1) {
                return teach;
            }
            if (teach < min) {
                min = teach;
            }
        }

        return min;
    }

    public int minimumTeaching(Community community, int n, int[][] languages, int[][] friendships) {
        // Calculate
        Set<Integer> personsToTeach = new HashSet<>();

        for (int[] friendship : friendships) {
            Set<Integer> connectingLanguages = community.getConnectingLanguages(friendship);
            if (connectingLanguages.isEmpty()) {
                // Teach both, if necessary
                int teach1 = community.teach(n, friendship[0]);
                if (teach1 > 0) {
                    personsToTeach.add(friendship[0]);
                }
                int teach2 = community.teach(n, friendship[1]);
                if (teach2 > 0) {
                    personsToTeach.add(friendship[1]);
                }
            }
        }

        int teached = personsToTeach.size();

        // Reverse the teach to restore community
        for (int personId : personsToTeach) {
            community.unteach(n, personId);
        }

        return teached;
    }

    private static class Community {

        private Map<Integer, Set<Integer>> mPersonToLanguagesMap = new HashMap<>();

        private Map<Integer, Set<Integer>> mLanguageToPersonsMap = new HashMap<>();

        private Map<Integer, Map<Integer, Set<Integer>>> mFriendshipMap = new HashMap<>();

        public void addPerson(int personId, int[] languagesIds) {
            // Add to person map
            mPersonToLanguagesMap.putIfAbsent(
                    personId,
                    Arrays.stream(languagesIds).boxed().collect(Collectors.toSet()));

            // Add to language map
            for (int languageId : languagesIds) {
                mLanguageToPersonsMap.putIfAbsent(languageId, new HashSet<>());
                mLanguageToPersonsMap.get(languageId).add(personId);
            }
        }

        public Set<Integer> getAllLanguages() {
            return mLanguageToPersonsMap.keySet();
        }

        public void addFriendship(int[] friendship) {
            Set<Integer> commonLanguages = new HashSet<>(mPersonToLanguagesMap.getOrDefault(friendship[0], Set.of()));
            commonLanguages.retainAll(mPersonToLanguagesMap.getOrDefault(friendship[1], new HashSet<>()));

            mFriendshipMap.putIfAbsent(friendship[0], new HashMap<>());
            mFriendshipMap.get(friendship[0]).put(friendship[1], new HashSet<>(commonLanguages));

            mFriendshipMap.putIfAbsent(friendship[1], new HashMap<>());
            mFriendshipMap.get(friendship[1]).put(friendship[0], new HashSet<>(commonLanguages));
        }

        public Set<Integer> getConnectingLanguages(int[] friendship) {
            return mFriendshipMap.getOrDefault(friendship[0], Map.of())
                    .getOrDefault(friendship[1], Set.of());
        }

        public int teach(int languageId, int personId) {
            if (mPersonToLanguagesMap.getOrDefault(personId, Set.of()).contains(languageId)) {
                return 0;
            }

            mPersonToLanguagesMap.get(personId).add(languageId);
            mLanguageToPersonsMap.get(languageId).add(personId);

            // Update friendship map
            Map<Integer, Set<Integer>> friendships = mFriendshipMap.getOrDefault(personId, Map.of());
            for (Entry<Integer, Set<Integer>> e : friendships.entrySet()) {
                int friend = e.getKey();

                Set<Integer> friendLanguages = mPersonToLanguagesMap.getOrDefault(friend, Set.of());
                if (friendLanguages.contains(languageId)) {
                    Set<Integer> commonLanguages = e.getValue();
                    commonLanguages.add(languageId);
                }
            }

            for (Entry<Integer, Map<Integer, Set<Integer>>> e : mFriendshipMap.entrySet()) {
                int friend = e.getKey();
                Set<Integer> friendLanguages = mPersonToLanguagesMap.getOrDefault(friend, Set.of());
                Map<Integer, Set<Integer>> friendshipsOfFriend = e.getValue();

                if (friendLanguages.contains(languageId)) {
                    Optional.ofNullable(friendshipsOfFriend.get(personId))
                            .ifPresent(langs -> {
                                langs.add(languageId);
                            });
                }
            }

            return 1;
        }

        public void unteach(int languageId, int personId) {
            mPersonToLanguagesMap.get(personId).remove(languageId);
            mLanguageToPersonsMap.get(languageId).remove(personId);

            // Update friendship map
            Map<Integer, Set<Integer>> friendships = mFriendshipMap.getOrDefault(personId, Map.of());
            for (Entry<Integer, Set<Integer>> e : friendships.entrySet()) {
                Set<Integer> commonLanguages = e.getValue();
                commonLanguages.remove(languageId);
            }

            for (Entry<Integer, Map<Integer, Set<Integer>>> e : mFriendshipMap.entrySet()) {
                Map<Integer, Set<Integer>> friendshipsOfFriend = e.getValue();

                if (friendshipsOfFriend.containsKey(personId)) {
                    Optional.ofNullable(friendshipsOfFriend.get(personId))
                            .ifPresent(langs -> {
                                langs.remove(languageId);
                            });
                }
            }
        }

    }

    // public static void main(String[] args) {
    // int n = 3;
    // int[][] languages = { { 2 }, { 1, 3 }, { 1, 2 }, { 3 } };
    // int[][] friendships = { { 1, 4 }, { 1, 2 }, { 3, 4 }, { 2, 3 } };

    // // int n = 2;
    // // int[][] languages = { { 1 }, { 2 }, { 1, 2 } };
    // // int[][] friendships = { { 1, 2 }, { 1, 3 }, { 2, 3 } };

    // Solution solution = new Solution();
    // int t = solution.minimumTeachings(n, languages, friendships);
    // System.out.println(t);
    // }
}
