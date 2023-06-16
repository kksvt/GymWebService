package uj.wmii.jwzp.gymclass.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import uj.wmii.jwzp.common.Gender;
import uj.wmii.jwzp.user.Member;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "gym_classes")
public class GymClass {
    @Id
    @Column(name="gym_class_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonIgnore
    private Member instructor;

    @Enumerated(EnumType.STRING)
    private GymClassType type;

    @Enumerated(EnumType.STRING)
    private GymClassLevel level;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public GymClass() {
    }

    @OneToMany(mappedBy = "gymClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<GymClassDate> dates = new HashSet<>();

    @OneToMany(mappedBy = "gymClass", cascade = CascadeType.ALL)
    private final Set<GymClassEnrollment> enrolledMembers = new HashSet<>();

    @OneToMany(mappedBy = "gymClass", cascade = CascadeType.ALL)
    private final Set<GymClassInstance> gymClassInstances = new HashSet<>();

    public GymClass(String name, String description, Member instructor, GymClassType type, GymClassLevel level, Gender gender) {
        this.name = name;
        this.description = description;
        this.instructor = instructor;
        this.type = type;
        this.level = level;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Member getInstructor() {
        return instructor;
    }

    public void setInstructor(Member instructor) {
        this.instructor = instructor;
    }

    public GymClassType getType() {
        return type;
    }

    public void setType(GymClassType type) {
        this.type = type;
    }

    public GymClassLevel getLevel() {
        return level;
    }

    public void setLevel(GymClassLevel level) {
        this.level = level;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Set<GymClassDate> getDates() {
        return dates;
    }

    public void addDate(GymClassDate date) {
        this.dates.add(date);
    }

    public boolean dateExists(GymClassDate date) {
        return dates.contains(date);
    }

    public boolean removeDate(GymClassDate date) {
        return dates.remove(date);
    }

    public boolean canEnroll(Member member) {
        return member.isActive();
    }

    public Set<GymClassEnrollment> getEnrolledMembers() {
        return enrolledMembers;
    }

    public Set<GymClassInstance> getGymClassInstances() {
        return gymClassInstances;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "GymClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", instructor='" + instructor + '\'' +
                ", type=" + type +
                ", level=" + level +
                ", gender=" + gender +
                '}';
    }
}