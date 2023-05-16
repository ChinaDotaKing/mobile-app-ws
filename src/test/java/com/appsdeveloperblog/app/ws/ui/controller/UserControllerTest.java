package com.appsdeveloperblog.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	@InjectMocks
	UserController userController;
	
	@Mock
	UserService userService;
	
	UserDto userDto;
	
	final String userId="oivhofheqoj8879";
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userDto = new UserDto();
		
		
		userDto.setId(1L);
		userDto.setFirstName("Eric");
		userDto.setLastName("Peng");
		userDto.setUserId(userId);
		userDto.setEncryptedPassword("bfgrrfq");
		userDto.setEmail("test@test.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setAddresses(getAddressesDto());
	}

	@Test
	void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		UserRest userRest= userController.getUser(userId);
		
		assertNotNull(userRest);
		assertEquals(userId,userRest.getUserId());
		assertEquals(userDto.getFirstName(),userRest.getFirstName());
		assertEquals(userDto.getLastName(),userRest.getLastName());
		assertTrue(userDto.getAddresses().size()==userRest.getAddresses().size());
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

}
