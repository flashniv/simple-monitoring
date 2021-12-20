package ua.com.serverhelp.simplemonitoring.utils.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.serverhelp.simplemonitoring.entities.account.SimpleUserDetails;
import ua.com.serverhelp.simplemonitoring.entities.account.User;
import ua.com.serverhelp.simplemonitoring.storage.db.UserRepository;

@Service
public class SimpleUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user=userRepository.findByUsername(s);
        if(user==null){
            throw new UsernameNotFoundException("SimpleUserDetailsService::loadUserByUsername User "+s+" not found");
        }
        return new SimpleUserDetails(user);
    }
}
