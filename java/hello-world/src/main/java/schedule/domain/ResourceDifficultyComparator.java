package schedule.domain;

import java.util.Comparator;

public class ResourceDifficultyComparator implements Comparator<Resource> {

    public int compare(Resource a, Resource b) {
        return Integer.compare(a.getDifficulty(), b.getDifficulty());
    }

}