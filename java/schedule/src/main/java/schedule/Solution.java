package schedule;

import ai.timefold.solver.core.api.score.ScoreExplanation;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import lombok.*;
import schedule.solver.Schedule;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Solution {

    Schedule schedule;
    ScoreExplanation<Schedule, HardSoftScore> scoreExplanation;

}
