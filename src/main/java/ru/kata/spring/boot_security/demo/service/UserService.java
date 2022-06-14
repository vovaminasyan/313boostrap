package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
//import org.springframework.security.core.userdetails.User;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final RoleDao roleDao;
    private final UserDao userDao;

    @Autowired
    public UserService(RoleDao roleDao, UserDao userDao) {
        this.roleDao = roleDao;
        this.userDao = userDao;
    }

    public boolean addRole(Role role) {
        Role userBas = roleDao.findByName(role.getRole());
        if(userBas != null) {return false;}
        roleDao.add(role);
        return true;
    }

    public Role findByNameRole(String name) { return roleDao.findByName(name); }

    public List<Role> listRoles() { return roleDao.listRoles(); }

    public Role findByIdRole(Long id) {
        return roleDao.findByIdRole(id);
    }

    public List<Role> listByRole(List<String> name) {
        return roleDao.listByName(name);
    }

    public boolean add(User user) {
        User userBas = userDao.findByName(user.getUsername());
        if(userBas != null) {return false;}
        userDao.add(user);
        return true;
    }

    public List<User> listUsers() {
        return userDao.listUsers();
    }

    public void delete(Long id) {
        userDao.delete(id);
    }

    public void update(Long id, String userName, String password, String email, List<Role> roles) {
        userDao.update(id, userName, password, email, roles);
    }

    public User findById(Long id) {
        return userDao.findById(id);
    }

    public User findByUsername(String userName) {
        return userDao.findByName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userBas = findByUsername(username);
        if (userBas == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        UserDetails user = new org.springframework.security.core.userdetails.User(userBas.getUsername(), userBas.getPassword(), aug(userBas.getRoles()));
        return userBas;
    }

    private Collection<? extends GrantedAuthority> aug(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getRole()))
                .collect(Collectors.toList());
    }
}
