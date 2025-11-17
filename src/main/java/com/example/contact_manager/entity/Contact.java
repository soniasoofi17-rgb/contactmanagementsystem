package com.example.contact_manager.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactEmail> emails = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactPhone> phones = new ArrayList<>();

    // -- getters / setters --

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // IMPORTANT: these must exist exactly as shown
    public List<ContactEmail> getEmails() { return emails; }
    public void setEmails(List<ContactEmail> emails) {
        this.emails = (emails == null) ? new ArrayList<>() : emails;
    }

    public List<ContactPhone> getPhones() { return phones; }
    public void setPhones(List<ContactPhone> phones) {
        this.phones = (phones == null) ? new ArrayList<>() : phones;
    }
}
