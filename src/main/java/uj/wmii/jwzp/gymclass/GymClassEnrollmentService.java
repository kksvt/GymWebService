package uj.wmii.jwzp.gymclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.exception.ResourceNotFoundException;
import uj.wmii.jwzp.gymclass.entities.GymClass;
import uj.wmii.jwzp.gymclass.entities.GymClassEnrollment;
import uj.wmii.jwzp.gymclass.entities.GymClassReview;
import uj.wmii.jwzp.user.Member;
import uj.wmii.jwzp.user.MemberRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GymClassEnrollmentService {

    private static Logger gymClassEnrollmentServiceLogger = LoggerFactory.getLogger(GymClassService.class);

    private final GymClassRepository gymClassRepository;
    private final GymClassEnrollmentRepository gymClassEnrollmentRepository;
    private final MemberRepository memberRepository;
    @Autowired
    public GymClassEnrollmentService(GymClassRepository gymClassRepository,
                           GymClassEnrollmentRepository gymClassEnrollmentRepository,
                                     MemberRepository memberRepository) {
        this.gymClassRepository = gymClassRepository;
        this.gymClassEnrollmentRepository = gymClassEnrollmentRepository;
        this.memberRepository = memberRepository;
    }

    public Optional<GymClassEnrollment> getEnrollment(Member member, GymClass gymClass) {
        gymClassEnrollmentServiceLogger.info("Getting enrollment for member with id: {}.", member.getId());
        return gymClass.getEnrolledMembers()
                .stream()
                .filter(enrollment -> enrollment.getMember() == member)
                .findFirst();
    }

    public List<GymClassEnrollment> getReviews(Long classId) {
        gymClassEnrollmentServiceLogger.info("Getting reviews for gym class with id: {}", classId);
        if (classId == null) {
            return gymClassEnrollmentRepository.findAll().stream().filter(e -> e.getRating() != null).toList();
        }
        return gymClassEnrollmentRepository
                .findAll()
                .stream()
                .filter(e -> e.getRating() != null && Objects.equals(e.getGymClass().getId(), classId))
                .toList();
    }

    public GymClass enrollMember(long gymClassId, Member member) throws ResourceNotFoundException {
        gymClassEnrollmentServiceLogger.info("Enrolling member with id: {} for gym class with id: {}", member.getId(), gymClassId);
        Optional<GymClass> gymClass = gymClassRepository.findById(gymClassId);
        if (gymClass.isEmpty()) {
            gymClassEnrollmentServiceLogger.error("Gym class with id: {} does not exist.", gymClassId);
            throw new ResourceNotFoundException("Gym class with id: " + gymClassId + " does not exist.");
        }
        if (!gymClass.get().canEnroll(member)) {
            gymClassEnrollmentServiceLogger.error("This member cannot enroll in this class.");
            throw new InvalidRequestException("This member cannot enroll in this class.");
        }
        GymClassEnrollment enrollment = new GymClassEnrollment(member, gymClass.get(), null, null);
        member.getEnrolledClasses().add(enrollment);
        gymClass.get().getEnrolledMembers().add(enrollment);
        gymClassEnrollmentRepository.save(enrollment);
        memberRepository.save(member);
        gymClassEnrollmentServiceLogger.info("Enrolled member with id: {}", member.getId());
        return gymClassRepository.save(gymClass.get());
    }

    public GymClassEnrollment addReview(Member member, Long gymClassId, GymClassReview review) {
        gymClassEnrollmentServiceLogger.info("Adding review from member with id: {}, for gym class with id: {}", member.getId(), gymClassId);
        Optional<GymClass> gymClass = gymClassRepository.findById(gymClassId);
        if (gymClass.isEmpty()) {
            gymClassEnrollmentServiceLogger.error("Gym class with id: {} does not exist", gymClassId);
            throw new ResourceNotFoundException("Gym class with id: " + gymClassId + " does not exist.");
        }
        Optional<GymClassEnrollment> enrollment = getEnrollment(member, gymClass.get());
        if (enrollment.isEmpty()) {
            gymClassEnrollmentServiceLogger.error("You are not enrolled in this class.");
            throw new InvalidRequestException("You are not enrolled in this class.");
        }
        if (enrollment.get().getRating() != null) {
            gymClassEnrollmentServiceLogger.error("You have already reviewed this class.");
            throw new InvalidRequestException("You have already reviewed this class.");
        }
        enrollment.get().setRating(review.rating());
        enrollment.get().setRatingDesc(review.ratingDesc());
        memberRepository.save(member);
        gymClassEnrollmentRepository.save(enrollment.get());
        gymClassEnrollmentServiceLogger.info("Added review from member with id: {}, for gym class with id: {}", member.getId(), gymClassId);
        return enrollment.get();
    }

    public void withdraw(Member member, Long gymClassId) {
        Optional<GymClass> gymClass = gymClassRepository.findById(gymClassId);
        gymClassEnrollmentServiceLogger.info("Withdrawing member with id: {} , from gym class with id: {}", member.getId(), gymClassId);
        if (gymClass.isEmpty()) {
            gymClassEnrollmentServiceLogger.error("Gym class with id: {} does not exist.", gymClassId);
            throw new ResourceNotFoundException("Gym class with id: " + gymClassId + " does not exist.");
        }
        Optional<GymClassEnrollment> enrollment = getEnrollment(member, gymClass.get());
        if (enrollment.isEmpty()) {
            gymClassEnrollmentServiceLogger.error("You are not enrolled in this class.");
            throw new InvalidRequestException("You are not enrolled in this class.");
        }
        member.getEnrolledClasses().remove(enrollment.get());
        gymClass.get().getEnrolledMembers().remove(enrollment.get());
        memberRepository.save(member);
        gymClassRepository.save(gymClass.get());
        gymClassEnrollmentRepository.delete(enrollment.get());
        gymClassEnrollmentServiceLogger.info("Withdrew member with id: {} , from gym class with id: {}", member.getId(), gymClassId);
    }

    public List<GymClass> getEnrolledClasses(Member member) {
        gymClassEnrollmentServiceLogger.info("Getting all member enrolled classes.");
        return member.getEnrolledClasses().stream().map(GymClassEnrollment::getGymClass).toList();
    }
}
