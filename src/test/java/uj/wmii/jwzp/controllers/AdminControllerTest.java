package uj.wmii.jwzp.controllers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uj.wmii.jwzp.config.security.JwtService;
import uj.wmii.jwzp.gymclass.GymClassEnrollmentService;
import uj.wmii.jwzp.gymclass.GymClassService;
import uj.wmii.jwzp.user.MemberService;
import uj.wmii.jwzp.user.TrainerReviewService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private GymClassService gymClassService;

    @MockBean
    private GymClassEnrollmentService gymClassEnrollmentService;

    @MockBean
    private TrainerReviewService trainerReviewService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(memberService, gymClassService, gymClassEnrollmentService,trainerReviewService)).build();
    }

    @Test
    public void getMemberWithNonExistingMemberId() throws Exception {
        Long memberId = 1L;
        when(memberService.getMember(memberId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/member/{memberId}", memberId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getEnrolledMembersWithNonExistingClassId() throws Exception {
        long classId = 1L;
        when(gymClassService.getClass(classId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/classes/{classId}/members", classId))
                .andExpect(status().isNotFound());
    }
}
