package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.*;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public MeResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken");
        
        // Logika: Harus ada salah satu, email atau phone
        if ((req.getEmail() == null || req.getEmail().isBlank()) && 
            (req.getPhoneNumber() == null || req.getPhoneNumber().isBlank())) {
            throw new IllegalArgumentException("Email or Phone Number is required");
        }

        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already used");
        
        if (req.getPhoneNumber() != null && userRepository.existsByPhoneNumber(req.getPhoneNumber()))
            throw new IllegalArgumentException("Phone number already used");

        var user = new User(
                req.getUsername(),
                req.getDisplayName(), // Sekarang pakai Display Name
                req.getEmail(),
                req.getPhoneNumber(),
                passwordEncoder.encode(req.getPassword()),
                Role.USER);
        
        userRepository.save(user);

        return mapToMeResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        String identifier = req.getIdentifier();
        
        // Cari berdasarkan Email, Username, atau Phone Number
        var user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new IllegalArgumentException("Invalid credentials");

        return new AuthResponse(jwtService.generateToken(user));
    }

    public MeResponse me() {
        return mapToMeResponse(getCurrentUser());
    }

    @Transactional
    public MeResponse updateAccount(UpdateAccountRequest req) {
        User user = getCurrentUser();

        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getDisplayName() != null) user.setDisplayName(req.getDisplayName());
        if (req.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        
        // Tambah/Update Email atau Phone
        if (req.getEmail() != null) {
            if (!req.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
                throw new IllegalArgumentException("Email already used");
            }
            user.setEmail(req.getEmail());
        }
        
        if (req.getPhoneNumber() != null) {
            if (!req.getPhoneNumber().equals(user.getPhoneNumber()) && userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already used");
            }
            user.setPhoneNumber(req.getPhoneNumber());
        }

        userRepository.save(user);
        return mapToMeResponse(user);
    }

    @Transactional
    public void deleteAccount() {
        User user = getCurrentUser();
        userRepository.delete(user);
        SecurityContextHolder.clearContext();
    }

    // Helper untuk mengambil user yang sedang login
    private User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser principal))
            throw new IllegalStateException("Unauthenticated");
        
        // Re-fetch dari DB untuk memastikan data terbaru (Penting untuk Hibernate session)
        return userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    // Helper mapping ke response DTO
    private MeResponse mapToMeResponse(User u) {
        return new MeResponse(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(), u.getPhoneNumber(), u.getRole());
    }
}