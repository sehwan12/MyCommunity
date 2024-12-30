package com.example.myCommunity.Config;

import com.example.myCommunity.domain.Board;
import com.example.myCommunity.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BoardRepository boardRepository;

    @Override
    public void run(String... args) throws Exception {
        // 초기 게시판 데이터 삽입
        if (boardRepository.count() == 0) { // 게시판이 없는 경우에만 삽입
            boardRepository.save(new Board(null, "General"));
            boardRepository.save(new Board(null, "Announcements"));
            boardRepository.save(new Board(null, "Feedback"));
            boardRepository.save(new Board(null, "Support"));
            boardRepository.save(new Board(null, "Off-topic"));
        }
    }
}
