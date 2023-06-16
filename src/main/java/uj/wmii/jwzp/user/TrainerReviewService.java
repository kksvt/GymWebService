package uj.wmii.jwzp.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrainerReviewService {

    private static final Logger trainerReviewServiceLogger = LoggerFactory.getLogger(TrainerReviewService.class);

    private final TrainerReviewRepository trainerReviewRepository;

    private final MemberRepository memberRepository;

    @Autowired
    public TrainerReviewService(TrainerReviewRepository trainerReviewRepository,
                                MemberRepository memberRepository) {
        this.trainerReviewRepository = trainerReviewRepository;
        this.memberRepository = memberRepository;
    }

    public List<TrainerReview> getReviews(Long trainerId) {
        if (trainerId == null) {
            return trainerReviewRepository.findAll();
        }
        return trainerReviewRepository
                .findAll()
                .stream()
                .filter(tr -> Objects.equals(tr.getInstructor().getId(), trainerId))
                .toList();
    }

    public TrainerReview addReview(Member member, Long trainerId, TrainerReviewRequest trainingReviewRequest) {
        Optional<Member> trainer = memberRepository.findById(trainerId);
        if (trainer.isEmpty()) {
            trainerReviewServiceLogger.error("Member with id: {} does not exist.", trainerId);
            throw new ResourceNotFoundException("Member with id: " + trainerId + " does not exist.");
        }
        if (trainer.get().getRole() != Role.ROLE_TRAINER) {
            trainerReviewServiceLogger.error("Member with id: {} is not a trainer.", trainerId);
            throw new InvalidRequestException("Member with id: "  + trainerId +  "is not a trainer.");
        }
        if (member.getSubmittedReviews().stream().anyMatch(tr -> tr.getInstructor().equals(trainer.get()))) {
            trainerReviewServiceLogger.error("You've already reviewed this trainer.");
            throw new InvalidRequestException("You've already reviewed this trainer.");
        }
        if (trainingReviewRequest.rating() < 0 || trainingReviewRequest.rating() > 5) {
            trainerReviewServiceLogger.error("The rating has to be an integer between 0 and 5.");
            throw new InvalidRequestException("The rating has to be an integer between 0 and 5.");
        }
        TrainerReview trainerReview = new TrainerReview(member, trainer.get(),
                trainingReviewRequest.rating(), trainingReviewRequest.ratingDesc());
        member.getSubmittedReviews().add(trainerReview);
        trainer.get().getReceivedReviews().add(trainerReview);
        memberRepository.save(member);
        memberRepository.save(trainer.get());
        trainerReviewServiceLogger.info("The rating was successfully added from member with id: {} to trainer with id: {}", member.getId(), trainerId);
        return trainerReviewRepository.save(trainerReview);
    }
}
