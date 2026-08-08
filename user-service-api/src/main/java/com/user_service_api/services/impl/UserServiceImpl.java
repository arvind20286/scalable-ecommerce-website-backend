package com.user_service_api.services.impl;

import java.util.List;

import com.user_service_api.models.dtos.UserIdDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.user_service_api.models.entities.User;
import com.user_service_api.repositories.UserRepository;
import com.user_service_api.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) throws Exception{
        try {
            return userRepository.findById(id).get();
        } catch (Exception e) {
            log.error("----------USER NOT FOUND----------", e);
            return null;
        }
        
    }

    @Override
    @Cacheable(value = "users", key = "#email", unless = "#result == null")
    public User getUserByEmail(String email) throws Exception {
        try {
            return userRepository.findByEmail(email).orElseThrow(
                    () -> new Exception("User not found")
            );
        }
        catch (Exception e) {
            log.error("----------USER NOT FOUND----------", e);
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() throws Exception{
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            log.error("-----------ERROR NOT USERS-----------", e);
            return null;
        }
        
    }

    @Override
    public UserIdDTO getUserIdJWT() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserIdDTO(user.getId());
    }


}
