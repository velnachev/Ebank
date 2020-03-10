package com.softuni.ebank.services;

import com.softuni.ebank.bindingModels.UserBindingModel;
import com.softuni.ebank.entities.Role;
import com.softuni.ebank.entities.User;
import com.softuni.ebank.repositories.RoleRepository;
import com.softuni.ebank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    public boolean registerUser(UserBindingModel userBindingModel) {
        User user = this.userRepository.findByUsername(userBindingModel.getUsername());

        if (user != null) {
            return false;
        } else if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            return false;
        }

        Role role = this.roleRepository.findByAuthority("USER");
        if (role == null) {
            return false;
        }

        user = new User();
        user.setUsername(userBindingModel.getUsername());
        user.setEmail(userBindingModel.getEmail());

        user.setPassword(bCryptPasswordEncoder.encode(userBindingModel.getPassword()));
        user.getAuthorities().add(role);

        this.userRepository.save(user);

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = this.userRepository.findByUsername(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException("Invalid user!");
        }

        return userDetails;
    }
}
