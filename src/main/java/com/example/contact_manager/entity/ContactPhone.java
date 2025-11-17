package com.example.contact_manager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contact_phones")
public class ContactPhone {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }
}
