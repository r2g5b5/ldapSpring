package com.example.ldapspring;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("/updateUser")
    public String updateUser(@RequestBody LdapUser ldapUser) {
        ldapService.updateUser(ldapUser);
        return "success";
    }

    @GetMapping("/getAllUsers")
    public List<LdapUser> getAllUsers() {
        return ldapService.getAllUsers();
    }

    @GetMapping("/getUserById/{uid}")
    public LdapUser getUserById(@PathVariable String uid) {
        return ldapService.getUserById(uid);
    }

    @GetMapping("/getUserByUsername/{username}")
    public LdapUser getUserByUsername(@PathVariable String username) {
        return ldapService.getUserByUsername(username);
    }


    @DeleteMapping("/deleteUser/{uid}")
    public String deleteUser(@PathVariable String uid) {
        ldapService.deleteUser(uid);
        return "User Deleted";
    }

}