package backend25.blogisovellus.web;

import org.springframework.stereotype.Service;

import backend25.blogisovellus.domain.AppUser;
import backend25.blogisovellus.domain.AppUserRepository;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserDetailsServiceBlogisovellus implements UserDetailsService {

    private final AppUserRepository repository;

    public UserDetailsServiceBlogisovellus(AppUserRepository appUserRepository) {
        this.repository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AppUser curruser = repository.findByUserName(userName).orElse(null);
        UserDetails user = new org.springframework.security.core.userdetails.User(userName, curruser.getPasswordHashed(), 
        		AuthorityUtils.createAuthorityList(curruser.getRole()));
        return user;
    }

}
