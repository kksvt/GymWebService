package uj.wmii.jwzp.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uj.wmii.jwzp.auth.AuthenticationService;
import uj.wmii.jwzp.config.security.JwtAuthenticationFilter;
import uj.wmii.jwzp.config.security.JwtService;
import uj.wmii.jwzp.gymclass.GymClassEnrollmentService;
import uj.wmii.jwzp.gymclass.GymClassService;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepositoryTest;

    @MockBean
    private MemberService memberService;

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

    @AfterEach
    void tearDown(){
        memberRepositoryTest.deleteAll();
    }

    @Test
    void findMemberByEmailExists() {
        Member member = new Member("Harry", "Potter", "harry@potter.com", LocalDate.now(),
                "boywholived", Role.ROLE_USER);
        memberRepositoryTest.save(member);

        Optional<Member> optionalMember = memberRepositoryTest.findMemberByEmail("harry@potter.com");

        Assertions.assertTrue(optionalMember.isPresent());
    }

    @Test
    void findMemberByEmailNotExists() {
        Optional<Member> optionalMember = memberRepositoryTest.findMemberByEmail("harry@potter.com");
        Assertions.assertFalse(optionalMember.isPresent());
    }
}