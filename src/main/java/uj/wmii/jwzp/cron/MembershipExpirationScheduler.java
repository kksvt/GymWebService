package uj.wmii.jwzp.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uj.wmii.jwzp.user.MemberService;

import java.time.LocalDate;

@Component
public class MembershipExpirationScheduler {

    private static final Logger membershipExpirationSchedulerLogger = LoggerFactory.getLogger(MembershipExpirationScheduler.class);

    private final MemberService memberService;

    @Autowired
    public MembershipExpirationScheduler(MemberService memberService){
        this.memberService = memberService;
    }

    //this will run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkMembershipExpiration(){
        LocalDate currentDate = LocalDate.now();
        membershipExpirationSchedulerLogger.info("Started checking all members membership expiration in day: {}", currentDate);
        memberService.getMembers().forEach(
                member -> {
                    if(currentDate.isAfter(member.getMembershipDateEnd())){
                        member.setActive(false);
                        memberService.updateMember(member);
                    }
                }
        );
        membershipExpirationSchedulerLogger.info("Membership expiration checked.");
    }
}
