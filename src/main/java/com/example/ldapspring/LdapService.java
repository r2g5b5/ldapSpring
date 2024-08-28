package com.example.ldapspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import java.util.List;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    private static final String BASE_DN="ou=users,ou=system";

    public void addUser(LdapUser user) {
        ldapTemplate.bind("uid="+user.getUsername()+","+BASE_DN,null,user.toAttributes());
    }

    public List<LdapUser> getAllUsers() {
        return ldapTemplate.search(BASE_DN, "(objectclass=inetOrgPerson)",
                (AttributesMapper<LdapUser>) attributes -> {
                    LdapUser ldapuser = new LdapUser();

                    // Check for null before accessing attributes
                    if (attributes.get("cn") != null) {
                        ldapuser.setCn(attributes.get("cn").get().toString());
                    }

                    if (attributes.get("sn") != null) {
                        ldapuser.setSn(attributes.get("sn").get().toString());
                    }

                    if (attributes.get("uid") != null) {
                        ldapuser.setUsername(attributes.get("uid").get().toString());
                    }

                    if (attributes.get("userPassword") != null) {
                        ldapuser.setPassword(attributes.get("userPassword").get().toString());
                    }

                    return ldapuser;
                });
    }

    public String getUserById(String uid) {
        List<String> usernames = ldapTemplate.search(
                BASE_DN,
                "(uid=" + uid + ")",
                (AttributesMapper<String>) attrs -> {
                    try {
                        return (String) attrs.get("sn").get();
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        if (!usernames.isEmpty()) {
            return usernames.get(0);
        } else {
            return null; // User not found
        }
    }
    public void deleteUser(String uid) {
        Name userDn= LdapNameBuilder.newInstance(BASE_DN)
                .add("uid",uid)
                .build();
        ldapTemplate.unbind(userDn);
    }

}
