package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.auth.AuthenticationRequest;
import com.app.emsx.dtos.auth.AuthenticationResponse;
import com.app.emsx.dtos.auth.RegisterRequest;
import com.app.emsx.entities.User;
import com.app.emsx.exceptions.BusinessRuleException;
import com.app.emsx.repositories.UserRepository;
import com.app.emsx.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl
 * -----------------------------------------------------
 * ✔ Gestiona registro y autenticación de usuarios
 * ✔ Genera tokens JWT válidos con roles incluidos
 * ✔ Retorna la respuesta de autenticación al frontend
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * ✅ Registrar un nuevo usuario
     * Solo crea el usuario, NO devuelve token (el usuario debe hacer login después)
     */
    public void register(RegisterRequest request) {
        // Validar longitud de contraseña
        if (request.getPassword() != null) {
            if (request.getPassword().length() < 6) {
                throw new BusinessRuleException("La contraseña debe tener mínimo 6 caracteres");
            }
            if (request.getPassword().length() > 30) {
                throw new BusinessRuleException("La contraseña debe tener máximo 30 caracteres");
            }
            // Validar que no empiece ni termine con espacio en blanco
            if (request.getPassword().startsWith(" ") || request.getPassword().endsWith(" ")) {
                throw new BusinessRuleException("La contraseña no puede empezar ni terminar con un espacio en blanco");
            }
        }
        
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El correo electrónico ya está registrado. Intenta con otro correo o inicia sesión.");
        }
        
        User user = new User();

        // Evitar errores de null en firstname / lastname
        user.setFirstname(
                request.getFirstname() != null && !request.getFirstname().isBlank()
                        ? request.getFirstname()
                        : "User"
        );
        user.setLastname(
                request.getLastname() != null && !request.getLastname().isBlank()
                        ? request.getLastname()
                        : "Default"
        );

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_ADMIN"); // Por defecto es administrador

        userRepository.save(user);
        // NO generamos token aquí - el usuario debe hacer login después
    }

    /**
     * ✅ Autenticar usuario existente
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Verificar primero si el usuario existe ANTES de intentar autenticar
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("El usuario no existe en el sistema. Verifica tu correo electrónico o crea una cuenta.");
        }
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            // Si llegamos aquí, el usuario existe pero la contraseña es incorrecta
            throw new org.springframework.security.authentication.BadCredentialsException("Credenciales no válidas. Verifica tu contraseña.");
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            // Usuario no encontrado (por si acaso)
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("El usuario no existe en el sistema. Verifica tu correo electrónico o crea una cuenta.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("El usuario no existe en el sistema. Verifica tu correo electrónico o crea una cuenta."));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();
    }
}
