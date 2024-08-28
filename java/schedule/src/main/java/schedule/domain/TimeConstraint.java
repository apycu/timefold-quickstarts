package schedule.domain;
import lombok.*;

import java.time.Duration;

/**
 * Defines a time constraint between two resources.
 * Relatively to the start of the earlierResource, the start of laterResource must be after within minDuration and maxDuration.
 * For example if minDuration is 3 days, and maxDuration is 7 days, the start of earlierResource is 1st January 2024, then the start of laterResource must be within 4th January and 8th January.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TimeConstraint {
    private String earlierResourceId;
    private String laterResourceId;
    private Duration minDuration;
    private Duration maxDuration;
}
