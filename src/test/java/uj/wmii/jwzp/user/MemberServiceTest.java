package uj.wmii.jwzp.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import uj.wmii.jwzp.auth.AuthenticationService;
import uj.wmii.jwzp.config.security.JwtAuthenticationFilter;
import uj.wmii.jwzp.config.security.JwtService;
import uj.wmii.jwzp.config.timezone.TimeZoneService;
import uj.wmii.jwzp.exception.ResourceNotFoundException;
import uj.wmii.jwzp.gymclass.GymClassEnrollmentService;
import uj.wmii.jwzp.gymclass.GymClassService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TimeZoneService timeZoneService;
    @Mock
    private MessageSource messageSource;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberServiceTest;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private TrainerReviewService trainerReviewService;

    @MockBean
    private GymClassService gymClassService;

    @MockBean
    private GymClassEnrollmentService gymClassEnrollmentService;

    @BeforeEach
    void setUp() {
        memberServiceTest = new MemberService(memberRepository, timeZoneService, messageSource, passwordEncoder);
    }

    @Test
    void getMembers() {
        memberServiceTest.getMembers();//when

        verify(memberRepository).findAll();//then
    }

    @Test
    void getMemberWithExistingMemberInDataBase() {
        Long memberId = 111L;
        Member member = new Member("Johnny", "Depp", "john@depp.com", LocalDate.now(),
                "", Role.ROLE_USER);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        Optional<Member> result = memberServiceTest.getMember(memberId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(member);
    }

    @Test
    void getMemberWithNonExistingMemberInDataBase() {
        Long memberId = 222L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        Optional<Member> result = memberServiceTest.getMember(memberId);

        assertThat(result).isEmpty();
    }

    @Test
    void addMember() {
        Member member = new Member("Harry", "Potter", "harry@potter.com", LocalDate.now(),
                "", Role.ROLE_USER); //given

        memberServiceTest.addMember(ZoneId.of("Europe/Warsaw"), member); //when

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class); //then
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member memberCaptured = memberArgumentCaptor.getValue();
        assertThat(memberCaptured).isEqualTo(member);
    }

    @Test
    void removeMemberWithExistingMember() {
        Member member = new Member("Harry", "Potter", "harry@potter.com", LocalDate.now(),
                "", Role.ROLE_USER);

        memberServiceTest.addMember(ZoneId.of("Europe/Warsaw"), member);
        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class); //then
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member memberCaptured = memberArgumentCaptor.getValue();

        Optional<Member> removedMember = memberRepository.findMemberByEmail(memberCaptured.getEmail());
        assertThat(removedMember).isEmpty();
    }

    @Test
    void removeMemberWithNonExistingMember(){
        Long memberId = 1L;

        assertThrows(ResourceNotFoundException.class, () -> memberServiceTest.removeMember(memberId));
    }

}