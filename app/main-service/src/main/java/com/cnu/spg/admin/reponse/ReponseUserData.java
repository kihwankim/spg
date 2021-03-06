package com.cnu.spg.admin.reponse;

import com.cnu.spg.domain.login.Role;
import com.cnu.spg.domain.login.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReponseUserData {
    private Long id;
    private String username;
    private String name;
    private Set<Role> roles;

    public ReponseUserData(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.roles = user.getRoles();
    }
}
