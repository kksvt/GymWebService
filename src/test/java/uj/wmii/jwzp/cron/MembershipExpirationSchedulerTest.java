package uj.wmii.jwzp.cron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uj.wmii.jwzp.user.Member;
import uj.wmii.jwzp.user.MemberService;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class MembershipExpirationSchedulerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MembershipExpirationScheduler scheduler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckMembershipExpiration_ExpiredMembership_SetIsActiveToFalse() {
        Member expiredMember = new Member();
        expiredMember.setMembershipDateEnd(LocalDate.now().minusDays(1));
        expiredMember.setActive(true);

        when(memberService.getMembers()).thenReturn(Collections.singletonList(expiredMember));

        scheduler.checkMembershipExpiration();

        assertFalse(expiredMember.isActive());
        verify(memberService).updateMember(expiredMember);
    }
}
