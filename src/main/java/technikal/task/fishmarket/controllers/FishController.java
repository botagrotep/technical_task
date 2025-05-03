package technikal.task.fishmarket.controllers;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.Security;
import java.util.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import technikal.task.fishmarket.config.JwtUtils;
import technikal.task.fishmarket.models.*;
import technikal.task.fishmarket.services.FishRepository;
import technikal.task.fishmarket.services.UsersRepository;

@Controller
@RequestMapping("/fish")
public class FishController {
	
	@Autowired
	private FishRepository repo;
	@Autowired
	private UsersRepository repoUsers;
	@Autowired
	private JwtUtils jwtUtils;


	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		List<Fish> fishlist = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishlist);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Authenticated user: " + auth.getName() + ", Authorities: " + auth.getAuthorities());
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			model.addAttribute("auth_login", auth.getName());
			model.addAttribute("auth_role", auth.getAuthorities().iterator().next().getAuthority());
		}
		return "index";
	}
	
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}

	@GetMapping("/delete")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String deleteFish(@RequestParam int id) {
		try {
			Fish fish = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Fish not found with id: " + id));

			for (FishImage image : fish.getImages()) {
				Path imagePath = Paths.get("public/images/" + image.getFileName());
				try {
					Files.deleteIfExists(imagePath);
				} catch (Exception ex) {
					System.out.println("Помилка при видаленні зображення : " + image.getFileName() + ", Exception: " + ex.getMessage());
				}
			}
			repo.delete(fish);
		} catch (Exception ex) {
			System.out.println("Помилка при видаленні рибки : " + ex.getMessage());
		}
		return "redirect:/fish";
	}

	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result,
						  @RequestParam("login") String login) {

		if (fishDto.getImageFiles() == null || fishDto.getImageFiles().isEmpty()) {
			result.addError(new FieldError("fishDto", "imageFiles", "Потрібно завантажити від 1 до 9 фотографій"));
		} else if (fishDto.getImageFiles().size() > 9) {
			result.addError(new FieldError("fishDto", "imageFiles", "Потрібно завантажити від 1 до 9 фотографій"));
		} else {
			boolean hasNonEmptyFile = fishDto.getImageFiles().stream().anyMatch(file -> !file.isEmpty());
			if (!hasNonEmptyFile) {
				result.addError(new FieldError("fishDto", "imageFiles", "Потрібно завантажити від 1 до 9 фотографій"));
			}
		}
		if (result.hasErrors()) {
			return "createFish";
		}

		Date catchDate = new Date();
		List<FishImage> imageList = new ArrayList<>();

		for (MultipartFile imageFile : fishDto.getImageFiles()) {
			if (!imageFile.isEmpty()) {
				String storageFileName = catchDate.getTime() + "_" + imageFile.getOriginalFilename();
				try {
					String uploadDir = "public/images/";
					Path uploadPath = Paths.get(uploadDir);

					if (!Files.exists(uploadPath)) {
						Files.createDirectories(uploadPath);
					}

					try (InputStream inputStream = imageFile.getInputStream()) {
						Files.copy(inputStream, Paths.get(uploadDir + storageFileName),StandardCopyOption.REPLACE_EXISTING);
					}
					FishImage fishImage = new FishImage();
					fishImage.setFileName(storageFileName);
					imageList.add(fishImage);
				} catch (Exception ex) {
					System.out.println("Exception: " + ex.getMessage());
				}
			}
		}
		Fish fish = new Fish();
		fish.setCatchDate(catchDate);
		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		fish.setUsers(repoUsers.findByLogin(login));

		for (FishImage img : imageList) {
			img.setFish(fish);
		}

		fish.setImages(imageList);
		repo.save(fish);
		return "redirect:/fish";
	}

	@GetMapping("/login")
	public String login(Model model) {
		if (SecurityContextHolder.getContext().getAuthentication() != null &&
				!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
			return "redirect:/fish";
		}
		LoginRequest login = new LoginRequest();
		model.addAttribute("loginReq", login);
		return "login";
	}
	@GetMapping("/register")
	public String register(Model model) {
		RegisterRequest register = new RegisterRequest();
		model.addAttribute("users", register);
		return "register";
	}

	@PostMapping("/register")
	public String handleRegister(@Valid @ModelAttribute("users") RegisterRequest request, BindingResult result) {
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			result.addError(new FieldError("users", "confirmPassword", "Паролі не співпадають.Спробуйте ще раз"));
		}
		if(repoUsers.findByLogin(request.getLogin())!=null) {
			result.addError(new FieldError("users", "login", "Такий логін вже зареєстровано"));
		}
		if (result.hasErrors()) {
			return "register";
		}
		Users user = new Users();
		user.setLogin(request.getLogin());
		try {
			if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
				Security.addProvider(new BouncyCastleProvider());
			}
			MessageDigest md = MessageDigest.getInstance("WHIRLPOOL", "BC");
			byte[] hashBytes = md.digest(request.getPassword().getBytes(StandardCharsets.UTF_8));
			String encodedPassword = Base64.getEncoder().encodeToString(hashBytes);
			user.setPassword(encodedPassword);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		if(request.getLogin().equals("admin")) {
			user.setRole("ROLE_ADMIN");
		}else{
			user.setRole("ROLE_USER");
		}

		repoUsers.save(user);

		return "redirect:/fish/login";
	}

	@PostMapping("/login")
	public String handleLogin(@Valid @ModelAttribute("loginReq") LoginRequest request,
							  BindingResult result,
							  RedirectAttributes redirectAttributes,
							  HttpServletResponse response ) {
		if (repoUsers.findByLogin(request.getLogin()) == null) {
			result.addError(new FieldError("loginReq", "login", "Логін не знайдено"));
		}
		Users user = repoUsers.findByLogin(request.getLogin());
		try {
			if (user != null && !verifyPassword(request.getPassword(), user.getPassword())) {
				result.addError(new FieldError("loginReq", "password", "Пароль не правильний"));
			}
		} catch (Exception ex) {
			result.addError(new FieldError("loginReq", "password", "Помилка шифрування пароля. Спробуйте ще раз"));
		}

		if (result.hasErrors()) {
			return "login";
		}

		String token = jwtUtils.generateToken(user);

		Cookie jwtCookie = new Cookie("jwt_token", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(3600);
		response.addCookie(jwtCookie);

		redirectAttributes.addFlashAttribute("token", token);
		redirectAttributes.addFlashAttribute("login", user.getLogin());
		redirectAttributes.addFlashAttribute("role", user.getRole());

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user.getLogin(), null, AuthorityUtils.createAuthorityList(user.getRole())
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "redirect:/fish";
	}
	private boolean verifyPassword(String rawPassword, String storedHash) throws Exception {
		try {
			MessageDigest md = MessageDigest.getInstance("WHIRLPOOL", "BC");
			byte[] hashBytes = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
			String encodedInputPassword = Base64.getEncoder().encodeToString(hashBytes);
			return encodedInputPassword.equals(storedHash);
		} catch (Exception ex) {
			throw new Exception("Помилка розшифровки пароля");
		}
	}
	@PostMapping("/logout")
	public String logout(HttpServletResponse response) {
		Cookie jwtCookie = new Cookie("jwt_token", null);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(0);
		response.addCookie(jwtCookie);
		SecurityContextHolder.clearContext();

		return "redirect:/fish";
	}

}
