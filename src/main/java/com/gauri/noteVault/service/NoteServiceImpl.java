package com.gauri.noteVault.service;

import com.gauri.noteVault.dto.NoteRequestDTO;
import com.gauri.noteVault.dto.NoteResponseDTO;
import com.gauri.noteVault.entity.Note;
import com.gauri.noteVault.entity.User;
import com.gauri.noteVault.exception.ResourceNotFoundException;
import com.gauri.noteVault.repository.NoteRepository;
import com.gauri.noteVault.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteServiceImpl(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    // Create a new note for a given user
    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        logger.debug("Creating note for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User '{}' not found, cannot create note", username);
                    return new ResourceNotFoundException("User not found");
                });

        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setTags(dto.getTags() != null ? dto.getTags() : new ArrayList<>());
        note.setUser(user);

        Note saved = noteRepository.save(note);
        logger.info("Note created successfully for user: {}, Note ID: {}", username, saved.getId());

        return Mapper.toDto(saved);
    }

    // Retrieve a note by ID for a specific user
    @Override
    public NoteResponseDTO getById(Long id, String username) {
        logger.debug("Fetching note ID {} for user: {}", id, username);

        Note note = noteRepository.findById(id)
                .filter(n -> n.getUser().getUsername().equals(username))
                .orElseThrow(() -> {
                    logger.warn("Note ID {} not found for user: {}", id, username);
                    return new ResourceNotFoundException("Note not found with id " + id);
                });

        logger.info("Note ID {} retrieved successfully for user: {}", id, username);
        return Mapper.toDto(note);
    }

    // Update an existing note
    @Override
    public NoteResponseDTO update(Long id, NoteRequestDTO dto, String username) {
        logger.debug("Updating note ID {} for user: {}", id, username);

        Note note = noteRepository.findById(id)
                .filter(n -> n.getUser().getUsername().equals(username))
                .orElseThrow(() -> {
                    logger.warn("Note ID {} not found for user: {}", id, username);
                    return new ResourceNotFoundException("Note not found with id " + id);
                });

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setTags(dto.getTags() != null ? dto.getTags() : new ArrayList<>());

        Note updated = noteRepository.save(note);
        logger.info("Note ID {} updated successfully for user: {}", id, username);

        return Mapper.toDto(updated);
    }

    // Delete a note by ID for a specific user
    @Override
    public void delete(Long id, String username) {
        logger.debug("Deleting note ID {} for user: {}", id, username);

        Note note = noteRepository.findById(id)
                .filter(n -> n.getUser().getUsername().equals(username))
                .orElseThrow(() -> {
                    logger.warn("Note ID {} not found for user: {}", id, username);
                    return new ResourceNotFoundException("Note not found with id " + id);
                });

        noteRepository.delete(note);
        logger.info("Note ID {} deleted successfully for user: {}", id, username);
    }

    // List notes for a user with optional search and pagination
    @Override
    public Page<NoteResponseDTO> list(String username, String q, Pageable pageable) {
        logger.debug("Listing notes for user: {}, search query: '{}', page: {}, size: {}", username, q, pageable.getPageNumber(), pageable.getPageSize());

        Page<Note> page;
        if (q == null || q.isBlank()) {
            page = noteRepository.findByUserUsername(username, pageable);
        } else {
            page = noteRepository.searchByUserAndQuery(username, q, pageable);
        }

        logger.info("Notes listed successfully for user: {}, total notes: {}", username, page.getTotalElements());
        return page.map(Mapper::toDto);
    }
}
