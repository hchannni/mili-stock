package com.milistock.develop.controller.Board;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.domain.Board;
import com.milistock.develop.dto.BoardPostDto;
import com.milistock.develop.dto.BoardPostResponseDto;
import com.milistock.develop.security.jwt.util.IfLogin;
import com.milistock.develop.security.jwt.util.LoginUserDto;
import com.milistock.develop.service.BoardService;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/post")
    public ResponseEntity<?> postBoard(@IfLogin LoginUserDto loginUserDto, @RequestBody @Valid BoardPostDto boardPostDto) {
        
        String title = boardPostDto.getTitle();
        String content = boardPostDto.getContent();
        String name = loginUserDto.getName();
        Long memberId = loginUserDto.getMemberId();
        
        // Board 객체 생성 및 필드 설정
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setName(name);
        board.setMemberId(memberId);

        // BoardService를 통해 게시물 저장
        Board savedBoard = boardService.createBoard(board);

        BoardPostResponseDto responseDto = new BoardPostResponseDto();
        responseDto.setBoardId(savedBoard.getBoardId());
        responseDto.setTitle(savedBoard.getTitle());
        responseDto.setContent(savedBoard.getContent());
        responseDto.setName(savedBoard.getName());
        responseDto.setMemberId(savedBoard.getMemberId());
        responseDto.setDate(savedBoard.getDate());
        responseDto.setAnswered(savedBoard.isAnswered());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}

