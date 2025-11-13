package com.gauri.noteVault.repository;

import com.gauri.noteVault.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // List notes belonging to a specific user
    @Query("SELECT n FROM Note n WHERE n.user.username = :username")
    Page<Note> findByUserUsername(@Param("username") String username, Pageable pageable);

    // Search notes by title or content (scoped to that user)
    @Query("""
            SELECT n FROM Note n
            WHERE n.user.username = :username AND
            (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Note> searchByUserAndQuery(@Param("username") String username,
                                    @Param("query") String query,
                                    Pageable pageable);

    // Find a specific note by ID (ensuring user ownership)
    @Query("SELECT n FROM Note n WHERE n.id = :id AND n.user.username = :username")
    Optional<Note> findByIdAndUserUsername(@Param("id") Long id,
                                           @Param("username") String username);

    // Optional: fetch all notes as a list (not paged)
    @Query("SELECT n FROM Note n WHERE n.user.username = :username")
    List<Note> findAllByUserUsername(@Param("username") String username);
}
