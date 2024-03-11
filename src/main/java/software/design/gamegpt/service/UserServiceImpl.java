package software.design.gamegpt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.design.gamegpt.entity.Role;
import software.design.gamegpt.entity.User;
import software.design.gamegpt.repository.RoleRepository;
import software.design.gamegpt.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(User user) {
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = createRoles();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    private Role createRoles() {
        Role admin = new Role();
        admin.setName("ROLE_ADMIN");
        roleRepository.save(admin);

        Role user = new Role();
        user.setName("ROLE_USER");
        roleRepository.save(user);
        return user;
    }
}