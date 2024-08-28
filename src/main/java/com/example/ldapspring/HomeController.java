package com.example.ldapspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HomeController {

    @Autowired
    private LdapService ldapService;

    @PostMapping("/addUser")
    public String addUser(@RequestBody LdapUser ldapUser) {
        ldapService.addUser(ldapUser);
        return "success";
    }

    @GetMapping("/getUserDetails")
    public String getUserDetails(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // access user details
        String userName = userDetails.getUsername();
        boolean accNonExpired = userDetails.isAccountNonExpired();
        return "UserDetails: " + userName + "\n Account Non Expired: " + accNonExpired;
    }

    @GetMapping("/getAllUsers")
    public List<LdapUser> getAllUsers() {
        return ldapService.getAllUsers();
    }

    @GetMapping("/getUserById/{uid}")
    public String getUserById(@PathVariable String uid) {

        return ldapService.getUserById(uid);
    }

    @DeleteMapping("/deleteUser/{uid}")
    public String deleteUser(@PathVariable String uid) {
        ldapService.deleteUser(uid);
        return "User Deleted";
    }

}