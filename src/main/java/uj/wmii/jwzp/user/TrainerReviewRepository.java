package uj.wmii.jwzp.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerReviewRepository extends JpaRepository<TrainerReview, Long> {

}
