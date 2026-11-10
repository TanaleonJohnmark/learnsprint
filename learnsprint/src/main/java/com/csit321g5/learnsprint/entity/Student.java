package com.csit321g5.learnsprint.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student")
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column(length = 30)
    private String schoolId;

    @Column(length = 80)
    private String program;

    private Integer yearLevel;

    private Integer totalPoints;

    private Integer currentStreak;

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Integer getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(Integer yearLevel) {
        this.yearLevel = yearLevel;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }
}
