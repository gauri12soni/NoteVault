package com.gauri.noteVault.service;

import com.gauri.noteVault.dto.NoteRequestDTO;
import com.gauri.noteVault.dto.NoteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteService {

    NoteResponseDTO createNote(NoteRequestDTO dto, String username);

    NoteResponseDTO getById(Long id, String username);

    NoteResponseDTO update(Long id, NoteRequestDTO dto, String username);

    void delete(Long id, String username);

    Page<NoteResponseDTO> list(String username, String q, Pageable pageable);
}
