package software.design.gamegpt.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import software.design.gamegpt.model.ConfirmationToken;
import software.design.gamegpt.model.User;
import software.design.gamegpt.service.EmailService;
import software.design.gamegpt.service.UserService;
import software.design.gamegpt.utils.Validator;

@Controller
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final Validator emailValidator = new Validator("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final Validator passwordValidator = new Validator("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$");

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (!emailValidator.validate(user.getEmail())) {
            result.rejectValue("email", "invalid_email_format", "Invalid email format.");
        }
        if (!passwordValidator.validate(user.getPassword())) {
            result.rejectValue("password", "invalid_password_format", "Password must contain at least 8 characters and one of each: uppercase, lowercase, digit, symbol.");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "username_exists", "Username already in use.");
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            result.rejectValue("email", "email_exists", "Email already in use.");
        }
        if (result.hasErrors()) {
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

    @GetMapping("/upgrade")
    public String requestRoleUpgrade() {
        User user = getAuthenticatedUser();
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        emailService.saveConfirmationToken(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Become an admin!");
        mailMessage.setText("To become an admin, please click here: http://localhost:8080/confirm-upgrade?token=" + confirmationToken.getConfirmationToken());
        emailService.sendEmail(mailMessage);

        return "redirect:/index";
    }

    @RequestMapping(value = "/confirm-upgrade", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = emailService.findConfirmationToken(confirmationToken);

        if (token != null) {
            userService.updateRole(token.getUser().getId(), "ROLE_ADMIN");
        } else {
            System.out.println("The link is invalid or broken!");
        }

        return "redirect:/index";
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.findByUsername(username);
    }
}