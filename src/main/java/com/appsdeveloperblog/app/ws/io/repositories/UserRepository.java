package com.appsdeveloperblog.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository <UserEntity, Long> {
		
	UserEntity findByEmail(String email);

	UserEntity findByUserId(String userId);

	UserEntity findUserByEmailVerificationToken(String token);
	
	
	
	@Query(value="select * from Users u where u.Email_Verification"
			+ "_Status = 'true'",
			countQuery="select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'",
			nativeQuery = true
			)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(
			Pageable pageableRequest);
	
	@Query(value= "select * from Users u where u.first_name = ?1 "/*and u.last_name=?2*/
			,nativeQuery=true)
	List<UserEntity> findUserByFirstName(String firstName/*,String firstName*/);
	
	@Query(value= "select * from Users u where u.last_name = :lastName  "/*and u.last_name=?2*/
			,nativeQuery=true)
	List<UserEntity> findUserByLastName(@Param("lastName") String lastName/*,String firstName*/);
	
	@Query(value= "select * from Users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%"/*and u.last_name=?2*/
			,nativeQuery=true)
	List<UserEntity> findUserByKeyword(@Param("keyword") String keyword/*,String firstName*/);

	@Query(value= "select u.first_name,u.last_name from Users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%"/*and u.last_name=?2*/
			,nativeQuery=true)
	List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword/*,String firstName*/);
	
	
	@Transactional
	@Modifying
	@Query(value= "update users u set u.EMAIL_VERIFICATION_STATUS=:emailVerificationStatus where u.user_id=:userId"/*and u.last_name=?2*/
			,nativeQuery=true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus")boolean emailVerificationStatus, @Param("userId") String userId);

	@Query("select user from UserEntity user where user.userId= :userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);

	@Query("select user.firstName, user.lastName from UserEntity user where user.userId =:userId")
	List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);
	
	@Modifying
	@Transactional
	@Query(value= "update UserEntity u set u.emailVerificationStatus=:emailVerificationStatus where u.userId=:userId"/*and u.last_name=?2
			,nativeQuery=true*/)
	void updateUserEntityEmailVerificationStatus(@Param("emailVerificationStatus")boolean emailVerificationStatus, @Param("userId") String userId);
	
}
