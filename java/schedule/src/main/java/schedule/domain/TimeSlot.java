package schedule.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TimeSlot {
    private LocalDateTime start;
    private LocalDateTime end;

    public TimeSlot(Date start, Date end) {
        this.start = toLocal(start);
        this.end = toLocal(end);
    }

    private LocalDateTime toLocal(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
