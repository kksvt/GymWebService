package uj.wmii.jwzp.gymclass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import uj.wmii.jwzp.common.Gender;
import uj.wmii.jwzp.config.timezone.TimeZoneService;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.exception.ResourceNotFoundException;
import uj.wmii.jwzp.gymclass.entities.GymClass;
import uj.wmii.jwzp.gymclass.entities.GymClassDate;
import uj.wmii.jwzp.gymclass.entities.GymClassLevel;
import uj.wmii.jwzp.gymclass.entities.GymClassType;
import uj.wmii.jwzp.user.Member;
import uj.wmii.jwzp.user.Role;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymClassServiceTest {

    @Mock
    private GymClassRepository gymClassRepository;
    @Mock
    private GymClassDateRepository gymClassDateRepository;

    @Mock
    private GymClassInstanceRepository gymClassInstanceRepository;

    @Mock
    private TimeZoneService timeZoneService;

    @Mock
    private GymClassValidator gymClassValidator;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GymClassService gymClassServiceTest;

    private Member createTrainerMember() {
        Member trainer = new Member("Tomek", "Tomaszewski", "tomek@tomaszewski.com", LocalDate.now(), "", Role.ROLE_TRAINER);
        trainer.extendMembership(1);
        return trainer;
    }

    @BeforeEach
    void setUp() {
        gymClassServiceTest = new GymClassService(gymClassRepository,gymClassDateRepository, gymClassInstanceRepository,
                timeZoneService,gymClassValidator,messageSource);
    }

    @Test
    void getClasses() {
        gymClassServiceTest.getClasses();//when

        verify(gymClassRepository).findAll();//then
    }

    @Test
    void addCorrectGymClass() {
        Member trainer = createTrainerMember();
        GymClass gymClass = new GymClass("Class 1", "Description 1", trainer, GymClassType.YOGA, GymClassLevel.ADVANCED, Gender.MALE);


        when(gymClassRepository.save(any(GymClass.class))).thenReturn(gymClass); // Mock the behavior of the repository

        GymClass result = gymClassServiceTest.addGymClass(gymClass);

        verify(gymClassRepository).save(argThat(savedGymClass ->
                savedGymClass.getName().equals("Class 1") &&
                        savedGymClass.getDescription().equals("Description 1") &&
                        savedGymClass.getInstructor().equals(trainer) &&
                        savedGymClass.getType() == GymClassType.YOGA &&
                        savedGymClass.getLevel() == GymClassLevel.ADVANCED &&
                        savedGymClass.getGender() == Gender.MALE));

        assertThat(result).isEqualTo(gymClass);
    }

    @Test
    void getGymClassByExistingId() {
        long gymClassId = 1L;
        GymClass expectedGymClass = new GymClass("Class 1", "Description", createTrainerMember(), GymClassType.YOGA, GymClassLevel.BEGINNER, Gender.FEMALE);
        when(gymClassRepository.findById(gymClassId)).thenReturn(Optional.of(expectedGymClass));

        Optional<GymClass> result = gymClassServiceTest.getClass(gymClassId);

        assertTrue(result.isPresent());
        assertEquals(expectedGymClass, result.get());
    }

    @Test
    void getClassByNonExistingId() {
        long gymClassId = 1L;
        when(gymClassRepository.findById(gymClassId)).thenReturn(Optional.empty());

        Optional<GymClass> result = gymClassServiceTest.getClass(gymClassId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getClassByExistingName() {
        String className = "Class 1";
        GymClass expectedGymClass = new GymClass("Class 1", "Description", createTrainerMember(), GymClassType.YOGA, GymClassLevel.BEGINNER, Gender.FEMALE);
        when(gymClassRepository.findByName(className)).thenReturn(Optional.of(expectedGymClass));

        Optional<GymClass> result = gymClassServiceTest.getClass(className);

        assertTrue(result.isPresent());
        assertEquals(expectedGymClass, result.get());
    }

    @Test
    void getClassByNonExistingName() {
        String className = "Class 1";
        when(gymClassRepository.findByName(className)).thenReturn(Optional.empty());

        Optional<GymClass> result = gymClassServiceTest.getClass(className);

        assertTrue(result.isEmpty());
    }

    @Test
    void addValidGymClass() {
        GymClass gymClass = new GymClass("Class 1", "Description", createTrainerMember(), GymClassType.YOGA, GymClassLevel.BEGINNER, Gender.FEMALE);
        when(gymClassRepository.save(gymClass)).thenReturn(gymClass);

        GymClass result = gymClassServiceTest.addGymClass(gymClass);

        assertNotNull(result);
        assertEquals(gymClass, result);
    }

    @Test
    void addValidDateToClass() {
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        long gymClassId = 100L;
        GymClassDate date = new GymClassDate(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        GymClass gymClass = new GymClass("Class 1", "Description", createTrainerMember(), GymClassType.YOGA, GymClassLevel.BEGINNER, Gender.FEMALE);

        when(gymClassRepository.findById(gymClassId)).thenReturn(Optional.of(gymClass));
        when(timeZoneService.getZoneId()).thenReturn(zoneId);
        when(gymClassRepository.save(any(GymClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GymClass addedClass = gymClassServiceTest.addDateToClass(zoneId, gymClassId, date);

        assertNotNull(addedClass);
        assertTrue(addedClass.getDates().contains(date));
        assertEquals(gymClass, addedClass);
    }

    @Test
    void addDateToClassWithInvalidGymClassId() {
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        long gymClassId = 1L;
        GymClassDate date = new GymClassDate(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(gymClassRepository.findById(gymClassId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gymClassServiceTest.addDateToClass(zoneId, gymClassId, date));
    }

    @Test
    void addDateToClassWithExistingDate() {
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        long gymClassId = 1L;
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        GymClassDate date = new GymClassDate(DayOfWeek.FRIDAY, startTime, endTime);
        GymClass gymClass = new GymClass("Class 1", "Description", createTrainerMember(), GymClassType.YOGA, GymClassLevel.BEGINNER, Gender.FEMALE);
        gymClass.addDate(date);
        when(gymClassRepository.findById(gymClassId)).thenReturn(Optional.of(gymClass));
        when(timeZoneService.getZoneId()).thenReturn(zoneId);


        assertThrows(InvalidRequestException.class, () -> gymClassServiceTest.addDateToClass(zoneId, gymClassId, date));
    }
}
