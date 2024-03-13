package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private UserEntity user;
	private String existingUsername, nonExistingUsername;
	private List<UserDetailsProjection> userDetails;

	@BeforeEach
	void setUp() {
		existingUsername = "maria@gmail.com";
		nonExistingUsername = "marcelo@gmail.com";

		user = UserFactory.createUserEntity();
		userDetails = UserDetailsFactory.createCustomAdminUser(existingUsername);

		Mockito.when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetails);
		Mockito.when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(new ArrayList<>());

		Mockito.when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
		Mockito.when(repository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		UserService userServiceSpy = Mockito.spy(service);

		Mockito.doReturn(user).when(userServiceSpy).authenticated();
		UserEntity result = userServiceSpy.authenticated();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), user.getId());

	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		UserService userServiceSpy = Mockito.spy(service);
		Mockito.doReturn(user).when(userServiceSpy).authenticated();

		Assertions.assertThrows(UsernameNotFoundException.class, ()->{
			UserEntity result = service.authenticated();
		});

	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(existingUsername);
		Assertions.assertNotNull(existingUsername);
		Assertions.assertEquals(result.getUsername(), user.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Assertions.assertThrows(UsernameNotFoundException.class, ()->{
			service.loadUserByUsername(nonExistingUsername);
		});
	}
}
