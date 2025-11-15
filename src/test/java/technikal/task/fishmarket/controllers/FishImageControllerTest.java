package technikal.task.fishmarket.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.services.FishImageService;
import technikal.task.fishmarket.services.FishRepository;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(FishImageController.class)
@Import({ SecurityConfig.class, PasswordEncoderProvider.class })
public class FishImageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FishRepository fishRepository;

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
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void testUploadImages_RedirectsToFish() throws Exception {
		MockMultipartFile file = new MockMultipartFile("images", "test.png", MediaType.IMAGE_PNG_VALUE,
				"data".getBytes());

		when(fishRepository.findById(1)).thenReturn(Optional.of(fish));

		mockMvc.perform(multipart("/fish/images/upload").file(file).param("fishId", "1"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/fish"));

		verify(fishImageService).saveFishImage(fish, List.of(file));
	}
 
	@Test
	@WithMockUser
	void uploadImages_FishNotFound() throws Exception {
		MockMultipartFile file = new MockMultipartFile("images", "test.png", MediaType.IMAGE_PNG_VALUE,
				"data".getBytes());

		when(fishRepository.findById(1)).thenReturn(Optional.empty());

		mockMvc.perform(multipart("/fish/images/upload").file(file).param("fishId", "1"))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));
	}
 
	@Test
	@WithMockUser
	void testViewImages_ShowsFishImages() throws Exception {
		List<FishImage> images = new ArrayList<>();
		images.add(new FishImage());
		when(fishRepository.findById(1)).thenReturn(Optional.of(fish));
		when(fishImageService.getImagesFish(fish)).thenReturn(images);

		mockMvc.perform(get("/fish/images/1")).andExpect(status().isOk()).andExpect(model().attributeExists("fish"))
				.andExpect(model().attributeExists("images")).andExpect(view().name("fishImages"));

		verify(fishImageService).getImagesFish(fish);
	}

	@Test
	@WithMockUser
	void viewImages_FishNotFound() throws Exception {
	    when(fishRepository.findById(1)).thenReturn(Optional.empty());

	    mockMvc.perform(get("/fish/images/1"))
	           .andExpect(status().isNotFound());
	}


	@Test
	@WithMockUser
	void testDeleteFishImage_Redirects() throws Exception {
		mockMvc.perform(post("/fish/images/delete").param("fishId", "1").param("imageId", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/fish/images/1"));

		verify(fishImageService).deleteFishImage(10L);
	}
}
