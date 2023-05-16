package com.appsdeveloperblog.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.AddressRepository;
import com.appsdeveloperblog.app.ws.io.repositories.PasswordResetTokenRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;



class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	AddressRepository addressRepository;
	
	@Mock
	Utils utils;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	UserEntity userEntity;
	
	String userId = "bgqqvqdqfeq";
	String encryptedPassword = "pghqoefjeqp67";
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userEntity = new UserEntity();
		
		userEntity.setId(1L);
		userEntity.setFirstName("Eric");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationToken("bwfwgkjqho");
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	void testGetUser() {
		//fail("Not yet implemented");
		
		
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@test.com");
		
		assertNotNull(userDto);
		
		assertEquals("Erii",userDto.getFirstName() );
	}
	
	@Test
	void testGetUser_UsernameNotFoundException() {
		
		when(userRepository.findByEmail( anyString() )).thenReturn(null);
		
		//userService.getUser("test@test.com");
		
		assertThrows(
				UsernameNotFoundException.class,
				()->{
			userService.getUser("test@test.com");
		} );
		
	}
	
	@Test
	void testCreateUser() {
		
		
		
		when(userRepository.findByEmail( anyString() ) ).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("lbkhdlkhqklfq");
		
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save( any(UserEntity.class) ) ).thenReturn(userEntity);
		
		
		
		
				
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Sergey");
		userDto.setLastName("Kargopolov");
		userDto.setPassword("12345678");
		userDto.setEmail("test@test.com");
		
		UserDto storedUserDetails = userService.createUser(userDto);
		
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(),storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(),storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(userEntity.getAddresses(),storedUserDetails.getAddresses());
		verify(utils,times(2)).generateAddressId(30);
		verify(bCryptPasswordEncoder,times(1)).encode("12345678");
		verify(userRepository,times(1)).save(any(UserEntity.class));
		//userRepository.save(userEntity);
		//bCryptPasswordEncoder.encode(user.getPassword());
	}
	
	private List<AddressDTO> getAddressesDto() {
		AddressDTO addressDto = new AddressDTO();
		
		addressDto.setType("shipping");
		addressDto.setCity("Vancouver");
		addressDto.setCountry("Canada");
		addressDto.setPostalCode("ABC123");
		addressDto.setStreetName("123 Street name");
		
		AddressDTO billingaddressDto = new AddressDTO();
		
		billingaddressDto.setType("shipping");
		billingaddressDto.setCity("Vancouver");
		billingaddressDto.setCountry("Canada");
		billingaddressDto.setPostalCode("ABC123");
		billingaddressDto.setStreetName("123 Street name");
		
		
		List<AddressDTO> addresses = new ArrayList<>();
		
		addresses.add(addressDto);
		addresses.add(billingaddressDto);
		
		return addresses;
	}
	private List<AddressEntity> getAddressesEntity( ){
		List<AddressDTO> addresses = getAddressesDto();
		
		Type listType = new TypeToken<List<AddressEntity> >() {}.getType();
		
		ModelMapper modelMapper = new ModelMapper();
		
		return modelMapper.map(addresses,listType);
		
	}

}
