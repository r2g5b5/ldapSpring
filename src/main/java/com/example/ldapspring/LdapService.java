package com.example.ldapspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    private static final String BASE_DN = "ou=users,ou=system";

    public void addUser(LdapUser user) {
        LdapName dn = buildDn(user.getUsername());
        ldapTemplate.bind(dn, null, user.toAttributes());
    }

    public void updateUser(LdapUser user) {
        LdapName dn = buildDn(user.getUsername());
        ModificationItem[] mods = buildModificationItems(user);
        ldapTemplate.modifyAttributes(dn, mods);
    }

    public List<LdapUser> getAllUsers() {
        return ldapTemplate.search("", "(objectclass=inetOrgPerson)", mapToLdapUser());
    }

    public LdapUser getUserById(String uid) {
        return searchUser(BASE_DN, "(uid=" + uid + ")").get();
    }

    public LdapUser getUserByUsername(String username) {
        return searchUser(BASE_DN, "(cn=" + username + ")").get();
    }

    public void deleteUser(String uid) {
        Name userDn = buildDn(uid);
        ldapTemplate.unbind(userDn);
    }

    private LdapName buildDn(String uid) {
        return LdapNameBuilder.newInstance(BASE_DN)
                .add("uid", uid)
                .build();
    }

    private ModificationItem[] buildModificationItems(LdapUser user) {
        return Stream.of(
                        newModificationItem("cn", user.getCn()),
                        newModificationItem("sn", user.getSn()),
                        newModificationItem("userPassword", user.getPassword())
                ).filter(Objects::nonNull)
                .toArray(ModificationItem[]::new);
    }

    private ModificationItem newModificationItem(String attributeName, String value) {
        return value != null ? new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attributeName, value)) : null;
    }

    private AttributesMapper<LdapUser> mapToLdapUser() {
        return attributes -> {
            LdapUser user = new LdapUser();
            user.setCn(getAttributeValue(attributes, "cn"));
            user.setSn(getAttributeValue(attributes, "sn"));
            user.setUsername(getAttributeValue(attributes, "uid"));
            user.setPassword(getAttributeValue(attributes, "userPassword"));
            return user;
        };
    }

    private String getAttributeValue(Attributes attributes, String attributeName) {
        try {
            return attributes.get(attributeName) != null ? attributes.get(attributeName).get().toString() : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve attribute: " + attributeName, e);
        }
    }

    private Optional<LdapUser> searchUser(String baseDn, String filter) {
        List<LdapUser> results = ldapTemplate.search(baseDn, filter, mapToLdapUser());
        return results.stream().findFirst();
    }
}
