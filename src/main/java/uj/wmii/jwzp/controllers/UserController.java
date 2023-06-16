package uj.wmii.jwzp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import uj.wmii.jwzp.common.Gender;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.exception.ResourceNotFoundException;
import uj.wmii.jwzp.gymclass.GymClassEnrollmentService;
import uj.wmii.jwzp.gymclass.GymClassService;
import uj.wmii.jwzp.gymclass.entities.*;
import uj.wmii.jwzp.user.*;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/* Non admin endpoints */
@RestController
public class UserController {

    private final MemberService memberService;

    private final GymClassService gymClassService;

    private final GymClassEnrollmentService gymClassEnrollmentService;

    private final TrainerReviewService trainerReviewService;

    private Member getMemberFromToken() throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Member)memberService.loadUserByUsername(authentication.getName());
    }

    @Autowired
    public UserController(MemberService memberService,
                          GymClassService gymClassService,
                          GymClassEnrollmentService gymClassEnrollmentService,
                          TrainerReviewService trainerReviewService){
        this.memberService = memberService;
        this.gymClassService = gymClassService;
        this.gymClassEnrollmentService = gymClassEnrollmentService;
        this.trainerReviewService = trainerReviewService;
    }

    /*does not require authentication*/
    @GetMapping("/classes")
    public ResponseEntity<List<GymClassDetailDTO>> getGymClasses(
            @RequestParam(value = "type", required = false) GymClassType type,
            @RequestParam(value = "gender", required = false) Gender gender,
            @RequestParam(value = "level", required = false) GymClassLevel level
    ) throws InvalidRequestException {
        List<GymClassDetailDTO> filtered = gymClassService.getClasses().stream().filter(gymClass -> {
            if (type != null && type != gymClass.getType())
                return false;
            if (gender != null && gender != gymClass.getGender())
                return false;
            if (level != null && level != gymClass.getLevel())
                return false;
            return true;
        }).map(gymClassService::convertToDetailDTO).toList();
        return ResponseEntity.ok(filtered);
    }

    /*does not require authentication*/
    @GetMapping("/classes/{classId}")
    public ResponseEntity<GymClassDetailDTO> getSingleClass(@PathVariable long classId) throws InvalidRequestException {
        Optional<GymClass> gymClass = gymClassService.getClass(classId);
        if (gymClass.isEmpty()) {
            throw new ResourceNotFoundException(("Gym class with id " + classId + " not found"));
        }
        return ResponseEntity.ok(gymClassService.convertToDetailDTO(gymClass.get()));

    }

    @PostMapping("/classes/{classId}")
    public ResponseEntity<String> signUpForClass(
            @PathVariable("classId") long classId) throws UsernameNotFoundException {
        Member member = getMemberFromToken();
        GymClass gymClass = gymClassEnrollmentService.enrollMember(classId, member);
        return ResponseEntity.status(HttpStatus.CREATED).
                body("You've been successfully enrolled in " + gymClass.getName() + "!");
    }

    @GetMapping("/profile")
    public ResponseEntity<Member> viewProfile() throws UsernameNotFoundException {
        Member member = getMemberFromToken();
        return ResponseEntity.ok(member);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Member> modifyProfile(ZoneId zoneId, @RequestBody ProfileChangeRequest profileChangeRequest) {
        Member member = getMemberFromToken();
        memberService.modifyProfile(zoneId, member, profileChangeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PatchMapping("/profile/cancelMembership")
    public ResponseEntity<String> cancelMembership(){
        Member member = getMemberFromToken();
        member.setActive(false);
        memberService.updateMember(member);
        return ResponseEntity.status(HttpStatus.OK).body("You have successfully canceled your membership.");
    }

    @GetMapping("/profile/classes")
    public ResponseEntity<List<GymClass>> getMemberClasses() {
        Member member = getMemberFromToken();
        return ResponseEntity.ok(gymClassEnrollmentService.getEnrolledClasses(member));
    }

    @DeleteMapping("/profile/classes/{classId}")
    public ResponseEntity<String> withdrawFromAClass(@PathVariable Long classId) {
        Member member = getMemberFromToken();
        gymClassEnrollmentService.withdraw(member, classId);
        return ResponseEntity.ok("You've successfully withdrawn from this class.");
    }

    @GetMapping("/trainers")
    public List<MemberDTO> getTrainers(){
        return memberService.getMembers()
                .stream()
                .filter(member -> member.getRole() == Role.ROLE_TRAINER)
                .map(memberService::convertToDTO)
                .toList();
    }

    @GetMapping("/trainers/{trainerId}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Long trainerId) throws InvalidRequestException {
        Optional<Member> member = memberService.getMember(trainerId);
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("This trainer does not exist.");
        }
        if (member.get().getRole() != Role.ROLE_TRAINER) {
            throw new InvalidRequestException("The given user is not a trainer.");
        }
        return ResponseEntity.ok(memberService.convertToDTO(member.get()));
    }

    @GetMapping("/reviews/trainers")
    public ResponseEntity<List<TrainerReview>> getReviews(@RequestParam(value = "trainerId", required = false) Long trainerId) {
        return ResponseEntity.ok(trainerReviewService.getReviews(trainerId));
    }

    @PostMapping("/reviews/trainers/{trainerId}")
    public ResponseEntity<TrainerReview> addReview(@PathVariable Long trainerId,
                                                   @RequestBody TrainerReviewRequest trainerReviewRequest)
            throws UsernameNotFoundException {
        Member member = getMemberFromToken();
        return ResponseEntity.ok(trainerReviewService.addReview(member, trainerId, trainerReviewRequest));
    }

    @GetMapping("/reviews/classes")
    public ResponseEntity<List<GymClassEnrollment>> getClassReviews(@RequestParam(value = "classId", required = false) Long classId) {
        return ResponseEntity.ok(gymClassEnrollmentService.getReviews(classId));
    }

    @PostMapping("/reviews/classes/{classId}")
    public ResponseEntity<GymClassEnrollment> addClassReview(@PathVariable Long classId,
                                                   @RequestBody GymClassReview gymClassReview)
            throws UsernameNotFoundException {
        Member member = getMemberFromToken();
        return ResponseEntity.ok(gymClassEnrollmentService.addReview(member, classId,  gymClassReview));
    }

    @GetMapping("/classes/{classId}/instances")
    public ResponseEntity<List<GymClassInstance>> getGymClassInstances(@PathVariable Long classId) {
        return ResponseEntity.ok(gymClassService.getClassInstancesFromClass(classId));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
