package arthur_ws.my_spring_project.security;

import arthur_ws.my_spring_project.entity.AuthorityEntity;
import arthur_ws.my_spring_project.entity.RoleEntity;
import arthur_ws.my_spring_project.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    UserEntity userEntity;
    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    /**
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       List<GrantedAuthority> authorities = new ArrayList<>();
       List<AuthorityEntity> authorityEntities = new ArrayList<>();

       // getting user roles
        Collection<RoleEntity> roles = userEntity.getRoles();

        if (roles == null) {
            return authorities;
        }

        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            authorityEntities.addAll(role.getAuthorities());
        });

        authorityEntities.forEach(authorityEntity -> {
            authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
        });
       return authorities;
    }

    /**
     * @return
     */
    @Override
    public String getPassword() {
        return this.userEntity.getEncryptedPassword();
    }

    /**
     * @return
     */
    @Override
    public String getUsername() {
        return this.userEntity.getEmail();
    }

    /**
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return this.userEntity.getEmailVerificationStatus();
    }
}
