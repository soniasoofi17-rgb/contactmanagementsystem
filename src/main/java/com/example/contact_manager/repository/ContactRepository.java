package com.example.contact_manager.repository;

import com.example.contact_manager.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findByUserEmail(String email, Pageable pageable);

    Page<Contact> findByUserEmailAndFirstNameContainingIgnoreCaseOrUserEmailAndLastNameContainingIgnoreCase(
            String email1, String firstName, String email2, String lastName, Pageable pageable);
}
