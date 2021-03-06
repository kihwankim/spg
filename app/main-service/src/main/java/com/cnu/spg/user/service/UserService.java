package com.cnu.spg.user.service;

import com.cnu.spg.domain.login.Role;
import com.cnu.spg.domain.login.RoleName;
import com.cnu.spg.domain.login.User;
import com.cnu.spg.dto.user.UserPasswordChangingDto;
import com.cnu.spg.dto.user.UserRegisterDto;
import com.cnu.spg.exception.ResourceNotFoundException;
import com.cnu.spg.exception.UsernameAlreadyExistException;
import com.cnu.spg.repository.user.RoleRepository;
import com.cnu.spg.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public boolean save(User user) {
        if (this.userRepository.existsByUsername(user.getUsername())) {
            return false;
        }

        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        Role unAuthUserRole = this.roleRepository.findByName(RoleName.ROLE_UNAUTH)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleName.ROLE_UNAUTH));
        user.setRoles(Collections.singleton(unAuthUserRole));
        User savedUser = this.userRepository.save(user);

        return savedUser.getId() != null;
    }

    @Transactional
    public Long signUp(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByUsername(userRegisterDto.getUserName())) {
            throw new UsernameAlreadyExistException(userRegisterDto.getUserName());
        }

        String cryptPassword = bCryptPasswordEncoder.encode(userRegisterDto.getPassword());
        Role unauthRole = roleRepository.findByName(RoleName.ROLE_UNAUTH)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", RoleName.ROLE_UNAUTH));
        User user = User.createUser(userRegisterDto.getName(), userRegisterDto.getUserName(), cryptPassword, unauthRole);
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public User findByUserName(String userName) {
        return this.userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User", "user name", userName));
    }

    @Transactional
    public void deleteByUserName(String username) {
        this.userRepository.deleteByUsername(username);
    }

    @Transactional
    public User changeUserPassword(String username, UserPasswordChangingDto userPasswordChangingDto) {
        User oridinaryUser = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        boolean isUserPasswordCorrect = this.bCryptPasswordEncoder.matches(userPasswordChangingDto.getBeforePassword()
                , oridinaryUser.getPassword());
        if (!isUserPasswordCorrect) {
            return null;
        }
        oridinaryUser.setPassword(this.bCryptPasswordEncoder.encode(userPasswordChangingDto.getPassword()));

        return oridinaryUser;
    }

    public boolean checkNowPassword(String username, String passowrd) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return this.bCryptPasswordEncoder.matches(passowrd, user.getPassword());
    }

    @Transactional
    public User updateUsernameAndName(String pastUserName, String username, String name) {
        User user = this.userRepository.findByUsername(pastUserName)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        user.setUsername(username);
        user.setName(name);

        return user;
    }
}
