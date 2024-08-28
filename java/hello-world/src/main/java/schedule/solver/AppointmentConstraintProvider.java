package schedule.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.domain.Resource;
import schedule.domain.TimeConstraint;
import static ai.timefold.solver.core.api.score.stream.Joiners.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class AppointmentConstraintProvider implements ConstraintProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentConstraintProvider.class);

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
            timeConstraint(factory),
        };
    }

    private Constraint timeConstraint(ConstraintFactory factory) {
        return factory.forEach(TimeConstraint.class)
                .join(Resource.class,
                        equal(TimeConstraint::getEarlierResourceId, Resource::getId)
                )
                .join(Resource.class,
                        equal((timeConstraint, earlierResource) -> timeConstraint.getLaterResourceId(), resource -> resource.getId())
                )
                .filter((timeConstraint, earlierResource, laterResource) -> {
                    return !isTimeConstraintSatisfied(timeConstraint, earlierResource, laterResource);
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Time constraint conflict");
    }

    public boolean isTimeConstraintSatisfied(TimeConstraint timeConstraint, Resource earlierResource, Resource laterResource) {
        LocalDateTime earlierStart = earlierResource.getChosenAvailability().getStart();
        LocalDateTime laterStart = laterResource.getChosenAvailability().getStart();

        if (earlierStart == null || laterStart == null) {
            return false;
        }

        Duration actualDuration = Duration.between(earlierStart, laterStart);
        return actualDuration.compareTo(timeConstraint.getMinDuration()) >= 0 &&
                (timeConstraint.getMaxDuration() == null ? true : (actualDuration.compareTo(timeConstraint.getMaxDuration()) <= 0));
    }

}
