package com.example.contact_manager.dto;

import java.util.List;

public class ContactDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private List<EmailDto> emails;
    private List<PhoneDto> phones;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<EmailDto> getEmails() { return emails; }
    public void setEmails(List<EmailDto> emails) { this.emails = emails; }
    public List<PhoneDto> getPhones() { return phones; }
    public void setPhones(List<PhoneDto> phones) { this.phones = phones; }
}
