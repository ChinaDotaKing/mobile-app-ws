package com.appsdeveloperblog.app.ws.shared;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		//fail("Not yet implemented");
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		assertNotNull(userId);
		assertTrue(userId.length() == 30);
		assertFalse(userId.equalsIgnoreCase(userId2));
	}

	@Test
	//@Disabled
	void testHasTokenNotExpired() {
		//fail("Not yet implemented");
		String token= utils.generateEmailVerificationToken("bjpj[epq");
		
				//"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjSUVRVVRZbkJPRlZoUTYzOUpHOGdWZjdmV280a2MiLCJleHAiOjE2NDE1NDU2MDR9.jUGVwdYO-G9NUjowlp0TnEb0dVT3phqNbDEWgFDELfiMg-YZAL1wAxY6lURKrQmlFksLBRk4arF1NPBdEddJjA";
		
		assertNotNull(token);
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		
		
		
		assertFalse(hasTokenExpired);
	}
	
	
	@Test
	final void testHasTokenExpired() {
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJxb3JtZWl4anNCU2duN2kySlJWMGVZaTBCeVZCNWEiLCJleHAiOjE2NDE1NDU4MzZ9.0woXP7HgmkfL7_wg8CT7K85QHiSURuK9Cef8x8Ab2TsU2WEXyCTZxgrHBRjIOvSUwC0XeLf5mE-XbLR5J3io_g";
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
		
		assertTrue(hasTokenExpired);
	
	}

}
