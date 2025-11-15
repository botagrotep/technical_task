package technikal.task.fishmarket.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityNotFoundException;
import technikal.task.fishmarket.config.PasswordEncoderProvider;
import technikal.task.fishmarket.config.SecurityConfig;
import technikal.task.fishmarket.dto.FishUpdateDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.services.FishImageService;
import technikal.task.fishmarket.services.FishRepository;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(FishController.class)
@Import({ SecurityConfig.class, PasswordEncoderProvider.class })
public class FishControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FishRepository repo;

	@MockBean
	private FishImageService fishImageService;

	private Fish fish;

	@BeforeEach
	void setup() {
		fish = new Fish();
		fish.setId(1);
		fish.setName("Carp");
		fish.setPrice(100);
		fish.setImageFileName("main.png");
		fish.setCatchDate(new Date());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testShowCreatePage() throws Exception {
		mockMvc.perform(get("/fish/create")).andExpect(status().isOk()).andExpect(model().attributeExists("fishDto"))
				.andExpect(view().name("createFish"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testAddFish_Success() throws Exception {
		MockMultipartFile file = new MockMultipartFile("imageFile", "fish.png", MediaType.IMAGE_PNG_VALUE,
				"data".getBytes());

		mockMvc.perform(multipart("/fish/create").file(file).param("name", "Carp").param("price", "100"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/fish"));

		verify(repo).save(any(Fish.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testAddFish_ValidationError() throws Exception {

		MockMultipartFile emptyFile = new MockMultipartFile("imageFile", "", MediaType.IMAGE_PNG_VALUE, new byte[0]);

		mockMvc.perform(multipart("/fish/create").file(emptyFile).param("name", "Carp").param("price", "100"))
				.andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("fishDto", "imageFile"))
				.andExpect(view().name("createFish"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteFish_Success() throws Exception {
		when(repo.findById(1)).thenReturn(Optional.of(fish));

		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

			mockedFiles.when(() -> Files.delete(any(Path.class))).thenAnswer(invocation -> null);

			mockMvc.perform(get("/fish/delete").param("id", "1")).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/fish"));

			verify(repo).delete(fish);

			mockedFiles.verify(() -> Files.delete(any(Path.class)));
		}
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteFish_NotFound() throws Exception {
		when(repo.findById(1)).thenReturn(Optional.empty());

		mockMvc.perform(get("/fish/delete").param("id", "1")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/fish"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testEditFishForm_Success() throws Exception {
		List<FishImage> images = new ArrayList<>();
		images.add(new FishImage());

		when(repo.findById(1)).thenReturn(Optional.of(fish));
		when(fishImageService.getImagesFish(fish)).thenReturn(images);

		mockMvc.perform(get("/fish/edit/1")).andExpect(status().isOk())
				.andExpect(model().attributeExists("fishUpdateDto")).andExpect(model().attributeExists("fish"))
				.andExpect(model().attributeExists("images")).andExpect(view().name("editFish"));

		verify(fishImageService).getImagesFish(fish);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testEditFishForm_NotFound() throws Exception {
		// Мокаем репозиторий: рыба с id=1 не найдена
		when(repo.findById(1)).thenThrow(new EntityNotFoundException("Fish not found"));

		mockMvc.perform(get("/fish/edit/1")).andExpect(status().isOk()) // контроллер возвращает "error/404" с 200 OK
				.andExpect(view().name("error/404")).andExpect(model().attributeExists("errorMessage"))
				.andExpect(model().attribute("errorMessage", "Fish not found"));

		// Проверяем, что repo.findById действительно вызвано
		verify(repo).findById(1);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateFish() throws Exception {
		FishUpdateDto dto = new FishUpdateDto();
		dto.setId(1);
		dto.setName("Updated Carp");
		dto.setPrice(150);

		mockMvc.perform(post("/fish/edit").param("id", "1").param("name", "Updated Carp").param("price", "150"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/fish"));

		verify(fishImageService).updateFish(any(FishUpdateDto.class));
	}
}
