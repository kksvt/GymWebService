package uj.wmii.jwzp.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class TrainerReview {
    @Id
    @Column(name="review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="instructor_id")
    private Member instructor;

    @Column(name="instructor_rating")
    private Short rating;

    @Column(name="instructor_rating_desc")
    private String ratingDesc;

    public TrainerReview() {
    }

    public TrainerReview(Member member, Member instructor, Short rating, String ratingDesc) {
        this.member = member;
        this.instructor = instructor;
        this.rating = rating;
        this.ratingDesc = ratingDesc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Member getInstructor() {
        return instructor;
    }

    public void setTrainer(Member instructor) {
        this.instructor = instructor;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainerReview that = (TrainerReview) o;
        return Objects.equals(id, that.id) && Objects.equals(member, that.member) &&
                Objects.equals(instructor, that.instructor) &&
                Objects.equals(rating, that.rating) &&
                Objects.equals(ratingDesc, that.ratingDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member, instructor, rating, ratingDesc);
    }

    @Override
    public String toString() {
        return "TrainerReview{" +
                "id=" + id +
                ", member=" + member +
                ", instructor=" + instructor +
                ", rating=" + rating +
                ", ratingDesc='" + ratingDesc + '\'' +
                '}';
    }
}
