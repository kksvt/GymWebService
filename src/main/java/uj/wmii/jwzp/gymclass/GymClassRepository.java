package uj.wmii.jwzp.gymclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uj.wmii.jwzp.gymclass.entities.GymClass;

import java.util.Optional;

@Repository
public interface GymClassRepository extends JpaRepository<GymClass,Long> {
    @Query("SELECT g FROM GymClass g WHERE name = :name")
    Optional<GymClass> findByName(@Param("name") String name);
}
