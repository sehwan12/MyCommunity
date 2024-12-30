package com.example.myCommunity.service;

import com.example.myCommunity.domain.Users;
import com.example.myCommunity.domain.UserGrade;
import com.example.myCommunity.dto.userDto.UserLoginDTO;
import com.example.myCommunity.dto.userDto.UserRegistrationDTO;
import com.example.myCommunity.dto.userDto.UserUpdateDTO;
import com.example.myCommunity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

//Spring Data JPA의 JpaRepository에서 제공하는 findById 메서드는 Optional<T>을 반환합니다. 이는 절대 null을 반환하지 않습니다. 대신 다음과 같이 동작합니다:
//   •	엔티티가 존재하지 않을 경우: Optional.empty()
//	•	엔티티가 존재할 경우: Optional.of(entity)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UsersServiceTest {
    @Autowired
    UserRepository userRepo;
    @Autowired
    UserService userService;

    //private User user;
    private UserRegistrationDTO registrationDTO;
    private UserRegistrationDTO registrationDTO2;
    private UserUpdateDTO updateDTO;
    private UserLoginDTO loginDTO;
    private UserLoginDTO loginFailDTO;
    private UserLoginDTO loginFailDTO2;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        registrationDTO = getUserRegistrationDTO();
        registrationDTO2 = getUserRegistrationDTO();
        updateDTO = getUserUpdateDTO();
        loginDTO = getUserLoginDTO();
        loginFailDTO = getLoginFailDTO();
        loginFailDTO2 = getLoginFailDTO2();
    }

    private UserRegistrationDTO getUserRegistrationDTO() {
        UserRegistrationDTO userRegistrationDTO=new UserRegistrationDTO();
        userRegistrationDTO.setBirthdate(LocalDate.of(2001,8,4));
        userRegistrationDTO.setUserGrade(UserGrade.NORMAL);
        userRegistrationDTO.setUserEmail("tlrpv12@naver.com");
        userRegistrationDTO.setUserPhone("010-1234-5678");
        userRegistrationDTO.setUserPassword("12345678");
        userRegistrationDTO.setUsername("kento");
        return userRegistrationDTO;
    }

    private UserUpdateDTO getUserUpdateDTO() {
        UserUpdateDTO userUpdateDTO=new UserUpdateDTO();
        userUpdateDTO.setBirthdate(LocalDate.of(2001,7,4));
        userUpdateDTO.setUserPassword("09876543");
        userUpdateDTO.setUserPhone("011-1234-5678");
        userUpdateDTO.setUsername("nanami");
        return userUpdateDTO;
    }
    //로그인 성공
    private UserLoginDTO getUserLoginDTO() {
        UserLoginDTO userLoginDTO=new UserLoginDTO();
        userLoginDTO.setUserEmail("tlrpv12@naver.com");
        userLoginDTO.setUserPassword("12345678");
        return userLoginDTO;

    }
    //패스워드가 다름
    private UserLoginDTO getLoginFailDTO() {
        UserLoginDTO userLoginDTO=new UserLoginDTO();
        userLoginDTO.setUserEmail("tlrpv12@naver.com");
        userLoginDTO.setUserPassword("1234567");
        return userLoginDTO;
    }
    //이메일이 다름
    private UserLoginDTO getLoginFailDTO2() {
        UserLoginDTO userLoginDTO=new UserLoginDTO();
        userLoginDTO.setUserEmail("tlrpv11@naver.com");
        userLoginDTO.setUserPassword("12345678");
        return userLoginDTO;
    }

    @Test
    void 회원가입_성공() {
        // given

        // when
        Long userid = userService.registerUser(registrationDTO);
        Users user=userRepository.getById(userid);

        // then
        assertNotNull(user);
        assertEquals(registrationDTO.getUserEmail(), user.getUserEmail());
        assertEquals(registrationDTO.getUserGrade(), user.getUserGrade());
        assertEquals(registrationDTO.getUserPhone(), user.getUserPhone());
        assertEquals(registrationDTO.getBirthdate(), user.getBirthdate());
        assertEquals(registrationDTO.getUsername(), user.getUsername());
    }


    @Test
    void 중복_회원_검증() throws Exception {
        // 첫 번째 회원 가입 시도: 이메일이 중복되지 않음
        // when
        userService.registerUser(registrationDTO);

        // then
        // 두 번째 회원 가입 시도: 이메일이 이미 존재함
        // 예외 발생 검증
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(registrationDTO2);
        });

        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
    }
    @Test
    void 회원_삭제() {
        //given
        Long userid= userService.registerUser(registrationDTO);
        //when
        userService.deleteUser(userid);
        //then
        assertFalse(userRepo.findById(userid).isPresent(), "사용자가 삭제되어야 합니다.");
    }

    @Test
    void 회원정보_수정() {
        //given
        Long userid= userService.registerUser(registrationDTO);
        Users user=userService.getUserById(userid);
        updateDTO.setUserId(userid);
        //when
        userService.updateUser(updateDTO);
        //then
        assertEquals(updateDTO.getUserPassword(), user.getUserPassword());
        assertEquals(updateDTO.getUserPhone(), user.getUserPhone());
        assertEquals(updateDTO.getBirthdate(), user.getBirthdate());
        assertEquals(updateDTO.getUsername(), user.getUsername());
    }

    @Test
    void 로그인_성공() {
        //given
        Long userid= userService.registerUser(registrationDTO);
        Users user=userService.getUserById(userid);
        //when
        userService.login(loginDTO);
        //then
        assertDoesNotThrow(()-> userService.login(loginDTO));
        assertEquals(loginDTO.getUserPassword(), user.getUserPassword());
        assertEquals(loginDTO.getUserEmail(), user.getUserEmail());
    }

    @Test
    void 로그인_이메일_실패() {
        //given
        Long userid= userService.registerUser(registrationDTO);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginFailDTO2);
        });
        //then
        assertEquals("이메일이 올바르지 않습니다.", exception.getMessage());
    }

    @Test
    void 로그인_패스워드_실패() {
        //given
        Long userid= userService.registerUser(registrationDTO);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginFailDTO);
        });
        //then
        assertEquals("비밀번호가 올바르지 않습니다.", exception.getMessage());
    }

    @Test

    void 단건_조회(){
        Long userid= userService.registerUser(registrationDTO);
        Users user=userService.getUserById(userid);
        Users user2=userService.getUserById(user.getUserId());
        assertEquals(user, user2);
    }


    @Test
    void 생일_전화번호로_조회() {
        //given
        Long userid= userService.registerUser(registrationDTO);
        Users user=userService.getUserById(userid);
        //when
        Users user2=userService.findUserByBirthdateAndPhone(user.getBirthdate(),user.getUserPhone());
        //then
        assertEquals(user, user2);

    }
}