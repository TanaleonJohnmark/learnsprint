package com.csit321g5.learnsprint.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * Supertype of every account in LearnSprint.
 *
 * The specialization is DISJOINT and TOTAL: an account is exactly one of
 * Student, Faculty or Admin. The subtype discriminator is the "role" column.
 *
 * JOINED inheritance makes the database match the ERD: one app_user table for
 * the shared attributes, plus one table per subtype that shares the same key.
 * The table is named app_user because "user" is a reserved word in MySQL.
 */
@Entity
@Table(name = "app_user")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 60)
    private String fname;

    @Column(nullable = false, length = 60)
    private String lname;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 120)
    private String password;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /** Read-only copy of the discriminator so the role appears in the JSON. */
    @Transient
    public String getRole() {
        return getClass().getSimpleName().toUpperCase();
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
