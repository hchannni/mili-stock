package com.milistock.develop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.milistock.develop.domain.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByTitleContaining(String title);
    List<Board> findByNameContaining(String name);
    Optional<Board> findByBoardId(Long boardId);
    Page<Board> findByTitleContaining(String title, Pageable pageable);
    Page<Board> findByNameContaining(String author, Pageable pageable);
    Page<Board> findAll(Pageable pageable);
}
