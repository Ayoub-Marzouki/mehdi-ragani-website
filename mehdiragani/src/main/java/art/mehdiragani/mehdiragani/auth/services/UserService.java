package art.mehdiragani.mehdiragani.auth.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import art.mehdiragani.mehdiragani.admin.dto.UserForm;
import art.mehdiragani.mehdiragani.auth.dto.RegisterForm;
import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.models.enums.UserRole;
import art.mehdiragani.mehdiragani.auth.repositories.UserRepository;


@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(RegisterForm registerForm) {
        User user = new User();
        user.setUsername(registerForm.getUsername());
        user.setEmail(registerForm.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerForm.getPassword()));
        user.setFirstName(registerForm.getFirstName());
        user.setLastName(registerForm.getLastName());
        user.setPhoneNumber(registerForm.getPhoneNumber());
        user.setRole(UserRole.Customer);
        
        return userRepository.save(user);    
    }
    

    public User createUser(UserForm userForm) {
        User user = new User();
        user.setUsername(userForm.getUsername());
        user.setEmail(userForm.getEmail());
        user.setPasswordHash(passwordEncoder.encode("customUser"));
        user.setFirstName(userForm.getFirstName());
        user.setLastName(userForm.getLastName());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setRole(userForm.getRole());

        return userRepository.save(user);
    }

    public User updateUser(UserForm userForm) {
        User user = userRepository.findById(userForm.getId()).get();

        user.setUsername(userForm.getUsername());
        user.setEmail(userForm.getEmail());
        user.setFirstName(userForm.getFirstName());
        user.setLastName(userForm.getLastName());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setRole(userForm.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));  
    }

    public Optional<User> getUserByUsername(String Username) {
        return userRepository.findByUsername(Username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            "No user found with username: " + username));
        
            return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPasswordHash())
            .roles(user.getRole().name())
            .accountExpired(!user.isAccountNonExpired())
            .accountLocked(!user.isAccountNonLocked())
            .credentialsExpired(!user.isCredentialsNonExpired())
            .disabled(!user.isEnabled())
            .build();
    } 
}
