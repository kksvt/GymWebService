package uj.wmii.jwzp.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uj.wmii.jwzp.gymclass.entities.GymClass;
import uj.wmii.jwzp.gymclass.entities.GymClassEnrollment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="gym_members")
public class Member implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate membershipDateStart;
    private LocalDate membershipDateEnd;
    private boolean isActive = false;
    private String email;
    private String password;
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private final Set<GymClassEnrollment> enrolledClasses = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "instructor")
    private Set<GymClass> classesTaught;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final Set<TrainerReview> submittedReviews = new HashSet<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    private final Set<TrainerReview> receivedReviews = new HashSet<>();
    public LocalDate getMembershipDateEnd() {
        return membershipDateEnd;
    }

    public void setMembershipDateEnd(LocalDate membershipDateEnd) {
        this.membershipDateEnd = membershipDateEnd;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Member(String firstName, String lastName, String email, LocalDate membershipDateStart,
                  String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.membershipDateStart = membershipDateStart;
        this.membershipDateEnd = membershipDateStart.plusMonths(1);
        this.email = email;
        this.password = password;
        this.role = role;
    }
    //todo: remove this if its okay
    /*public Member(Long id,String firstName, String lastName, String email, LocalDate membershipDateStart) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.membershipDateStart = membershipDateStart;
        this.membershipDateEnd = membershipDateStart.plusMonths(1);
        this.email = email;
    }*/

    public Member() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getMembershipDateStart() {
        return membershipDateStart;
    }

    public void setMembershipDateStart(LocalDate membershipDateStart) {
        this.membershipDateStart = membershipDateStart;
    }

    public void fixMembershipDateStart(ZoneId from, ZoneId to) {
        membershipDateStart = ZonedDateTime.
                of(membershipDateStart.atTime(0, 0, 0), from ).
                withZoneSameInstant(to != null ? to : ZoneId.systemDefault()).
                toLocalDate();
    }

    public void extendMembership(long months) {
        membershipDateEnd = membershipDateStart.plusMonths(months);
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", membershipDateStart=" + membershipDateStart +
                '}';
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Set<GymClassEnrollment> getEnrolledClasses() {
        return enrolledClasses;
    }

    public Set<GymClass> getClassesTaught() { return classesTaught; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    public Set<TrainerReview> getSubmittedReviews() {
        return submittedReviews;
    }

    public Set<TrainerReview> getReceivedReviews() {
        return receivedReviews;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
