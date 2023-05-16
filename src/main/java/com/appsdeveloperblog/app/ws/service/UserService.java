package com.appsdeveloperblog.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService{
		public UserDto createUser(UserDto user);

		UserDto getUser(String email);

		UserDto getUserByUserId(String userId);

		public UserDto updateUser(String userId,UserDto user);
		
		public void deleteUser(String userId);

		public List<UserDto> getUsers(int page, int limit);
		
		boolean verifyEmailToken(String token);

		boolean requestPasswordReset(String email);

	    public boolean resetPassword(String token, String password);
}
