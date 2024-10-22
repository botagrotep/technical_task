package technikal.task.fishmarket.controllers;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import technikal.task.fishmarket.dtos.user.UserLoginRequestDto;
import technikal.task.fishmarket.dtos.user.UserRegistrationRequestDto;
import technikal.task.fishmarket.services.user.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("userLoginRequestDto", new UserLoginRequestDto("", ""));
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("userRegistrationRequestDto") @Valid UserRegistrationRequestDto requestDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }
        userService.registerUser(requestDto.getUsername(), requestDto.getPassword());
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userRegistrationRequestDto", new UserRegistrationRequestDto());
        return "register";
    }
}
