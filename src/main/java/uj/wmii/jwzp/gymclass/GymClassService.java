package uj.wmii.jwzp.gymclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uj.wmii.jwzp.config.timezone.TimeZoneService;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.exception.ResourceNotFoundException;
import uj.wmii.jwzp.gymclass.entities.*;
import uj.wmii.jwzp.user.Member;
import uj.wmii.jwzp.user.MemberDTO;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class GymClassService {

    private final static Logger gymClassServiceLogger = LoggerFactory.getLogger(GymClassService.class);

    private final GymClassRepository gymClassRepository;
    private final GymClassDateRepository gymClassDateRepository;

    private final GymClassInstanceRepository gymClassInstanceRepository;

    private final TimeZoneService timeZoneService;

    private final GymClassValidator gymClassValidator;

    private final MessageSource messageSource;
    @Autowired
    public GymClassService(GymClassRepository gymClassRepository,
                           GymClassDateRepository gymClassDateRepository,
                           GymClassInstanceRepository gymClassInstanceRepository,
                           TimeZoneService timeZoneService,
                           GymClassValidator gymClassValidator,
                           MessageSource messageSource) {
        this.gymClassRepository = gymClassRepository;
        this.gymClassDateRepository = gymClassDateRepository;
        this.timeZoneService = timeZoneService;
        this.gymClassValidator = gymClassValidator;
        this.messageSource = messageSource;
        this.gymClassInstanceRepository = gymClassInstanceRepository;
    }

    public List<GymClass> getClasses(){
        gymClassServiceLogger.info("Getting all gym classes...");
        return gymClassRepository.findAll();
    }

    public Optional<GymClass> getClass(long gymClassId)  {
        gymClassServiceLogger.info("Getting gym class by id: {}", gymClassId);
        return gymClassRepository.findById(gymClassId);
    }

    public Optional<GymClassInstance> getClassInstance(long gymClassInstanceId) {
        gymClassServiceLogger.info("Getting gym class instance by id: {}", gymClassInstanceId);
        return gymClassInstanceRepository.findById(gymClassInstanceId);
    }

    public List<GymClassInstance> getClassInstancesFromClass(long gymClassId) throws ResourceNotFoundException {
        Optional<GymClass> gymClass = getClass(gymClassId);
        if (gymClass.isEmpty()) {
            throw new ResourceNotFoundException("Gym class with id " + gymClassId + " does not exist.");
        }
        return gymClass.get().getGymClassInstances()
                .stream()
                .sorted(Comparator.comparing(GymClassInstance::getStartTime))
                .toList();
    }

    public Optional<GymClass> getClass(String name) {
        gymClassServiceLogger.info("Getting gym class by name: {}", name);
        return gymClassRepository.findByName(name);
    }

    public List<Member> getEnrolledMembers(long gymClassId) throws ResourceNotFoundException {
        Optional<GymClass> gymClass = getClass(gymClassId);
        if (gymClass.isEmpty()) {
            throw new ResourceNotFoundException(("Gym class with id " + gymClassId + " not found"));
        }
        return gymClass.get()
                .getEnrolledMembers()
                .stream()
                .map(GymClassEnrollment::getMember)
                .toList();
    }

    public GymClass addGymClass(GymClass gymClass) {
        BindingResult result = new BeanPropertyBindingResult(gymClass, "gymClass");
        gymClassValidator.validate(gymClass, result);
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(error -> messageSource.getMessage(error.getCode(), null, Locale.getDefault()))
                    .toList();
            gymClassServiceLogger.info("GymClass error: {}", errorMessages);
            throw new InvalidRequestException("GymClass error: " + errorMessages);
        }
        gymClassServiceLogger.info("Adding gym class with id: {}", gymClass.getId());
        return gymClassRepository.save(gymClass);
    }

    public GymClass addDateToClass(ZoneId zoneId, long gymClassId, GymClassDate date) {
        Optional<GymClass> gymClass = getClass(gymClassId);
        if (gymClass.isEmpty()) {
            gymClassServiceLogger.error("Gym class with id: {} does not exist.", gymClassId);
            throw new ResourceNotFoundException("Gym class with id: " + gymClassId + " does not exist.");
        }
        //fixme: there surely has to be a less atrocious way of doing this
        ZonedDateTime zonedDateTime = ZonedDateTime.
                of(LocalDateTime.now(zoneId), zoneId).
                with(TemporalAdjusters.next(date.getDayOfWeek()));
        ZonedDateTime zonedStartTime = zonedDateTime.
                with(date.getStartTime()).
                withZoneSameInstant(timeZoneService.getZoneId());
        ZonedDateTime zonedEndTime = zonedDateTime.
                with(date.getEndTime()).
                withZoneSameInstant(timeZoneService.getZoneId());
        date.setStartTime(zonedStartTime.toLocalTime());
        date.setEndTime(zonedEndTime.toLocalTime());
        if (gymClass.get().dateExists(date)) {
            gymClassServiceLogger.error("This gym class already has the given date and time.");
            throw new InvalidRequestException("This gym class already has the given date and time.");
        }
        gymClass.get().addDate(date);
        date.setGymClass(gymClass.get());
        gymClassServiceLogger.info("Adding date to gym class with id: {} ...", gymClassId);
        gymClassRepository.save(gymClass.get());
        gymClassServiceLogger.info("Added date to gym class with id: {} ...", gymClassId);
        //gymClassDateRepository.save(date);
        return gymClass.get();
    }

    public void removeGymClass(long gymClassId) throws ResourceNotFoundException {
        if(!gymClassRepository.existsById(gymClassId)){
            gymClassServiceLogger.error("Gym class with id: {} does not exist.", gymClassId);
            throw new ResourceNotFoundException("Gym class with id: " + gymClassId + " does not exist.");
        }
        gymClassServiceLogger.info("Removing gym class with id: {}", gymClassId);
        gymClassRepository.deleteById(gymClassId);
        gymClassServiceLogger.info("Removed gym class with id: {}", gymClassId);
    }

    public void removeGymClassDate(long gymClassId, long gymClassDateId) throws ResourceNotFoundException {
        Optional<GymClass> gymClass = getClass(gymClassId);
        if (gymClass.isEmpty()) {
            gymClassServiceLogger.error("Gym class with id: {} does not exist.", gymClassId);
            throw new ResourceNotFoundException("Gym class with id: " + gymClassId + " does not exist.");
        }
        Optional<GymClassDate> gymClassDate = gymClassDateRepository.findById(gymClassDateId);
        if (gymClassDate.isEmpty()) {
            gymClassServiceLogger.error("Gym class date with id: {} does not exist.", gymClassDateId);
            throw new ResourceNotFoundException("Gym class date with id: " + gymClassDateId + " does not exist.");
        }
        if (!gymClass.get().dateExists(gymClassDate.get())) {
            gymClassServiceLogger.error("Gym class date with id: {} is not linked to gym class with id: {}" ,gymClassDateId, gymClassId);
            throw new ResourceNotFoundException("Gym class date with id: " + gymClassDateId + " is not linked to gym class with id: " + gymClassId);
        }
        gymClassServiceLogger.warn("Removing date with id: {} from gym class with id: {}", gymClassDateId, gymClassId);
        gymClass.get().removeDate(gymClassDate.get());
        gymClassRepository.save(gymClass.get());
        gymClassDateRepository.delete(gymClassDate.get());
        gymClassServiceLogger.info("Removed date with id: {} from gym class with id: {}", gymClassDateId, gymClassId);
    }

    public GymClassInstance addGymClassInstance(ZoneId zoneId, long gymClassId, LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            gymClassServiceLogger.error("Gym Class Instance's endTime cannot be before its startTime.");
            throw new InvalidRequestException("Gym Class Instance's endTime cannot be before its startTime.");
        }
        Optional<GymClass> gymClass = getClass(gymClassId);
        if (gymClass.isEmpty()) {
            gymClassServiceLogger.error("Gym class with id: {} does not exist.", gymClassId);
            throw new ResourceNotFoundException("Gym class with id: " + gymClassId + " does not exist.");
        }
        //fixme: there surely has to be a less atrocious way of doing this
        LocalDateTime adjustedStartTime = ZonedDateTime
                .of(startTime, zoneId)
                .withZoneSameInstant(timeZoneService.getZoneId())
                .toLocalDateTime();
        LocalDateTime adjustedEndTime = ZonedDateTime
                .of(endTime, zoneId)
                .withZoneSameInstant(timeZoneService.getZoneId())
                .toLocalDateTime();
        GymClassInstance gymClassInstance = new GymClassInstance(gymClass.get(), adjustedStartTime, adjustedEndTime);
        gymClassServiceLogger.info("Creating a gym class instance for gym class id: {} ...", gymClassId);
        gymClassInstanceRepository.save(gymClassInstance);
        gymClassServiceLogger.info("Created a gym class instance for gym class id: {} ...", gymClassId);
        return gymClassInstance;
    }

    public void removeGymClassInstance(long gymClassInstanceId) {
        Optional<GymClassInstance> gymClassInstance = getClassInstance(gymClassInstanceId);
        if (gymClassInstance.isEmpty()) {
            gymClassServiceLogger.error("Gym class instance with id: {} does not exist.", gymClassInstanceId);
            throw new ResourceNotFoundException("Gym class instance with id: " + gymClassInstanceId + " does not exist.");
        }
        GymClass gymClass = gymClassInstance.get().getGymClass();
        gymClass.getGymClassInstances().remove(gymClassInstance.get());
        gymClassInstanceRepository.delete(gymClassInstance.get());
    }

    public GymClassInstance addGymClassInstanceFromGymClassDate(GymClassDate gymClassDate) {
        GymClassInstance gymClassInstance = new GymClassInstance(gymClassDate);
        return gymClassInstanceRepository.save(gymClassInstance);
    }

    public GymClass addGymClass(GymClassRequest gymClassRequest, Member instructor) {
        if (getClass(gymClassRequest.name()).isPresent()) {
            throw new InvalidRequestException("Gym class named " + gymClassRequest.name() + " already exists");
        }
        GymClass gymClass = new GymClass(gymClassRequest.name(), gymClassRequest.description(),
                instructor, gymClassRequest.type(),
                gymClassRequest.level(), gymClassRequest.gender());
        gymClassRepository.save(gymClass);
        return gymClass;
    }

    public void createGymClassInstancesForCurrentWeek() {
        getClasses().forEach(
                gymClass -> {
                    gymClass.getDates().forEach(
                            this::addGymClassInstanceFromGymClassDate
                    );
                }
        );
    }

    public GymClassSummaryDTO convertToSummaryDTO(GymClass gymClass) {
        return new GymClassSummaryDTO(
                gymClass.getId(),
                gymClass.getName(),
                gymClass.getType(),
                gymClass.getLevel(),
                gymClass.getGender()
        );
    }

    public GymClassDetailDTO convertToDetailDTO(GymClass gymClass) {
        return new GymClassDetailDTO(
                gymClass.getId(),
                gymClass.getName(),
                gymClass.getDescription(),
                new MemberDTO(
                        gymClass.getInstructor().getId(),
                        gymClass.getInstructor().getFirstName(),
                        gymClass.getInstructor().getLastName(),
                        null),
                gymClass.getType(),
                gymClass.getLevel(),
                gymClass.getGender(),
                gymClass.getDates()
        );
    }
}
