package com.example.myCommunity.service;

import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.dto.userDto.UserLoginDTO;
import com.example.myCommunity.dto.userDto.UserRegistrationDTO;
import com.example.myCommunity.dto.userDto.UserUpdateDTO;
import com.example.myCommunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;

    //중복 회원 검사(이메일로)
    private boolean isEmailDuplicated(String userEmail) {
        return userRepository.findByUserEmail(userEmail).isPresent();
    }

    @Transactional
    public Long registerUser(UserRegistrationDTO registrationDTO) {
        // 중복 이메일 확인
        if (isEmailDuplicated(registrationDTO.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Users user= registrationDTO.toEntity();

        // 사용자 저장
        userRepository.save(user);
        return user.getUserId();
    }

    //회원 삭제
    @Transactional
    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }


    //회원 정보 수정
    //update는 save필요없이 updateUser함수만 호출해주면 됨.
    @Transactional
    public void updateUser(UserUpdateDTO updateDTO) {
        Users user = userRepository.findById(updateDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.updateUser(updateDTO);
    }

    //로그인
    public Long login(UserLoginDTO loginDTO) {
        // 이메일로 사용자 조회
        Users user = userRepository.findByUserEmail(loginDTO.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 올바르지 않습니다."));

        // 비밀번호 검증
        if (!Objects.equals(loginDTO.getUserPassword(), user.getUserPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return user.getUserId();
    }

    //단건 조회(ID로)
    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // 생년월일과 휴대폰 번호로 회원 조회
    public Users findUserByBirthdateAndPhone(LocalDate birthdate, String userPhone) {
        return userRepository.findByBirthdateAndUserPhone(birthdate, userPhone)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    //모든 사용자 조회(페이징 적용)
    public Page<Users> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}

