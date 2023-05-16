package com.appsdeveloperblog.app.ws.service;

import java.util.List;

import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

public interface AddressService {

	  List<AddressDTO> getAddresses(String userId) ;

	AddressDTO getAddress(String addressId);

}
