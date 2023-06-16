package uj.wmii.jwzp.gymclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uj.wmii.jwzp.gymclass.entities.GymClassInstance;

@Repository
public interface GymClassInstanceRepository extends JpaRepository<GymClassInstance, Long> {
}
