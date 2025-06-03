package arthur_ws.my_spring_project;

import arthur_ws.my_spring_project.entity.AuthorityEntity;
import arthur_ws.my_spring_project.entity.RoleEntity;
import arthur_ws.my_spring_project.entity.UserEntity;
import arthur_ws.my_spring_project.shared.dto.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class InitialUsersSetup {

//    @Autowired
//    AuthorityRepository authorityRepository;

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final Utilities utilities;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public InitialUsersSetup(AuthorityRepository authorityRepository, RoleRepository roleRepository, Utilities utilities, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.utilities = utilities;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("From application ready event...");

        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole("ROLE_USER", Arrays.asList(readAuthority, writeAuthority));
       RoleEntity roleAdmin = createRole("ROLE_ADMIN", Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

       if (roleAdmin == null) {
           return;
       }

        UserEntity adminUser = new UserEntity();
       adminUser.setFirstName("firstname");
       adminUser.setLastName("surname");
       adminUser.setEmail("admin@gmail.com");
       adminUser.setEmailVerificationStatus(true);
       adminUser.setUserId(utilities.generateUserId(50));
       adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("123456"));
       adminUser.setRoles(List.of(roleAdmin));

       userRepository.save(adminUser);
    }

    @Transactional
    protected AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }
        return authority;
    }

    @Transactional
    protected RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }
}
