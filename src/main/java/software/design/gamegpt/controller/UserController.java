package software.design.gamegpt.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import software.design.gamegpt.model.ConfirmationToken;
import software.design.gamegpt.model.User;
import software.design.gamegpt.service.EmailService;
import software.design.gamegpt.service.UserService;

@Controller
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
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
