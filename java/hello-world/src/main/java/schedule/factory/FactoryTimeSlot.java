package schedule.factory;

import schedule.domain.TimeSlot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FactoryTimeSlot {

    private static int freeSlotsFromHours = 7;
    private static int freeSlotsUntilHours = 20;

    public static TimeSlot create(int day) {
        return new TimeSlot(LocalDateTime.of(2024, 1, day, 0, 0), LocalDateTime.of(2024, 1, day, 23, 0));
    }

    public static TimeSlot create(int month, int day, int fromH, int toH) {
        return new TimeSlot(LocalDateTime.of(2024, month, day, fromH, 0), LocalDateTime.of(2024, month, day, toH, 0));
    }

    private static Date date(String s) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<TimeSlot> createSlots(String dateStartString, String dateEndString, int granularityMinutes) throws ParseException {
        List<TimeSlot> slots = new ArrayList<>();
        Date dateStart = date(dateStartString);
        Date dateEnd = date(dateEndString);
        LocalDateTime startDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime start = startDate.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime end = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        while (!start.isAfter(end)) {
            if (start.getHour() >= freeSlotsFromHours && start.getHour() <= freeSlotsUntilHours) {
                slots.add(new TimeSlot(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()), Date.from(start.plusMinutes(granularityMinutes).atZone(ZoneId.systemDefault()).toInstant())));
            }
            start = start.plusMinutes(granularityMinutes);
        }

        return slots;
    }

    public static List<TimeSlot> createFromDaysAndHours(String[] daysHours) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        for (int i = 0; i < daysHours.length; i++) {
            int[] daysHoursInstance = Arrays.stream(daysHours[i].split(",")).map(s -> s.trim()).mapToInt(Integer::parseInt).toArray();
            timeSlots.add(new TimeSlot(LocalDateTime.of(2024, 1, daysHoursInstance[0], daysHoursInstance[1], 0), LocalDateTime.of(2024, 1, daysHoursInstance[0], daysHoursInstance[2], 0)));
        }
        return timeSlots;
    }

    public static List<TimeSlot> createFromDays(int[] days) {
        String[] daysHours = new String[days.length];
        for (int i = 0; i < days.length; i++) {
            daysHours[i] = days[i] + ",0,23";
        }
        return createFromDaysAndHours(daysHours);
    }

    public static List<TimeSlot> create(int day, int intervalDays, int numOccurrences) {
        return create(1, day, 0, 23, intervalDays, numOccurrences);
    }

    public static List<TimeSlot> create(int month, int day, int fromH, int toH, int intervalDays, int numOccurrences) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2024, month, day, fromH, 0);
        for (int i = 0; i < numOccurrences; i++) {
            LocalDateTime end = start.plusHours(toH - fromH);
            timeSlots.add(new TimeSlot(start, end));
            start = start.plusDays(intervalDays);
        }
        return timeSlots;
    }

}
