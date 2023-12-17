package com.milistock.develop.controller.Board;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.milistock.develop.code.ErrorCode;
import com.milistock.develop.domain.Board;
import com.milistock.develop.dto.BoardAnswerDto;
import com.milistock.develop.dto.BoardDeleteDto;
import com.milistock.develop.dto.BoardEditDto;
import com.milistock.develop.dto.BoardPostDto;
import com.milistock.develop.dto.BoardPostResponseDto;
import com.milistock.develop.dto.BoardViewDto;
import com.milistock.develop.dto.HttpOkResponseDto;
import com.milistock.develop.exception.BusinessExceptionHandler;
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
    public ResponseEntity<?> postBoard(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid BoardPostDto boardPostDto) {

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
        responseDto.setStatus(201);
        responseDto.setBoardId(savedBoard.getBoardId());
        responseDto.setTitle(savedBoard.getTitle());
        responseDto.setContent(savedBoard.getContent());
        responseDto.setName(savedBoard.getName());
        responseDto.setMemberId(savedBoard.getMemberId());
        responseDto.setDate(savedBoard.getDate());
        responseDto.setAnswered(savedBoard.isAnswered());
        responseDto.setAnswer(savedBoard.getAnswer());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/viewBoard")
    public ResponseEntity<?> viewBoard(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid BoardViewDto boardViewDto) {

        Long boardId = boardViewDto.getBoardId();
        Long memberId = loginUserDto.getMemberId();

        // BoardService를 통해 게시물 불러오기
        Board viewBoard = boardService.viewBoard(boardId);

        if (viewBoard == null) {
            throw new BusinessExceptionHandler("게시물을 찾을 수 없습니다.", ErrorCode.BOARD_VIEW_ERROR);
        }

        // 작성자가 자신의 게시물을 들어간 경우
        if (viewBoard.getMemberId().equals(memberId)) {
            BoardPostResponseDto responseDto = new BoardPostResponseDto();
            responseDto.setStatus(202);
            responseDto.setBoardId(viewBoard.getBoardId());
            responseDto.setTitle(viewBoard.getTitle());
            responseDto.setContent(viewBoard.getContent());
            responseDto.setName(viewBoard.getName());
            responseDto.setMemberId(viewBoard.getMemberId());
            responseDto.setDate(viewBoard.getDate());
            responseDto.setAnswered(viewBoard.isAnswered());
            responseDto.setAnswer(viewBoard.getAnswer());

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else { // 일반 사용자가 다른사람의 게시물을 들어간 경우
            BoardPostResponseDto responseDto = new BoardPostResponseDto();
            responseDto.setStatus(200);
            responseDto.setBoardId(viewBoard.getBoardId());
            responseDto.setTitle(viewBoard.getTitle());
            responseDto.setContent(viewBoard.getContent());
            responseDto.setName(viewBoard.getName());
            responseDto.setMemberId(viewBoard.getMemberId());
            responseDto.setDate(viewBoard.getDate());
            responseDto.setAnswered(viewBoard.isAnswered());
            responseDto.setAnswer(viewBoard.getAnswer());

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editBoard(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid BoardEditDto boardEditDto) {

        String title = boardEditDto.getTitle();
        String content = boardEditDto.getContent();
        Long boardId = boardEditDto.getBoardId();

        // BoardService를 통해 게시물 수정
        Board editBoard = boardService.editBoard(boardId, title, content);

        BoardPostResponseDto responseDto = new BoardPostResponseDto();
        responseDto.setStatus(200);
        responseDto.setBoardId(editBoard.getBoardId());
        responseDto.setTitle(editBoard.getTitle());
        responseDto.setContent(editBoard.getContent());
        responseDto.setName(editBoard.getName());
        responseDto.setMemberId(editBoard.getMemberId());
        responseDto.setDate(editBoard.getDate());
        responseDto.setAnswered(editBoard.isAnswered());
        responseDto.setAnswer(editBoard.getAnswer());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteBoard(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid BoardDeleteDto boardDeleteDto) {

        Long boardId = boardDeleteDto.getBoardId();

        Board ifDeleteBoard = boardService.viewBoard(boardId);
        if (ifDeleteBoard == null) {
            throw new BusinessExceptionHandler("이미 삭제된 게시물 입니다.", ErrorCode.BOARD_DELETE_ERROR);
        }

        // BoardService를 통해 게시물 삭제
        boardService.deleteBoard(boardId);

        HttpOkResponseDto responseDto = HttpOkResponseDto.builder()
                .status(200)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public Page<Board> getBoard(@PageableDefault(size = 1) Pageable pageable) {
        return boardService.getAllBoards(pageable);
    }

    @GetMapping("/search")
    public Page<Board> searchBoard(
            @RequestParam(required = false) String title, @RequestParam(required = false) String author,
            @PageableDefault(size = 1) Pageable pageable) {

        if (title != null) {
            return boardService.searchBoardsByTitle(title, pageable);
        } else if (author != null) {
            return boardService.searchBoardsByName(author, pageable);
        } else {
            return boardService.getAllBoards(pageable);
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<?> answerBoard(@IfLogin LoginUserDto loginUserDto,
            @RequestBody @Valid BoardAnswerDto boardAnswerDto) {

        Long boardId = boardAnswerDto.getBoardId();
        String answer = boardAnswerDto.getAnswer();

        // BoardService를 통해 답변달기
        Board answerBoard = boardService.answerBoard(boardId, answer);

        BoardPostResponseDto responseDto = new BoardPostResponseDto();
        responseDto.setStatus(200);
        responseDto.setBoardId(answerBoard.getBoardId());
        responseDto.setTitle(answerBoard.getTitle());
        responseDto.setContent(answerBoard.getContent());
        responseDto.setName(answerBoard.getName());
        responseDto.setMemberId(answerBoard.getMemberId());
        responseDto.setDate(answerBoard.getDate());
        responseDto.setAnswered(answerBoard.isAnswered());
        responseDto.setAnswer(answerBoard.getAnswer());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
