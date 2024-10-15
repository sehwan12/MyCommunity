package com.example.myCommunity.repository;

import com.example.myCommunity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 회원 조회
    Optional<User> findByUserEmail(String userEmail);

    // 생년월일과 휴대폰 번호로 회원 조회
    Optional<User> findByBirthdateAndUserPhone(LocalDate birthdate, String userPhone);

    // 로그인 시 사용 (이메일과 비밀번호로 회원 조회)
    Optional<User> findByUserEmailAndUserPassword(String userEmail, String userPassword);


}
