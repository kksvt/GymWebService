package uj.wmii.jwzp.gymclass.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import uj.wmii.jwzp.user.Member;

@Entity
public class GymClassEnrollment {
    @Id
    @Column(name="gym_enrollment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="gym_class_id")
    private GymClass gymClass;

    @Column(name="gym_rating")
    private Short rating;

    @Column(name="gym_rating_desc")
    private String ratingDesc;

    public GymClassEnrollment() {}

    public GymClassEnrollment(Member member, GymClass gymClass, Short rating, String ratingDesc) {
        this.member = member;
        this.gymClass = gymClass;
        this.rating = rating;
        this.ratingDesc = ratingDesc;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public GymClass getGymClass() {
        return gymClass;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getRatingDesc() {
        return ratingDesc;
    }

    public void setRatingDesc(String ratingDesc) {
        this.ratingDesc = ratingDesc;
    }

    public void setGymClass(GymClass gymClass) {
        this.gymClass = gymClass;
    }

}
