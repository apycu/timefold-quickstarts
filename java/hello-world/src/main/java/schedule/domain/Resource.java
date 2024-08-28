package schedule.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@PlanningEntity(difficultyComparatorClass = ResourceDifficultyComparator.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @PlanningId
    private String id;

    private String name;

    private Integer durationMinutes;

    @PlanningVariable
    private TimeSlot chosenAvailability;

    @ValueRangeProvider
    private List<TimeSlot> availabilities;

//    @PlanningVariable(valueRangeProviderRefs = "bestCommonResource1")
//    private TaggableResource bestCommonResource1;

    private String bestCommonResource1Name;

    private int difficulty;

    public Resource(String id, Integer durationMinutes, List<TimeSlot> availabilities) {
        this(id, null, durationMinutes, availabilities);
    }

    public Resource(String id, String name, Integer durationMinutes, List<TimeSlot> availabilities) {
        this.id = id;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.availabilities = availabilities;
    }

    @Override
    public String toString() {
        return String.format("Resource (%s - %s - %s)",
            getId(),
            getChosenAvailability() == null ? null : getChosenAvailability().getStart(),
            getDurationMinutes());
    }
}
