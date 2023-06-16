package uj.wmii.jwzp.gymclass.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "gym_class_dates")
public class GymClassDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_class_id")
    @JsonIgnore
    private GymClass gymClass;

    public GymClassDate() { }

    public GymClassDate(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setGymClass(GymClass gymClass) {
        this.gymClass = gymClass;
    }

    public GymClass getGymClass() {
        return gymClass;
    }

    @Override
    public String toString() {
        return "GymClassDate{" +
                "id=" + id +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                //", zoneId=" + zoneId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GymClassDate that = (GymClassDate) o;
        return dayOfWeek == that.dayOfWeek && startTime.equals(that.startTime) && endTime.equals(that.endTime)/* && zoneId.equals(that.zoneId)*/;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime/* ,zoneId*/);
    }
}