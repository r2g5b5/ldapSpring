package com.example.ldapspring;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public class LdapUser {
    private String cn;
    private String sn;
    private String password;
    private String username;

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Attributes toAttributes() {
        Attributes attributes = new BasicAttributes();

        // Add the objectClass attribute
        attributes.put("objectClass", "inetOrgPerson");

        // Add attributes if they are not null
        if (cn != null) {
            attributes.put("cn", cn);
        }

        if (sn != null) {
            attributes.put("sn", sn);
        }

        if (username != null) {
            attributes.put("uid", username);
        }

        if (password != null) {
            attributes.put("userPassword", password);
        }

        return attributes;
    }
}
