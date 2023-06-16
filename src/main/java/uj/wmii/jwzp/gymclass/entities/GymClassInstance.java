package uj.wmii.jwzp.gymclass.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Entity
@Table(name = "gym_class_instances")
public class GymClassInstance {
    @Id
    @Column(name="gym_class_instance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gym_class_id")
    @JsonIgnore
    private GymClass gymClass;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public GymClassInstance() {

    }

    public GymClassInstance(GymClassDate gymClassDate) {
        this.gymClass = gymClassDate.getGymClass();
        LocalDate date = LocalDateTime.now().with(TemporalAdjusters.next(gymClassDate.getDayOfWeek())).toLocalDate();
        this.startTime = date.atTime(gymClassDate.getStartTime());
        this.endTime = date.atTime(gymClassDate.getEndTime());
    }

    public GymClassInstance(GymClass gymClass, LocalDateTime startTime, LocalDateTime endTime) {
        this.gymClass = gymClass;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GymClass getGymClass() {
        return gymClass;
    }

    public void setGymClass(GymClass gymClass) {
        this.gymClass = gymClass;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
