package com.eureka.ip.team1.urjung_main.user.service;

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
}
