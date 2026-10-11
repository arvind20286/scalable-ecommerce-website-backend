package com.user_service_api.services.impl;

import com.user_service_api.models.entities.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.user_service_api.models.dtos.AuthDTO;
import com.user_service_api.models.dtos.LoginDTO;
import com.user_service_api.models.dtos.RegisterDTO;
import com.user_service_api.repositories.UserRepository;
import com.user_service_api.services.AuthService;
import com.user_service_api.utils.JwtUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserServiceImpl userService;

    @Override
    public AuthDTO login(LoginDTO login) throws Exception {
        try {
            authenticate(login.getEmail(), login.getPassword());

            Users users = userService.getUserByEmail(login.getEmail());
            String token = jwtUtil.generateToken(users);
            return new AuthDTO(token);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            System.out.println(e.getMessage());
            throw new BadCredentialsException("Incorrect username or password");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public AuthDTO register(RegisterDTO register) throws Exception {
        try {
            Users users = createUserFromRegistration(register);
            users = userRepository.save(users);

            String token = jwtUtil.generateToken(users);
            return new AuthDTO(token);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private Users createUserFromRegistration(RegisterDTO register) {
        Users users = new Users();
        users.setName(register.getName());
        users.setLastName(register.getLastName());
        users.setEmail(register.getEmail());
        users.setPassword(passwordEncoder.encode(register.getPassword()));
        users.setRole(register.getRole());
        users.setAddress(register.getAddress());
        users.setPhone(register.getPhone());

        return users;
    }
}
