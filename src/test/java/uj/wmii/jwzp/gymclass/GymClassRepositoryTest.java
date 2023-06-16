package uj.wmii.jwzp.gymclass;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uj.wmii.jwzp.auth.AuthenticationService;
import uj.wmii.jwzp.common.Gender;
import uj.wmii.jwzp.config.security.JwtAuthenticationFilter;
import uj.wmii.jwzp.config.security.JwtService;
import uj.wmii.jwzp.gymclass.entities.GymClass;
import uj.wmii.jwzp.gymclass.entities.GymClassLevel;
import uj.wmii.jwzp.gymclass.entities.GymClassType;
import uj.wmii.jwzp.user.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GymClassRepositoryTest {

    @Autowired
    private GymClassRepository gymClassRepositoryTest;

    @Autowired
    private MemberRepository memberRepository;

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

    private Member createTrainerMember() {
        Member trainer = new Member("Tomek", "Tomaszewski", "tomek@tomaszewski.com", LocalDate.now(), "", Role.ROLE_TRAINER);
        trainer.extendMembership(1);
        return trainer;
    }

    @AfterEach
    void tearDown(){
        gymClassRepositoryTest.deleteAll();
    }

    @Test
    void findByNameExists() {
        Member trainer = createTrainerMember();
        memberRepository.save(trainer);
        GymClass gymClass = new GymClass("Yoga", "Stretching exercises", trainer, GymClassType.YOGA, GymClassLevel.ADVANCED, Gender.FEMALE);
        gymClassRepositoryTest.save(gymClass);

        Optional<GymClass> optionalGymClass = gymClassRepositoryTest.findByName("Yoga");

        Assertions.assertTrue(optionalGymClass.isPresent());
    }

    @Test
    void findByNameNotExists() {
        Optional<GymClass> optionalGymClass = gymClassRepositoryTest.findByName("Yoga");

        Assertions.assertFalse(optionalGymClass.isPresent());
    }
}