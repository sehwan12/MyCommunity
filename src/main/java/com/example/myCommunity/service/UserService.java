package com.example.myCommunity.service;

import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.User;
import com.example.myCommunity.dto.UserLoginDTO;
import com.example.myCommunity.dto.UserRegistrationDTO;
import com.example.myCommunity.dto.UserResponseDTO;
import com.example.myCommunity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

//
//  •DTO를 통해 클라이언트로부터 회원가입 데이터를 받습니다.
//  •서비스 계층에서 DTO를 엔티티로 변환합니다.
//	•리포지토리를 사용하여 엔티티를 데이터베이스에 저장합니다.
//	•저장된 엔티티를 응답 DTO로 변환하여 클라이언트에 반환합니다.
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //중복 회원 검사(이메일로)
    public boolean isEmailDuplicated(String userEmail) {
        return userRepository.findByUserEmail(userEmail).isPresent();
    }

    // 유틸리티 메서드: User 엔티티를 UserResponseDTO로 변환
    private UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setUserId(user.getUserId());
        responseDTO.setUserEmail(user.getUserEmail());
        responseDTO.setUserPhone(user.getUserPhone());
        responseDTO.setBirth(user.getBirth());
        responseDTO.setUserGrade(user.getUserGrade().name());
        return responseDTO;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // 중복 이메일 확인
        if (isEmailDuplicated(registrationDTO.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 엔티티 생성 및 필드 설정
        User user = new User();
        user.setUserEmail(registrationDTO.getUserEmail());
        user.setUserPassword(registrationDTO.getUserPassword()); // 비밀번호 암호화
        user.setUserPhone(registrationDTO.getUserPhone());
        user.setBirth(registrationDTO.getBirthdate());
        user.setUserGrade(registrationDTO.getUserGrade());

        // 사용자 저장
        User savedUser = userRepository.save(user);

        // 응답 DTO 생성

        return toUserResponseDTO(savedUser);
    }

    //회원 삭제
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 게시글과 댓글의 user 필드를 null로 설정
        user.getPosts().forEach(post -> post.setUser(null));
        user.getComments().forEach(comment -> comment.setUser(null));

        userRepository.delete(user);
    }

    //단건 조회(ID로)
    public UserResponseDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toUserResponseDTO(user);
    }

    //회원 정보 수정
    @Transactional
    public UserResponseDTO updateUser(Integer userId, UserRegistrationDTO registrationDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setUserEmail(registrationDTO.getUserEmail());
        user.setUserPassword(registrationDTO.getUserPassword());
        user.setUserPhone(registrationDTO.getUserPhone());
        user.setBirth(registrationDTO.getBirthdate());
        user.setUserGrade(registrationDTO.getUserGrade());

        User updatedUser = userRepository.save(user);
        return toUserResponseDTO(updatedUser);
    }

    // 로그인
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        // 비밀번호 암호화되어 저장된 경우 PasswordEncoder로 비교 필요
        User user = userRepository.findByUserEmailAndUserPassword(
                        loginDTO.getUserEmail(), loginDTO.getUserPassword())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        return toUserResponseDTO(user);
    }

    // 생년월일과 휴대폰 번호로 회원 조회
    public UserResponseDTO findUserByBirthdateAndPhone(LocalDate birthdate, String userPhone) {
        User user = userRepository.findByBirthdateAndUserPhone(birthdate, userPhone)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return toUserResponseDTO(user);
    }

}

