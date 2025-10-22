package com.srsoft.modorder25.service;

import com.srsoft.modorder25.entity.User;
import com.srsoft.modorder25.entity.Role;
import com.srsoft.modorder25.repository.UserRepository;
import com.srsoft.modorder25.repository.RoleRepository;
import com.srsoft.modorder25.dto.UserRequest;
import com.srsoft.modorder25.dto.UserResponse;
import com.srsoft.modorder25.exception.BusinessException;
import com.srsoft.modorder25.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {
   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;
   
   @Autowired
   private AuthenticationFacade authenticationFacade;

   public UserService(UserRepository userRepository,
                     RoleRepository roleRepository,
                     PasswordEncoder passwordEncoder) {
       this.userRepository = userRepository;
       this.roleRepository = roleRepository;
       this.passwordEncoder = passwordEncoder;
   }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUsername(username)
               .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

       List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
               .map(role -> new SimpleGrantedAuthority(role.getName()))
               .collect(Collectors.toList());

       return new org.springframework.security.core.userdetails.User(
               user.getUsername(),
               user.getPassword(),
               user.isActive(),
                          true,
               true,
               true,
               authorities
       );
   }

   public User getCurrentUser() {
       String username = authenticationFacade.getUsername();
       return userRepository.findByUsername(username)
               .orElseThrow(() -> new BusinessException("Utente corrente non trovato"));
   }

   public UserResponse createUser(UserRequest request) {
       if (userRepository.existsByUsername(request.getUsername())) {
           throw new BusinessException("Username già in uso");
       }

       User user = new User();
       user.setUsername(request.getUsername());
       user.setPassword(passwordEncoder.encode(request.getPassword()));
       user.setEmail(request.getEmail());
       user.setActive(true);
       user.setPermessi(1);
       Role userRole = roleRepository.findByName("ROLE_USER")
               .orElseThrow(() -> new BusinessException("Ruolo default non trovato"));
       user.setRoles(Set.of(userRole));

       return UserResponse.fromEntity(userRepository.save(user));
   }

   @Transactional(readOnly = true)
   public Page<UserResponse> getAllUsers(Pageable pageable) {
       return userRepository.findAll(pageable)
               .map(UserResponse::fromEntity);
   }

   @Transactional(readOnly = true)
   public UserResponse getUserById(Long id) {
       User user = userRepository.findById(id)
               .orElseThrow(() -> new BusinessException("Utente non trovato"));
       return UserResponse.fromEntity(user);
   }

   public UserResponse updateUser(Long id, UserRequest request) {
       User user = userRepository.findById(id)
               .orElseThrow(() -> new BusinessException("Utente non trovato"));

       if (request.getEmail() != null) {
           user.setEmail(request.getEmail());
       }
       if (request.getPassword() != null) {
           user.setPassword(passwordEncoder.encode(request.getPassword()));
       }

       return UserResponse.fromEntity(userRepository.save(user));
   }

   public void deleteUser(Long id) {
       User user = userRepository.findById(id)
               .orElseThrow(() -> new BusinessException("Utente non trovato"));
       user.setActive(false);
       userRepository.save(user);
   }

   public void addRoleToUser(Long userId, String roleName) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new BusinessException("Utente non trovato"));
       Role role = roleRepository.findByName(roleName)
               .orElseThrow(() -> new BusinessException("Ruolo non trovato"));
       
       user.getRoles().add(role);
       userRepository.save(user);
   }

   public void changePassword(Long userId, String oldPassword, String newPassword) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new BusinessException("Utente non trovato"));

       if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
           throw new BusinessException("Password attuale non corretta");
       }

       user.setPassword(passwordEncoder.encode(newPassword));
       userRepository.save(user);
   }

   @Transactional(readOnly = true)
   public Set<String> getUserRoles(Long userId) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new BusinessException("Utente non trovato"));
       return user.getRoles().stream()
               .map(Role::getName)
               .collect(Collectors.toSet());
   }

   public boolean hasRole(String roleName) {
       User user = getCurrentUser();
       return user.getRoles().stream()
               .anyMatch(role -> role.getName().equals(roleName));
   }

   public User findById(Long userId) {
	   return userRepository.findById(userId)
	           .orElseThrow(() -> new BadCredentialsException("Utente con id " + userId + " non trovato"));
	}

   public User findByUsername(String username) {
       return userRepository.findByUsername(username)
               .orElseThrow(() -> new BadCredentialsException("Utente non trovato: " + username) );
       }
}