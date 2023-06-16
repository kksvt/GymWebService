package uj.wmii.jwzp.gymclass;

import org.springframework.data.jpa.repository.JpaRepository;
import uj.wmii.jwzp.gymclass.entities.GymClassEnrollment;

public interface GymClassEnrollmentRepository extends JpaRepository<GymClassEnrollment, Long> {
}
