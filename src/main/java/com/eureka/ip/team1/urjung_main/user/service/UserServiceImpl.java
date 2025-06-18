package com.eureka.ip.team1.urjung_main.user.service;

import com.eureka.ip.team1.urjung_main.user.dto.UserInfoDto;
import org.springframework.stereotype.Service;

import com.eureka.ip.team1.urjung_main.common.exception.InternalServerErrorException;
import com.eureka.ip.team1.urjung_main.common.exception.NotFoundException;
import com.eureka.ip.team1.urjung_main.user.dto.UserDto;
import com.eureka.ip.team1.urjung_main.user.entity.User;
import com.eureka.ip.team1.urjung_main.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	
	@Override
	public UserDto findById(String userId) {
		UserDto userDto = new UserDto();
		
		try {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new NotFoundException("해당 유저가 없습니다."));
			
			return userDto.fromEntity(user);
		}catch(Exception e){
			log.debug("Find-user failed: ", e.getMessage());
            throw new InternalServerErrorException();
		}
	}

	// 마이페이지 내 본인 정보 조회
//	@Override
//	public UserInfoDto getUserInfoDto(String userId) {
//		try {
//			User user = userRepository.findById(userId)
//					.orElseThrow(() -> new NotFoundException("해당 유저가 없습니다."));
//
//			return UserInfoDto.builder()
//					.name(user.getName())
//					.email(user.getEmail())
//					.gender(user.getGender())
//					.birth(user.getBirth())
//					.membershipName(user.getMembership().getMembershipName())
//					.build();
//
//		} catch (Exception e) {
//			log.debug("Get-user-info failed: ", e.getMessage());
//			throw new InternalServerErrorException();
//		}
//	}
	@Override
	public UserInfoDto getUserInfoDto(String userId) {
		try {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new NotFoundException("해당 유저가 없습니다."));

			return UserInfoDto.builder()
					.name(user.getName())
					.email(user.getEmail())
					.gender(user.getGender())
					.birth(user.getBirth())
					.membershipName(user.getMembership().getMembershipName())
					.build();
		} catch (NotFoundException e) {
			// NotFoundException은 그대로 던짐
			throw e;
		} catch (Exception e) {
			// 그 외 예외만 InternalServerError
			throw new InternalServerErrorException();
		}
	}


}
