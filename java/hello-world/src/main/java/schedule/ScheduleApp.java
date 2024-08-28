package schedule;

import ai.timefold.solver.core.api.score.ScoreExplanation;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicType;
import ai.timefold.solver.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import ai.timefold.solver.core.config.exhaustivesearch.ExhaustiveSearchType;
import ai.timefold.solver.core.config.exhaustivesearch.NodeExplorationType;
import ai.timefold.solver.core.config.localsearch.LocalSearchPhaseConfig;
import ai.timefold.solver.core.config.localsearch.LocalSearchType;
import ai.timefold.solver.core.config.solver.SolverConfig;
import schedule.solver.AppointmentConstraintProvider;
import schedule.solver.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.domain.Resource;
import schedule.domain.TimeConstraint;
import schedule.domain.TimeSlot;
import schedule.factory.FactoryTimeSlot;

import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ScheduleApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleApp.class);

    public static void main(String[] args) throws ParseException {
        Schedule problem = generateDemoDataAvailability();
        Solution solution = ScheduleApp.solveSchedule(problem);
        printTimetable(solution.schedule);
        System.out.println(solution.scoreExplanation.getSummary());
    }

    public static Solution solveSchedule(Schedule problem){
        return solveSchedule(problem, 2);
    }

    public static Solution solveSchedule(Schedule problem, int seconds){

        ExhaustiveSearchPhaseConfig exhaustiveSearchPhaseConfig = new ExhaustiveSearchPhaseConfig();
        exhaustiveSearchPhaseConfig.setExhaustiveSearchType(ExhaustiveSearchType.BRANCH_AND_BOUND);
        exhaustiveSearchPhaseConfig.setNodeExplorationType(NodeExplorationType.SCORE_FIRST);

        ConstructionHeuristicPhaseConfig phaseConfig = new ConstructionHeuristicPhaseConfig();
        phaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT_DECREASING);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);

        SolverFactory<Schedule> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(Schedule.class)
                .withEntityClasses(Resource.class)
                .withConstraintProviderClass(AppointmentConstraintProvider.class)
                .withPhases(exhaustiveSearchPhaseConfig)
                //.withPhases(phaseConfig, localSearchPhaseConfig)
                .withTerminationSpentLimit(Duration.ofSeconds(seconds)));

        Solver<Schedule> solver = solverFactory.buildSolver();
        Schedule solution = solver.solve(problem);

        printTimetable(solution);

        SolutionManager<Schedule, HardSoftScore> scoreManager = SolutionManager.create(solverFactory);
        ScoreExplanation<Schedule, HardSoftScore> scoreExplanation = scoreManager.explain(solution);

        return new Solution(solution, scoreExplanation);
    }

    public static Schedule generateDemoDataAvailability() throws ParseException {

        List<Resource> resources = new ArrayList<>();

        List<TimeSlot> slotsMainAppointment = FactoryTimeSlot.createSlots("2024-1-1", "2024-4-1", 60);
        List<TimeSlot> slots1 = FactoryTimeSlot.createSlots("2024-1-1", "2024-3-1", 15);
         List<TimeSlot> slots2 = FactoryTimeSlot.createSlots("2024-1-1", "2024-3-1", 60);

//        List<TimeSlot> slotsMainAppointment = FactoryTimeSlot.createSlots("2024-3-25", "2024-5-26", 180);
//        List<TimeSlot> slots1 = FactoryTimeSlot.createSlots("2024-3-1", "2024-5-26", 15);
//        List<TimeSlot> slots2 = FactoryTimeSlot.createSlots("2024-3-1", "2024-5-26", 60);

        resources.add(new Resource("0stCheckIn", 60, slots1));
        resources.add(new Resource("1stCheckIn", 60, slots1));
        resources.add(new Resource("2ndCheckIn", 60, slots1));
        resources.add(new Resource("Main Operation", 240, slotsMainAppointment));
        resources.add(new Resource("SimultaneousResource", 30, slots1));
        resources.add(new Resource("1stCheckOut", 60, slots2));
        // resources.add(new Resource("CheckOut2", 60, slots2));

        List<TimeConstraint> timeConstraints = new ArrayList<>();

        timeConstraints.add(new TimeConstraint("0stCheckIn", "Main Operation", Duration.ofDays(5), Duration.ofDays(10)));
        timeConstraints.add(new TimeConstraint("0stCheckIn", "1stCheckIn", Duration.ofMinutes(60), null));
        timeConstraints.add(new TimeConstraint("1stCheckIn", "Main Operation", Duration.ofDays(5), Duration.ofDays(10)));
        timeConstraints.add(new TimeConstraint("2ndCheckIn", "Main Operation", Duration.ofDays(1), Duration.ofDays(1)));
        timeConstraints.add(new TimeConstraint("SimultaneousResource", "Main Operation", Duration.ofMinutes(30), Duration.ofMinutes(30)));
        timeConstraints.add(new TimeConstraint("Main Operation", "1stCheckOut", Duration.ofDays(5), Duration.ofDays(8)));

//        List<TaggableResource> docs = new ArrayList<>();
//        TaggableResource resDoctor = new TaggableResource(""+id++, "Doctor John", FactoryTimeSlot.createFromDays(new int[]{2, 4}), null, "doctor");
//        TaggableResource resDoctor2 = new TaggableResource(""+id++, "Doctor Mario", FactoryTimeSlot.createFromDays(new int[]{1}), null, "doctor");
//        TaggableResource nurse = new TaggableResource(""+id++, "Nurse Maria", FactoryTimeSlot.create(1, 1, 30), null, "nurse");
//        docs.add(resDoctor);
//        docs.add(resDoctor2);
//        docs.add(nurse);

        Schedule schedule = new Schedule(timeConstraints, resources); // , docs);
        // schedule.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-1-1"));
        return schedule;
    }

    private static void printTimetable(Schedule cp) {
        LOGGER.info("");
        for (Resource resource : cp.getResources()) {
            LOGGER.info(resource.toString());
        }

    }

}
