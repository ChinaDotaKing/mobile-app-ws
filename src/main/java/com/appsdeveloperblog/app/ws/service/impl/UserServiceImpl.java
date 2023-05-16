package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.PasswordResetTokenEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.PasswordResetTokenRepository;
import com.appsdeveloperblog.app.ws.io.repositories.RoleRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.security.UserPrincipal;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	
	@Override
	public UserDto createUser(UserDto user) {
		
		
		
		if(userRepository.findByEmail(user.getEmail())!=null) throw new RuntimeException("record already exists.");
		
		for(int i=0;i<user.getAddresses().size();i++) {
			AddressDTO address= user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId( utils.generateAddressId(30) );
			user.getAddresses().set(i, address );
		}
		
		UserEntity userEntity = new UserEntity();
		
		ModelMapper modelMapper= new ModelMapper();
		userEntity = modelMapper.map(user,UserEntity.class);
		//BeanUtils.copyProperties(user,userEntity);
		
		String publicUserId = utils.generateUserId(30);
		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		userEntity.setUserId(publicUserId);
		
		
		
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		
		userEntity.setEmailVerificationStatus(false);
		
		//Set Roles
		Collection<RoleEntity> roleEntities = new HashSet<>();
		for(String role: user.getRoles()) {
			RoleEntity roleEntity= roleRepository.findByName(role);
			if(roleEntity !=null) {
				roleEntities.add(roleEntity);
			}
		}
		
		userEntity.setRoles(roleEntities);
		UserEntity storedUserDetails = userRepository.save(userEntity);
		System.out.print("repository save ok");
		
		UserDto returnValue = new UserDto();
		returnValue = modelMapper.map(storedUserDetails,UserDto.class);
		//BeanUtils.copyProperties(storedUserDetails,returnValue);
		
		
		
		
		
		return returnValue;
	}

	@Override
	public UserDto getUser(String email) 
	{
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}
	
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		
		return new UserPrincipal(userEntity);
		
//		return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),
//				userEntity.getEmailVerificationStatus(),true,
//				true, true,new ArrayList<>());
		
		/*return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),
				new ArrayList<>());*/
	}
	
	@Override
	public UserDto getUserByUserId(String userId) {
		
		UserDto returnValue = new UserDto();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity ==null) throw new UsernameNotFoundException("User with ID "+ userId+" is not found");
		
		ModelMapper modelMap= new ModelMapper();
		returnValue= modelMap.map(userEntity, UserDto.class);
		//BeanUtils.copyProperties(userEntity,returnValue);
		
		
		return returnValue;
	}
	
	@Override
	public UserDto updateUser(String userId,UserDto user) {
		
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
	
	
	userEntity.setFirstName(user.getFirstName());
	userEntity.setLastName(user.getLastName());
	
	UserEntity updatedUserDetails = userRepository.save(userEntity);
	returnValue = new ModelMapper().map(updatedUserDetails,UserDto.class);
	
	//BeanUtils.copyProperties(updatedUserDetails, returnValue);
	
	return returnValue;
	}
	
	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.delete(userEntity);
	}
	
	@Override
	public List<UserDto> getUsers(int page, int limit){
		
		List<UserDto> returnValue= new ArrayList<>();
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		
		List<UserEntity> users = usersPage.getContent();
		
		for( UserEntity userEntity: users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		
		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue=false;
		
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		
		if(userEntity !=null) {
			boolean hastokenExpired = utils.hasTokenExpired(token);
			if(!hastokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}
		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		// TODO Auto-generated method stub
		
		boolean returnValue =false;
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) {
			return returnValue;
		}
		String token = utils.generatePasswordResetToken(userEntity.getUserId());
		
		PasswordResetTokenEntity passwordResetTokenEntity= new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		returnValue = true;
				/*new AmazonSES().sendPasswordResetRequest(
				userEntity.getFirstName(),
				userEntity.getEmail(),
				token
				);*/
				
		return returnValue;
		
	}

	@Override
	public boolean resetPassword(String token, String password) {
		// TODO Auto-generated method stub
		boolean returnValue= false;
		if( utils.hasTokenExpired(token)) return returnValue;
		
		
		
		PasswordResetTokenEntity passwordResetTokenEntity= passwordResetTokenRepository.findByToken(token);
		
		if(passwordResetTokenEntity== null) return returnValue;
		
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPassword);
		UserEntity savedUserEntity = userRepository.save(userEntity);
		
		if(savedUserEntity!= null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) returnValue = true;
		
		//passwordResetTokenRepository.delete(passwordResetTokenEntity);
		
		return returnValue;
	}
}
