package com.milistock.develop.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 게시물 수정 메소드
    public Board editBoard(Long boardId, String title, String content) {

        Board existingBoard = boardRepository.findByBoardId(boardId).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));;
        
        existingBoard.setContent(content);
        existingBoard.setTitle(title);
        
        Board saveBoard = boardRepository.save(existingBoard);
        return saveBoard;
    }

    //게시물 삭제 메소드
    public void deleteBoard(Long boardId) {

        boardRepository.findByBoardId(boardId).ifPresent(boardRepository::delete);
        
    }

    // 게시물 보기 메소드
    public Board viewBoard(Long boardId) {
        Optional<Board> boardFromBoardId = boardRepository.findByBoardId(boardId);
        if (boardFromBoardId.isPresent()) {
            return boardFromBoardId.get();
        } else {
            return null;
        }

    }

    //게시물 전체 조회 메소드
    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    // 제목으로 게시물 검색 메소드
    public Page<Board> searchBoardsByTitle(String title, Pageable pageable) {
        return boardRepository.findByTitleContaining(title, pageable);
    }
    // 작성자로 게시물 검색 메소드
    public Page<Board> searchBoardsByName(String name, Pageable pageable) {
        return boardRepository.findByNameContaining(name, pageable);
    }

    //답변 남기기 메소드
    public Board answerBoard(Long boardId, String answer) {

        Board existingBoard = boardRepository.findByBoardId(boardId).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));;
        
        existingBoard.setAnswer(answer);
        existingBoard.setAnswered(true);
        
        Board saveBoard = boardRepository.save(existingBoard);
        return saveBoard;
    }
}
