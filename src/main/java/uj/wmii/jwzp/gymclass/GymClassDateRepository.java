package uj.wmii.jwzp.gymclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uj.wmii.jwzp.gymclass.entities.GymClassDate;

@Repository
public interface GymClassDateRepository extends JpaRepository<GymClassDate,Long> {
}
