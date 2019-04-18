package com.ys.idatrix.quality.reference.user;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Lists;
import org.pentaho.di.core.util.Utils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;

@Service
public class CloudUserService {
	
	@Reference(check = false)
	private UserService userService;
	
	public User getUserInfo( String username ) {
		if(Utils.isEmpty(username)) {
			return null ;
		}
		User user = userService.findByUserName(username);
		return user ;
	}

	public User getRenterInfo( Long renterId ) {
		if( renterId == null ) {
			return null ;
		}
		User user = userService.findRenterByRenterId(renterId) ;
		return user ;
	}
	
	
	public  List<User>  getUsersByRenterId( Long renterId ) {
		if( renterId == null ) {
			return null ;
		}
		List<User> user =  userService.findUsersByRenterId( renterId) ;
		return user ;
	}

	public Boolean isRenter( String username ) {
		if( Utils.isEmpty(username) ) {
			return false ;
		}
		User user = getUserInfo( username );
		if( user == null ) {
			return false ;
		}
		User renter = getRenterInfo(user.getRenterId());
		if( renter == null ) {
			return false ;
		}
		if( renter.getUsername().equalsIgnoreCase(username)) {
			return  true ;
		}

		return false ;
	}
	
	public List<String> getUserNamesNyRolesAndRenter(int roleId, Long renterId){
		 List<User> user =  userService.findUserByRoleAndRenter(roleId, renterId);
		 if( user != null && user.size() >0 ) {
			 return user.stream().map(u -> { return u.getUsername() ;}).collect(Collectors.toList());
		 }
		return Lists.newArrayList() ;
	}
}
