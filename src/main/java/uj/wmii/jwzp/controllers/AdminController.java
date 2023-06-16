package uj.wmii.jwzp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.exception.ResourceNotFoundException;
import uj.wmii.jwzp.gymclass.GymClassEnrollmentService;
import uj.wmii.jwzp.gymclass.GymClassService;
import uj.wmii.jwzp.gymclass.entities.*;
import uj.wmii.jwzp.user.Member;
import uj.wmii.jwzp.user.MemberService;
import uj.wmii.jwzp.user.ProfileChangeRequest;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final GymClassService gymClassService;

    private final GymClassEnrollmentService gymClassEnrollmentService;

    private final MemberService memberService;

    @Autowired
    public AdminController(GymClassService gymClassService,
                           GymClassEnrollmentService gymClassEnrollmentService,
                           MemberService memberService) {
        this.gymClassService = gymClassService;
        this.gymClassEnrollmentService = gymClassEnrollmentService;
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public List<Member> getMembers(){
        return memberService.getMembers();
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable Long memberId) throws InvalidRequestException {
        Optional<Member> member = memberService.getMember(memberId);
        return member.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long memberId) throws InvalidRequestException {
        memberService.removeMember(memberId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/member/{memberId}")
    public ResponseEntity<Member> modifyMember(
            ZoneId zoneId,
            @PathVariable Long memberId,
            @RequestBody ProfileChangeRequest profileChangeRequest) throws ResourceNotFoundException {
        Member member = memberService.getMember(memberId).orElseThrow(() -> new ResourceNotFoundException("Member does not exist."));
        memberService.modifyProfile(zoneId, member, profileChangeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @GetMapping("/classes/{classId}/members")
    public ResponseEntity<List<Member>> getEnrolledMembers(@PathVariable long classId) throws ResourceNotFoundException {
        return ResponseEntity.ok(gymClassService.getEnrolledMembers(classId));
    }

    @PostMapping("/classes")
    public ResponseEntity<GymClass> postClass(@RequestBody GymClassRequest gymClassRequest) throws InvalidRequestException {
        Member instructor = memberService.getInstructor(gymClassRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(gymClassService.addGymClass(gymClassRequest, instructor));
    }

    @PostMapping("/classes/{classId}/dates")
    public ResponseEntity<GymClass> addClassDate(ZoneId zoneId, @PathVariable long classId, @RequestBody GymClassDate classDate) {
        return ResponseEntity.
                status(HttpStatus.CREATED).
                body(gymClassService.addDateToClass(zoneId, classId, classDate));
    }

    @DeleteMapping("/classes/{classId}")
    public ResponseEntity<Void> removeClass(@PathVariable long classId) {
        gymClassService.removeGymClass(classId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/classes/{classId}/dates/{dateId}")
    public ResponseEntity<Void> removeClassDate(
            @PathVariable("classId") long classId,
            @PathVariable("dateId") long dateId) {
        gymClassService.removeGymClassDate(classId, dateId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/classes/{classId}/instances")
    public ResponseEntity<GymClassInstance> addGymClassInstance(
            ZoneId zoneId,
            @PathVariable("classId") long classId,
            @RequestBody GymClassInstanceRequest gymClassInstanceRequest) {
        return ResponseEntity.ok(gymClassService.addGymClassInstance(
                zoneId,
                classId,
                gymClassInstanceRequest.start(),
                gymClassInstanceRequest.end())
        );
    }

    @DeleteMapping("/classes/{classId}/instances/{instanceId}")
    public ResponseEntity<Void> removeGymClassInstance(
            @PathVariable("classId") long classId,
            @PathVariable("instanceId") long instanceId) {
        gymClassService.removeGymClassInstance(instanceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/instances")
    public ResponseEntity<Void> runGymClassInstanceAdder() {
        gymClassService.createGymClassInstancesForCurrentWeek();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleMemberNotFoundException(InvalidRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
