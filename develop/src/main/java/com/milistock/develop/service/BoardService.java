package com.milistock.develop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.milistock.develop.domain.Board;
import com.milistock.develop.repository.BoardRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // 게시물 작성 메소드
    public Board createBoard(Board board) {
        
        Board saveBoard = boardRepository.save(board);
        return saveBoard;
    }

    // 게시물 전체 조회 메소드
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 제목으로 게시물 검색 메소드
    public List<Board> searchBoardsByTitle(String title) {
        return boardRepository.findByTitleContaining(title);
    }

    // 작성자로 게시물 검색 메소드
    public List<Board> searchBoardsByName(String name) {
        return boardRepository.findByNameContaining(name);
    }
}
