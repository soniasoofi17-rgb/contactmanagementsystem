package com.example.contact_manager.controller;

import com.example.contact_manager.dto.ContactDto;
import com.example.contact_manager.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Helper: safely extract the username/email from Authentication principal
    private String principalToEmail(Authentication auth) {
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        // fallback to auth.getName() which is usually the username
        return auth.getName();
    }

    @PostMapping
    public ResponseEntity<?> createContact(Authentication auth, @RequestBody ContactDto dto) {
        String userEmail = principalToEmail(auth);
        if (userEmail == null) return ResponseEntity.status(401).body("Unauthorized");
        ContactDto created = contactService.createContact(userEmail, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(Authentication auth, @PathVariable("id") Long id, @RequestBody ContactDto dto) {
        String userEmail = principalToEmail(auth);
        if (userEmail == null) return ResponseEntity.status(401).body("Unauthorized");
        ContactDto updated = contactService.updateContact(userEmail, id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(Authentication auth, @PathVariable("id") Long id) {
        String userEmail = principalToEmail(auth);
        if (userEmail == null) return ResponseEntity.status(401).body("Unauthorized");
        contactService.deleteContact(userEmail, id);
        return ResponseEntity.ok().body("Deleted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContact(Authentication auth, @PathVariable("id") Long id) {
        String userEmail = principalToEmail(auth);
        if (userEmail == null) return ResponseEntity.status(401).body("Unauthorized");
        ContactDto dto = contactService.getContact(userEmail, id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<?> listContacts(Authentication auth,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String q) {
        String userEmail = principalToEmail(auth);
        if (userEmail == null) return ResponseEntity.status(401).body("Unauthorized");
        Page<ContactDto> results = contactService.listContacts(userEmail, page, size, q);
        return ResponseEntity.ok(results);
    }
}
