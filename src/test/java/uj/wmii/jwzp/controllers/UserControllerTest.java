package uj.wmii.jwzp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uj.wmii.jwzp.config.security.JwtService;
import uj.wmii.jwzp.gymclass.GymClassEnrollmentService;
import uj.wmii.jwzp.gymclass.GymClassService;
import uj.wmii.jwzp.gymclass.entities.*;
import uj.wmii.jwzp.user.*;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymClassService gymClassService;

    @MockBean
    private GymClassEnrollmentService gymClassEnrollmentService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TrainerReviewService trainerReviewService;


    @Test
    public void getNonExistingClass() throws Exception {
        long classId = 123L;
        when(gymClassService.getClass(classId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/classes/{classId}", classId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getEnrolledMembersFromNonExistingClass() throws Exception {
        long classId = 123L;
        when(gymClassService.getClass(classId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/classes/{classId}/members", classId))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getSingleClassWithNonExistingClass() throws Exception {
        long classId = 1L;
        when(gymClassService.getClass(classId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/classes/{classId}", classId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTrainers() throws Exception {
        MemberDTO trainerDTO = new MemberDTO(1L, "John", "Doe", Set.of(new GymClass()));
        when(memberService.getMembers())
                .thenReturn(Collections.singletonList(new Member("John", "Doe", "jhon@doe.com", LocalDate.now(), "password", Role.ROLE_TRAINER)));
        when(memberService.convertToDTO(any(Member.class))).thenReturn(trainerDTO);

        mockMvc.perform(get("/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(trainerDTO.id()))
                .andExpect(jsonPath("$.[0].firstName").value(trainerDTO.firstName()))
                .andExpect(jsonPath("$.[0].lastName").value(trainerDTO.lastName()))
                .andExpect(jsonPath("$.[0].classesTaught").isArray());
    }
}