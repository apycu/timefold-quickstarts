package schedule.solver;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import schedule.domain.Resource;
import schedule.domain.TimeConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@PlanningSolution
@Getter
@Setter
@NoArgsConstructor
public class Schedule {

    @ProblemFactCollectionProperty
    public List<TimeConstraint> timeConstraints;

    @PlanningEntityCollectionProperty
    private List<Resource> resources;

    @PlanningScore
    private HardSoftScore score;

    public Schedule(List<TimeConstraint> timeConstraints, List<Resource> resources){
        this.timeConstraints = timeConstraints;
        this.resources = resources;
        for(Resource resource: resources) {
            resource.setDifficulty((int) timeConstraints.stream().filter(t -> resource.getId().equals(t.getEarlierResourceId()) || resource.getId().equals(t.getLaterResourceId())).count());
        }
    }

}
