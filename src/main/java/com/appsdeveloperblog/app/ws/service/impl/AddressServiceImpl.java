package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.AddressRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	AddressRepository addressRepository;
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		// TODO Auto-generated method stub
		
		List<AddressDTO> returnValue=new ArrayList<>();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		
		
		if(userEntity==null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		
		for(AddressEntity addressEntity: addresses) {
			
			
			AddressDTO addressDTO = new ModelMapper().map( addressEntity,AddressDTO.class );
			returnValue.add(addressDTO);
		}
		
		return returnValue;
	}
	
	@Override
	public AddressDTO getAddress(String addressId) {
		
		
		AddressEntity addressEntity= addressRepository.findByAddressId(addressId);
		
		return new ModelMapper().map(addressEntity, AddressDTO.class);
	}

}
