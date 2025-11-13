package com.gauri.noteVault.controller;

import com.gauri.noteVault.dto.NoteRequestDTO;
import com.gauri.noteVault.dto.NoteResponseDTO;
import com.gauri.noteVault.service.NoteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Create a new note
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@Valid @RequestBody NoteRequestDTO dto) {
        String username = getCurrentUsername();
        logger.info("Creating note for user: {}", username);
        NoteResponseDTO created = noteService.createNote(dto, username);
        logger.info("Note created successfully for user: {}", username);
        return ResponseEntity.ok(created);
    }

    // Get a note by its ID
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNote(@PathVariable Long id) {
        String username = getCurrentUsername();
        logger.info("Fetching note with ID: {} for user: {}", id, username);
        NoteResponseDTO note = noteService.getById(id, username);
        return ResponseEntity.ok(note);
    }

    // Update an existing note
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long id, @Valid @RequestBody NoteRequestDTO dto) {
        String username = getCurrentUsername();
        logger.info("Updating note with ID: {} for user: {}", id, username);
        NoteResponseDTO updated = noteService.update(id, dto, username);
        logger.info("Note updated successfully for user: {}", username);
        return ResponseEntity.ok(updated);
    }

    // Delete a note by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        String username = getCurrentUsername();
        logger.info("Deleting note with ID: {} for user: {}", id, username);
        noteService.delete(id, username);
        logger.info("Note deleted successfully for user: {}", username);
        return ResponseEntity.noContent().build();
    }

    // List all notes (supports optional search and pagination)
    @GetMapping
    public ResponseEntity<Page<NoteResponseDTO>> listNotes(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        String username = getCurrentUsername();
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Listing notes for user: {} (page: {}, size: {}, query: {})", username, page, size, query);
        Page<NoteResponseDTO> notes = noteService.list(username, query, pageable);
        return ResponseEntity.ok(notes);
    }

    // Get the current authenticated user's username from JWT
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Access denied - user not authenticated");
            throw new RuntimeException("User not authenticated");
        }
        return authentication.getName();
    }
}
