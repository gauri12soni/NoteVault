package com.gauri.noteVault.service;

import com.gauri.noteVault.dto.NoteResponseDTO;
import com.gauri.noteVault.entity.Note;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class Mapper {

    public static NoteResponseDTO toDto(Note note) {
        if (note == null) {
            return null;
        }

        return new NoteResponseDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getTags() != null ? note.getTags() : Collections.emptyList(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
