package uj.wmii.jwzp.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uj.wmii.jwzp.config.timezone.TimeZoneService;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.exception.ResourceNotFoundException;
import uj.wmii.jwzp.gymclass.entities.GymClassRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    private final static Logger memberServiceLogger = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;

    private final TimeZoneService timeZoneService;

    private final MessageSource messageSource;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    public MemberService(MemberRepository memberRepository,
                         TimeZoneService timeZoneService,
                         MessageSource messageSource,
                         PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.timeZoneService = timeZoneService;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Member> getMembers(){
        memberServiceLogger.info("Getting all members from database.");
        return memberRepository.findAll();
    }

    public Optional<Member> getMember(Long memberId) {
        memberServiceLogger.info("Getting member by id: {}", memberId);
        return memberRepository.findById(memberId);
    }

    public void addMember(ZoneId zoneId, Member member) {
        member.fixMembershipDateStart(zoneId, timeZoneService.getZoneId());
        member.extendMembership(1);
        memberRepository.save(member);
        memberServiceLogger.info("Added member with id: {}", member.getId());
    }

    public void removeMember(Long memberId) throws ResourceNotFoundException {
        if(!memberRepository.existsById(memberId)){
            memberServiceLogger.error("Member with id: {} does not exist." , memberId);
            throw new ResourceNotFoundException("Member with id: " + memberId + " does not exist.");
        }
        memberRepository.deleteById(memberId);
        memberServiceLogger.info("Member with id: {} removed.", memberId);
    }

    public void modifyProfile(ZoneId zoneId, Member member, ProfileChangeRequest profileChangeRequest) throws InvalidRequestException {
        boolean modified = false;
        memberServiceLogger.info("Starting modifying member profile (member id: {}).", member.getId());
        if (profileChangeRequest.firstName() != null) {
            member.setFirstName(profileChangeRequest.firstName());
            memberServiceLogger.info("Modified first name.");
            modified = true;
        }
        if (profileChangeRequest.lastName() != null) {
            member.setLastName(profileChangeRequest.lastName());
            memberServiceLogger.info("Modified last name.");
            modified = true;
        }
        if (profileChangeRequest.password() != null) {
            member.setPassword(passwordEncoder.encode(profileChangeRequest.password()));
            memberServiceLogger.info("Modified password.");
            modified = true;
        }
        if (profileChangeRequest.membershipDateEnd() != null) {
            member.setMembershipDateEnd(profileChangeRequest.membershipDateEnd());
            member.fixMembershipDateStart(zoneId, timeZoneService.getZoneId());
            if (member.getMembershipDateEnd().isBefore(member.getMembershipDateStart()) ||
                    member.getMembershipDateEnd().isBefore(LocalDate.now(timeZoneService.getZoneId()))) {
                memberServiceLogger.error("Invalid membership date end.");
                throw new InvalidRequestException("Invalid membership date end.");
            }
            memberServiceLogger.info("Modified membership.");
            modified = true;
        }
        if (modified) {
            memberRepository.save(member);
            memberServiceLogger.info("Modified profile has been saved successfully.");
        }
    }

    public void updateMember(Member member){
        memberRepository.save(member);
        memberServiceLogger.info("Update member has been saved successfully.");
    }

    public Member getInstructor(GymClassRequest gymClassRequest){
        memberServiceLogger.info("Getting trainer: {}", gymClassRequest.instructor());
        Optional<Member> instructor = this.getMembers()
                .stream()
                .filter(member -> member.getRole() == Role.ROLE_TRAINER &&
                        gymClassRequest.instructor().equals(member.getEmail()))
                .findFirst();
        if (instructor.isEmpty()) {
            memberServiceLogger.error("Trainer with the specified e-mail does not exist.");
            throw new UsernameNotFoundException("Trainer with the specified e-mail does not exist.");
        }
        return instructor.get();
    }


    public MemberDTO convertToDTO(Member member) {
        return new MemberDTO(member.getId(), member.getFirstName(), member.getLastName(), member.getClassesTaught());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findMemberByEmail(username)
                .orElseThrow(() -> {
                    memberServiceLogger.error("Username not found: {}", username);
                    return new UsernameNotFoundException("Username not found");
                });
    }
}
