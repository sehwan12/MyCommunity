package com.example.myCommunity.service;

import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.User;
import com.example.myCommunity.dto.UserLoginDTO;
import com.example.myCommunity.dto.UserRegistrationDTO;
import com.example.myCommunity.dto.UserUpdateDTO;
import com.example.myCommunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Objects;

//  •DTO를 통해 클라이언트로부터 회원가입/회원데이터 수정 데이터를 받습니다.
//  •서비스 계층에서 DTO를 엔티티로 변환합니다.
//	•리포지토리를 사용하여 엔티티를 데이터베이스에 저장합니다.
//	•저장된 엔티티를 응답 DTO로 변환하여 클라이언트에 반환합니다.
@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    //중복 회원 검사(이메일로)
    private boolean isEmailDuplicated(String userEmail) {
        return userRepository.findByUserEmail(userEmail).isPresent();
    }

    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        // 중복 이메일 확인
        if (isEmailDuplicated(registrationDTO.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }


        User user= User.builder()
                .userEmail(registrationDTO.getUserEmail())
                .userPassword(registrationDTO.getUserPassword())
                .userPhone(registrationDTO.getUserPhone())
                .birthdate(registrationDTO.getBirthdate())
                .userGrade(registrationDTO.getUserGrade())
                .username(registrationDTO.getUsername())
                .build();

        // 사용자 저장

        // 응답 DTO 생성

        return userRepository.save(user);
    }

    //회원 삭제
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }

    //단건 조회(ID로)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    //회원 정보 수정
    @Transactional
    public User updateUser(UserUpdateDTO updateDTO) {
        User user = userRepository.findById(updateDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.updateMember(updateDTO);

        return userRepository.save(user);
    }

    public User login(UserLoginDTO loginDTO) {
        // 이메일로 사용자 조회
        User user = userRepository.findByUserEmail(loginDTO.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 올바르지 않습니다."));

        // 비밀번호 검증
        if (!Objects.equals(loginDTO.getUserPassword(), user.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    // 생년월일과 휴대폰 번호로 회원 조회
    public User findUserByBirthdateAndPhone(LocalDate birthdate, String userPhone) {
        return userRepository.findByBirthdateAndUserPhone(birthdate, userPhone)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

}

