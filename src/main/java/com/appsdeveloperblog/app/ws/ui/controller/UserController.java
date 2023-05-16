package com.appsdeveloperblog.app.ws.ui.controller;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Roles;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;



@RestController
@RequestMapping("/users") //http://localhost:8080/users
public class UserController {
	
	@Autowired
	UserService userService;
	@Autowired 
	AddressService addressService;
	
	
	@PostAuthorize("hasRole('Admin') or returnObject.userId == principal.userId")
	@ApiOperation(value="The Get User Details Web Service Endpoint",
			notes="${userController.getUserApiOperationNotes}")
	@GetMapping(path="/{id}")
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);
		returnValue = new ModelMapper().map(userDto, UserRest.class);
		//BeanUtils.copyProperties(userDto,returnValue);
		return returnValue;
	}
	
	@PostMapping
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
	{
		
		UserRest returnValue = new UserRest();
		
		if(userDetails.getFirstName().isEmpty() ) throw new NullPointerException(/*ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage()*/);
		
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto= modelMapper.map(userDetails,UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));
		
		
		//UserDto userDto = new UserDto();
		//BeanUtils.copyProperties(userDetails, userDto);
		
		//if(userDto !=null) {
			
		UserDto createdUser =userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		//BeanUtils.copyProperties(createdUser, returnValue);
		
		
		return returnValue;
	}
	
	@PutMapping(path="/{id}")
	public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();
		
		UserDto userDto= new ModelMapper().map(userDetails , UserDto.class);
				//UserDto();
		//BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser =userService.updateUser(id,userDto);
		returnValue= new ModelMapper().map(updatedUser , UserRest.class);
		
		
		//BeanUtils.copyProperties(updatedUser, returnValue);
		
		
		return returnValue;
	}
	
	@PreAuthorize("hasRole('ADMIN') or #id== principal.userId")
	
	//@Secured("ROLE_ADMIN")
	@DeleteMapping(path="/{id}")
	public OperationStatusModel deleteUser(@PathVariable String id) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
		
		
	}
	
	
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization",value="${userController.authorizationHeader.description}",paramType="header")
	})//authorization Header
	@GetMapping
	public List<UserRest> getUsers(@RequestParam(value="page",defaultValue="0")int page,
			@RequestParam(value="limit",defaultValue= "2")int limit){
		List<UserRest> returnValue= new ArrayList<>();
		
		if(page>0) page-=1;
		
		List<UserDto> users= userService.getUsers(page,limit);
		
		for(UserDto userDto: users ) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
	}
	
	//https://localhost:8080/mobile-app-ws/users/vgrqq/addresses
	@GetMapping(path="/{id}/addresses")
	public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
		
		List<AddressesRest> returnValue = new ArrayList<>();
		
		List<AddressDTO> addressDTO = addressService.getAddresses(id);
		
		if(addressDTO!=null && ! addressDTO.isEmpty()) {
		
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			returnValue =new ModelMapper().map(addressDTO,listType);
		}
		
		for(AddressesRest addressRest:returnValue) {
			Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
					.getUserAddress(id,addressRest.getAddressId())).withSelfRel();
			
			addressRest.add(selfLink);
		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(id).withRel("user");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(id)).withSelfRel();
//				.slash(userId)
//				.slash("addresses")
//				.slash(addressId)
				
		
		/*for(AddressDTO addressDTO: userDto.getAddresses()) {
			AddressesRest addressesRest =new AddressesRest();
			addressesRest= modelMapper.map( addressDTO,AddressesRest.class );
			returnValue.add(addressesRest);
		}*/
		
		//BeanUtils.copyProperties(userDto,returnValue);
		return CollectionModel.of(returnValue,userLink,selfLink);
	}
	
	
	
	
	@GetMapping(path="/{userId}/addresses/{addressId}")
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		
		
		AddressDTO addressesDto = addressService.getAddress(addressId);
		
		ModelMapper modelMapper = new ModelMapper();
		AddressesRest returnValue= modelMapper.map(addressesDto, AddressesRest.class);
		// http://localhost:8080/users/<userId>/addresses
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
//				.slash(userId)
//				.slash("addresses")
				.withRel("addresses");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId,addressId))
//				.slash(userId)
//				.slash("addresses")
//				.slash(addressId)
				.withSelfRel();
		
		
//		returnValue.add(userLink);
//		returnValue.add(userAddressesLink);
//		returnValue.add(selfLink);
		
		return EntityModel.of(returnValue, Arrays.asList(userLink,userAddressesLink,selfLink));
		
		
	}
	//http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
	@GetMapping(path = "/email-verification")
	public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token ) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		
		if(isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		return returnValue;
	}
	
	
	//http://localhost:8080/mobile-app-ws/users/password-reset-request
	
	@PostMapping(path = "/password-reset-request")
	public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if(operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	@PostMapping(path = "/password-reset")
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.resetPassword(
				passwordResetModel.getToken(),
				passwordResetModel.getPassword() );
				
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if(operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		return returnValue;

	
	
	}
}
