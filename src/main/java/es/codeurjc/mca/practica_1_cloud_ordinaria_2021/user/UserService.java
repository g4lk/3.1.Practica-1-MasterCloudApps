package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserComponent userComponent;


    public Collection<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }

    public Optional<User> createUser(User user, String... role){
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Optional.ofNullable(null);
        } else if (userRepository.findByName(user.getName()).isPresent()) {
            return Optional.ofNullable(null);
        } else {
            final String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
            user.setPassword(encryptedPassword);
            user.setRoles(Arrays.asList(role));
            userRepository.save(user);
            return Optional.of(user);
        }
    }

    public boolean deleteUser(long id) {
        Optional<User> toDelete = userRepository.findById(id);
        if(toDelete.isPresent()){
            userRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }

    public User getMe(){
        // Why get another time my User?
        // -> Collection like events will not load on sesion components (lazy loading)
        // -> Could solve with @Fetch, but create problems at remove Events
        User me = userComponent.getLoggedUser();
        return userRepository.getOne(me.getId());
    }

    public boolean getIfIAdmin() {
        return userComponent.getLoggedUser().getRoles().contains(User.ROLE_ADMIN);
    }
    
}
