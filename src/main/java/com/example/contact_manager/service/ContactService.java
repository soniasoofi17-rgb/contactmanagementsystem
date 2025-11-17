package com.example.contact_manager.service;

import com.example.contact_manager.dto.ContactDto;
import com.example.contact_manager.dto.EmailDto;
import com.example.contact_manager.dto.PhoneDto;
import com.example.contact_manager.entity.Contact;
import com.example.contact_manager.entity.ContactEmail;
import com.example.contact_manager.entity.ContactPhone;
import com.example.contact_manager.entity.User;
import com.example.contact_manager.repository.ContactRepository;
import com.example.contact_manager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    private ContactDto toDto(Contact c) {
        ContactDto dto = new ContactDto();
        dto.setId(c.getId());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setTitle(c.getTitle());

        List<EmailDto> emails = new ArrayList<>();
        if (c.getEmails() != null) {
            for (ContactEmail e : c.getEmails()) {
                EmailDto ed = new EmailDto();
                ed.setId(e.getId());
                ed.setEmail(e.getEmail());
                ed.setLabel(e.getLabel());
                emails.add(ed);
            }
        }
        dto.setEmails(emails);

        List<PhoneDto> phones = new ArrayList<>();
        if (c.getPhones() != null) {
            for (ContactPhone p : c.getPhones()) {
                PhoneDto pd = new PhoneDto();
                pd.setId(p.getId());
                pd.setPhone(p.getPhone());
                pd.setLabel(p.getLabel());
                phones.add(pd);
            }
        }
        dto.setPhones(phones);

        return dto;
    }

    private Contact fromDto(ContactDto dto) {
        Contact c = new Contact();
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setTitle(dto.getTitle());

        if (dto.getEmails() != null) {
            for (EmailDto ed : dto.getEmails()) {
                ContactEmail ce = new ContactEmail();
                ce.setEmail(ed.getEmail());
                ce.setLabel(ed.getLabel());
                ce.setContact(c);
                c.getEmails().add(ce);
            }
        }

        if (dto.getPhones() != null) {
            for (PhoneDto pd : dto.getPhones()) {
                ContactPhone cp = new ContactPhone();
                cp.setPhone(pd.getPhone());
                cp.setLabel(pd.getLabel());
                cp.setContact(c);
                c.getPhones().add(cp);
            }
        }

        return c;
    }

    @Transactional
    public ContactDto createContact(String userEmail, ContactDto dto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Contact c = fromDto(dto);
        c.setUser(user);
        Contact saved = contactRepository.save(c);
        return toDto(saved);
    }

    @Transactional
    public ContactDto updateContact(String userEmail, Long contactId, ContactDto dto) {
        Contact existing = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
        if (existing.getUser() == null || !existing.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Not allowed");
        }

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setTitle(dto.getTitle());

        // Clear and rebuild emails
        existing.getEmails().clear();
        if (dto.getEmails() != null) {
            for (EmailDto ed : dto.getEmails()) {
                ContactEmail ce = new ContactEmail();
                ce.setEmail(ed.getEmail());
                ce.setLabel(ed.getLabel());
                ce.setContact(existing);
                existing.getEmails().add(ce);
            }
        }

        // Clear and rebuild phones
        existing.getPhones().clear();
        if (dto.getPhones() != null) {
            for (PhoneDto pd : dto.getPhones()) {
                ContactPhone cp = new ContactPhone();
                cp.setPhone(pd.getPhone());
                cp.setLabel(pd.getLabel());
                cp.setContact(existing);
                existing.getPhones().add(cp);
            }
        }

        Contact saved = contactRepository.save(existing);
        return toDto(saved);
    }

    public void deleteContact(String userEmail, Long contactId) {
        Contact existing = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
        if (existing.getUser() == null || !existing.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Not allowed");
        }
        contactRepository.delete(existing);
    }

    public ContactDto getContact(String userEmail, Long contactId) {
        Contact c = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
        if (c.getUser() == null || !c.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Not allowed");
        }
        return toDto(c);
    }

    public Page<ContactDto> listContacts(String userEmail, int page, int size, String query) {
        Pageable p = PageRequest.of(page, size);
        Page<Contact> pageResult;
        if (query == null || query.isBlank()) {
            pageResult = contactRepository.findByUserEmail(userEmail, p);
        } else {
            pageResult = contactRepository.findByUserEmailAndFirstNameContainingIgnoreCaseOrUserEmailAndLastNameContainingIgnoreCase(
                    userEmail, query, userEmail, query, p);
        }
        return pageResult.map(this::toDto);
    }
}
