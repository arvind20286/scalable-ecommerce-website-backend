package com.user_service_api.services;

import java.util.List;

import com.user_service_api.models.dtos.UserIdDTO;
import com.user_service_api.models.entities.Users;

public interface UserService {

    Users getUserById(Long id) throws Exception;
    Users getUserByEmail(String email) throws Exception;
    List<Users> getAllUsers() throws Exception;

    UserIdDTO getUserIdJWT();
}
