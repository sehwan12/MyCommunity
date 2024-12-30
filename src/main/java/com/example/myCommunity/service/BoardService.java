package com.example.myCommunity.service;

import com.example.myCommunity.Exception.BoardNotFoundException;
import com.example.myCommunity.domain.Board;
import com.example.myCommunity.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Board getBoardByName(String boardName){
        return boardRepository.findByBoardName(boardName)
                .orElseThrow(()-> new BoardNotFoundException("게시판을 찾을 수 없습니다."));
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }
}
