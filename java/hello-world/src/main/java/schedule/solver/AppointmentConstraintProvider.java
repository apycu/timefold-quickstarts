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
                //resourceAvailabilityConstraint(factory),
                timeConstraint(factory),
//                sameDynamic(factory)
        };
    }

//    private Constraint resourceAvailabilityConstraint(ConstraintFactory factory) {
//        return factory.forEach(Appointment.class)
//                .filter(appointment -> !areResourcesAvailable(appointment))
//                .penalize(HardSoftScore.ONE_HARD)
//                .asConstraint("Resource not available");
//    }

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

//    private Constraint sameDynamic(ConstraintFactory factory) {
//        return factory.forEachUniquePair(Appointment.class, Joiners.equal(Appointment::getBestCommonResource1Name))
//                .filter((appointment, appointment2) -> {
//                    if (!appointment.getBestCommonResource1().getTag().equals(appointment.getBestCommonResource1Name())){
//                        return true;
//                    }
//                    if (!isResourceAvailable(appointment, appointment.getBestCommonResource1())) {
//                        return true;
//                    }
//                    if (!isResourceAvailable(appointment2, appointment2.getBestCommonResource1())) {
//                        return true;
//                    }
//                    return !appointment.getBestCommonResource1().getId().equals(appointment2.getBestCommonResource1().getId());
//                })
//                .penalize(HardSoftScore.ONE_SOFT)
//                .asConstraint("Dynamic value not the same");
//    }
//
//    public boolean areResourcesAvailable(Appointment appointment) {
//        AtomicBoolean available = new AtomicBoolean(true);
//        appointment.getRequiredResources().stream().forEach(resource -> {
//            available.set(available.get() && isResourceAvailable(appointment, resource));
//        });
//        return available.get();
//    }
//
//    public boolean isResourceAvailable(Appointment appointment) {
//        AtomicBoolean available = new AtomicBoolean(true);
//        if (appointment.getStartTimes() != null) {
//            available.set(false);
//            appointment.getStartTimes().stream().forEach(ts -> {
//                available.set(available.get() || isOverlapping(appointment.getStartTime().getStart(), appointment.getStartTime().getStart().plusMinutes(appointment.getDurationMinutes()), ts.getStart(), ts.getEnd()));
//            });
//        }
//        return available.get();
//    }

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

//    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
//        // return !start1.isAfter(end2) && !start2.isAfter(end1);
//        return start1.isBefore(end2) && start2.isBefore(end1);
//    }

//    public boolean isTimeConstraintSatisfied(TimeConstraint constraint, Resource earlierResource, Resource laterResource) {
//        return calculateTimeConstraintStarts(constraint, earlierResource, laterResource) != null;
//    }
//
//    public LocalDateTime[] calculateTimeConstraintStarts(TimeConstraint constraint, Resource earlierResource, Resource laterResource) {
//        if (earlierResource == null || laterResource == null) {
//            return null;
//        }
//        LocalDateTime earliestStartFirst = earlierResource.getChosenAvailability().getStart();
//        LocalDateTime latestStartFirst = earlierResource.getChosenAvailability().getEnd().minusMinutes(earlierResource.getDurationMinutes());
//
//        // Iterate over possible start times for the first resource
//        for (LocalDateTime candidateStartFirst = earliestStartFirst;
//             !candidateStartFirst.isAfter(latestStartFirst);
//             candidateStartFirst = candidateStartFirst.plusMinutes(10)) {
//
//            // Calculate the feasible start window for laterResource
//            LocalDateTime earliestPossibleStartSecond = candidateStartFirst.plus(constraint.getMinDuration());
//            LocalDateTime latestPossibleStartSecond = candidateStartFirst.plus(constraint.getMaxDuration());
//
//            // Intersection with laterResource's TimeSlot
//            LocalDateTime earliestStartSecond = max(earliestPossibleStartSecond, laterResource.getChosenAvailability().getStart());
//            LocalDateTime latestStartSecond = min(latestPossibleStartSecond, laterResource.getChosenAvailability().getEnd().minusMinutes(laterResource.getDurationMinutes()));
//
//            // Check if the start times are valid
//            if (!earliestStartSecond.isAfter(latestStartSecond)) {
//                return new LocalDateTime[]{candidateStartFirst, earliestStartSecond};
//            }
//        }
//        return null;
//    }
//
//    // Utility functions to get the maximum/minimum of two LocalDateTime values
//    private LocalDateTime max(LocalDateTime a, LocalDateTime b) {
//        return a.isAfter(b) ? a : b;
//    }
//
//    private LocalDateTime min(LocalDateTime a, LocalDateTime b) {
//        return a.isBefore(b) ? a : b;
//    }

}
