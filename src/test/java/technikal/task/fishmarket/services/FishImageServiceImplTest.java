package technikal.task.fishmarket.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import technikal.task.fishmarket.dto.FishUpdateDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.repasitories.FishImageRepository;

@ExtendWith(MockitoExtension.class)
public class FishImageServiceImplTest {

	@Mock
	private FishRepository fishRepository;

	@Mock
	private FishImageRepository fishImageRepository;

	@InjectMocks
	private FishImageServiceImpl fishImageService;

	Fish fish;

	@BeforeEach
	void setup() {
		fish = new Fish();
		fish.setId(1);
		fish.setName("Carp");
		fish.setPrice(100);
	}

	@Test
	void testSaveFishImage_SavesImages() throws Exception {

		MultipartFile fileMock = mock(MultipartFile.class);

		when(fileMock.isEmpty()).thenReturn(false);
		when(fileMock.getOriginalFilename()).thenReturn("test.png");
		when(fileMock.getInputStream()).thenReturn(InputStream.nullInputStream());

		fishImageService.saveFishImage(fish, List.of(fileMock));

		ArgumentCaptor<List<FishImage>> captor = ArgumentCaptor.forClass(List.class);

		verify(fishImageRepository).saveAll(captor.capture());
		List<FishImage> savedImages = captor.getValue();

		assertEquals(1, savedImages.size());
		assertEquals(fish, savedImages.get(0).getFish());
		assertTrue(savedImages.get(0).getFileName().contains("test.png"));
	}

	@Test
	void testGetImagesFish_ReturnsImages() {
		FishImage img = new FishImage();
		img.setFish(fish);

		when(fishImageRepository.findByFish(fish)).thenReturn(List.of(img));

		List<FishImage> result = fishImageService.getImagesFish(fish);

		assertEquals(1, result.size());
		assertEquals(fish, result.get(0).getFish());
	}

	@Test
	void testUpdateFish_UpdatesFields() {
		FishUpdateDto dto = new FishUpdateDto();
		dto.setId(1);
		dto.setName("New Name");
		dto.setPrice(200);

		when(fishRepository.findById(1)).thenReturn(Optional.of(fish));

		fishImageService.updateFish(dto);

		assertEquals("New Name", fish.getName());
		assertEquals(200, fish.getPrice());

		verify(fishRepository).save(fish);
	}

	@Test
	void testUpdateFish_ThrowsError_WhenFishNotFound() {
		FishUpdateDto dto = new FishUpdateDto();
		dto.setId(10);

		when(fishRepository.findById(10)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> fishImageService.updateFish(dto), "Fish not found");
	}
 
	@Test
	void testDeleteFishImage_RemovesImage() throws Exception {

		FishImage img = new FishImage();
		img.setId(5L);
		img.setFileName("delete.png");

		when(fishImageRepository.findById(5L)).thenReturn(Optional.of(img));

		fishImageService.deleteFishImage(5L);

		verify(fishImageRepository).delete(img);
	}

	@Test
	void testDeleteFishImage_ThrowsError_ImageNotFound() {

		when(fishImageRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> fishImageService.deleteFishImage(99L), "Image not found");
	}

}
