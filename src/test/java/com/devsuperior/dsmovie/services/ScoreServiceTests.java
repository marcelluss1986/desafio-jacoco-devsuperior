package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository repository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private UserService userService;
	private UserEntity user;
	private Long existingUserId, nonExistingUserId;
	private Long existingMovieId, nonExistingMovieId;
	private ScoreEntity score;
	private MovieEntity movie;
	private MovieDTO movieDTO;
	private ScoreDTO scoreDTO;


	@BeforeEach
	void setUp() throws Exception {
		existingUserId = 1L;
		nonExistingUserId = 2L;
		existingMovieId = 1L;

		movie = MovieFactory.createMovieEntity();
		movieDTO = new MovieDTO(movie);
		score = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		user = UserFactory.createUserEntity();


		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenThrow(ResourceNotFoundException.class);
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
		Mockito.when(repository.saveAndFlush(any())).thenReturn(score);



	}

	@Test
	public void saveScoreShouldReturnMovieDTO() {
		MovieDTO result = service.saveScore(scoreDTO);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Mockito.doThrow(ResourceNotFoundException.class).when(movieRepository).findById(nonExistingMovieId);
		score.setMovie(new MovieEntity());
		scoreDTO = new ScoreDTO(score);
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			MovieDTO result = service.saveScore(scoreDTO);
		});
	}
}
