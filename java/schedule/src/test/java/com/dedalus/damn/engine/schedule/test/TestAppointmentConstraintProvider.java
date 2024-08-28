package com.dedalus.damn.engine.schedule.test;

import schedule.domain.Resource;
import schedule.domain.TimeConstraint;
import schedule.domain.TimeSlot;
import schedule.solver.AppointmentConstraintProvider;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAppointmentConstraintProvider {

    public TimeSlot ts(int d, int h) {
        return new TimeSlot(LocalDateTime.of(2024, 1, d, h, 0), LocalDateTime.of(2024, 1, d, 19, 0));
    }

    @Test
    void isTimeConstraintSatisfied() {
        AppointmentConstraintProvider appointmentConstraintProvider = new AppointmentConstraintProvider();

        TimeConstraint t = new TimeConstraint(null, null, Duration.of(1, ChronoUnit.DAYS), Duration.of(5, ChronoUnit.DAYS));
        boolean satisfied = appointmentConstraintProvider.isTimeConstraintSatisfied(t,
                new Resource().setDurationMinutes(60).setChosenAvailability(ts(1, 12)),
                new Resource().setDurationMinutes(60).setChosenAvailability(ts(6, 10))); // difference is 5 days - 2 hours. the constraint is minute precise
        assertThat(satisfied).isTrue();

        t = new TimeConstraint(null, null, Duration.of(1, ChronoUnit.DAYS), Duration.of(5, ChronoUnit.DAYS));
        satisfied = appointmentConstraintProvider.isTimeConstraintSatisfied(t,
                new Resource().setDurationMinutes(60).setChosenAvailability(ts(1, 12)),
                new Resource().setDurationMinutes(60).setChosenAvailability(ts(6, 14)));  // now is 2 hours too late
        assertThat(satisfied).isFalse();


        t = new TimeConstraint(null, null, Duration.of(1, ChronoUnit.HOURS), Duration.of(3, ChronoUnit.DAYS));
        satisfied = appointmentConstraintProvider.isTimeConstraintSatisfied(t,
                new Resource().setDurationMinutes(180).setChosenAvailability(ts(1, 12)),
                new Resource().setDurationMinutes(60).setChosenAvailability(ts(1, 18)));
        assertThat(satisfied).isTrue();


        t = new TimeConstraint(null, null, Duration.of(6, ChronoUnit.HOURS), Duration.of(10, ChronoUnit.DAYS));
        satisfied = appointmentConstraintProvider.isTimeConstraintSatisfied(t,
                new Resource().setDurationMinutes(240).setChosenAvailability(ts(1, 14)),
                new Resource().setDurationMinutes(60).setChosenAvailability(ts(1, 18)));
        assertThat(satisfied).isFalse();

    }


}
