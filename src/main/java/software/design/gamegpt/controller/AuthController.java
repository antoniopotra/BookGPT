package software.design.gamegpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import software.design.gamegpt.model.User;
import software.design.gamegpt.service.UserService;
import software.design.gamegpt.utils.ValidationResult;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") User user, BindingResult result, Model model) {
        ValidationResult validation = userService.validateCredentials(user);
        if (!validation.isValid()) {
            result.rejectValue(validation.errorField(), validation.errorCode(), validation.errorMessage());
            model.addAttribute("user", user);
            return "register";
        }

        userService.save(user);
        return "redirect:/register?success";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}