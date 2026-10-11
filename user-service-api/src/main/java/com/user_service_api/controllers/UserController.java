package com.user_service_api.controllers;

import java.util.List;

import com.user_service_api.models.dtos.UserIdDTO;
import com.user_service_api.models.entities.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user_service_api.services.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Users>> getAllUsers() {
        try {
            List<Users> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("ERROR", e);
            ;
            return null;
        }
    }
    

    @GetMapping("/client/user/{id}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        try {
            Users users = userService.getUserById(id);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("ERROR", e);
            return null;
        }
    }

    @GetMapping("/user-id")
    public ResponseEntity<UserIdDTO> getUserIdJWT(){
        try {
            UserIdDTO userIdDTO = userService.getUserIdJWT();
            return ResponseEntity.ok(userIdDTO);
        } catch (Exception e) {
            log.error("ERROR", e);
            return null;
        }
    }

    @GetMapping("/admin/verification")
    public boolean isAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        return "ADMIN".equalsIgnoreCase(userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @GetMapping("/seller/verification")
    public boolean isSeller(@AuthenticationPrincipal UserDetails userDetails){
        return "SELLER".equalsIgnoreCase(userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @GetMapping("/client/verification")
    public boolean isClient(@AuthenticationPrincipal UserDetails userDetails) {
        return "CLIENT".equalsIgnoreCase(userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @GetMapping("/user/verification")
    @PreAuthorize("hasAnyAuthority('CLIENT','ADMIN')")
    public boolean isUser(){
        return true;
    }
}
