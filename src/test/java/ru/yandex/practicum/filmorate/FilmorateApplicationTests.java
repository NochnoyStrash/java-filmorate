package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
	private final UserStorage userStorage;
	private final FilmStorage filmStorage ;

	@BeforeEach
	public void createUser() {

		User user = User.builder().name("Marina")
				.email("marina@mail.ru")
				.birthday(LocalDate.of(1995,8,22))
				.login("Novak")
				.build();
		userStorage.createUser(user);

		Film film = Film.builder()
				.mpa(Rating.builder()
						.id(1)
						.name("Комедия")
						.build())
				.duration(150)
				.name("Только вперед")
				.description("jogfghjklkjfghj")
				.releaseDate(LocalDate.of(2014, 5,7))
				.build();
		filmStorage.addFilm(film);

	}

	@Test
	public void getUserTest() {
		User user1 = userStorage.getUser(1);
		assertEquals(user1.getId(), 1);
	}

	@Test
	public void updateUserTest() {
		User user2 = User.builder()
				.id(1)
				.email("dorime@kula.ru")
				.name("Fifa")
				.login("Tarantul")
				.birthday(LocalDate.of(2014,9,9))
				.build();
		user2 = userStorage.updateUser(user2);
		assertEquals("Fifa", user2.getName());
	}

	@Test
	public void findFilmTest() {
		Film film = filmStorage.findFilm(1);
	}

	@Test
	public void updateFilmTest() {

		Film film = Film.builder()
				.releaseDate(LocalDate.of(2022,3,3))
				.name("Вперед к миру")
				.description("длпмитьлдшрит")
				.id(1)
				.mpa(Rating.builder().id(2).name("Драма").build())
				.build();
		Film film1 = filmStorage.updateFilm(film);
		assertEquals(film1.getName(), "Вперед к миру");

	}


}