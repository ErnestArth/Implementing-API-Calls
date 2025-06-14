package arthur_ws.my_spring_project.service.implementation;

import arthur_ws.my_spring_project.PasswordResetTokenRepository;
import arthur_ws.my_spring_project.UserRepository;
import arthur_ws.my_spring_project.entity.AddressEntity;
import arthur_ws.my_spring_project.entity.PasswordResetTokenEntity;
import arthur_ws.my_spring_project.entity.UserEntity;
import arthur_ws.my_spring_project.exceptions.UserServiceException;
import arthur_ws.my_spring_project.security.UserPrincipal;
import arthur_ws.my_spring_project.service.UserService;
import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import arthur_ws.my_spring_project.shared.dto.AmazonSES;
import arthur_ws.my_spring_project.shared.dto.UserDto;
import arthur_ws.my_spring_project.shared.dto.Utilities;
import arthur_ws.my_spring_project.ui.model.response.ErrorMessages;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utilities utilities;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserServiceException("User already exists");
        }

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utilities.generateAddressId(10));
            user.getAddresses().set(i, address);
        }

        ModelMapper modelMapper = new ModelMapper();
       UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utilities.generateUserId(50);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder. encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utilities.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        UserEntity savedUserDetails = userRepository.save(userEntity);

        UserDto returnUser = modelMapper.map(savedUserDetails, UserDto.class);

        //send an email message to user to verify their email address
        new AmazonSES().verifyEmail(returnUser);

        return returnUser;
    }


    /**
     * @param email
     * @return
     */
    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        UserDto returnUser = new UserDto();

        BeanUtils.copyProperties(userEntity, returnUser);
        return returnUser;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnUser = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User with ID: " + userId + "not found");
        }
        BeanUtils.copyProperties(userEntity, returnUser);
        return returnUser;
    }

    /**
     * @param userId
     * @param user
     * @return
     */
    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserDto returnUser = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
//        userEntity.setEmail(user.getEmail());

        UserEntity savedUserDetails = userRepository.save(userEntity);
        BeanUtils.copyProperties(savedUserDetails, returnUser);
        return returnUser;
    }

    /**
     */
    @Transactional
    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userRepository.delete(userEntity);
    }

    /**
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnUsers = new ArrayList<>();

        //not necessarily starting the page from 0
        if (page > 0) {
            page -= 1;
        }

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnUsers.add(userDto);
        }

        return returnUsers;
    }

    /**
     * @param token
     * @return
     */
    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnUser = false;
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = Utilities.hasTokenExpired(token);

            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnUser = true;
            }
        }
        return returnUser;
    }

    /**
     * @param email
     * @return
     */
    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnUser = false;
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            return returnUser;
        }

        String token = Utilities.generatePasswordResetToken(userEntity.getUserId());
        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        returnUser = new AmazonSES().sendPasswordResetRequest(
                userEntity.getFirstName(),
                userEntity.getEmail(), token
        );

        return returnUser;
    }

    /**
     * @param token
     * @param password
     * @return
     */
    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnUser =  false;

        if (Utilities.hasTokenExpired(token)) {
            return returnUser;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {
            return returnUser;
        }

        //prepare a new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        //update User password in database
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserDetails = userRepository.save(userEntity);

        //verify if the password was saved successfully
        if (savedUserDetails.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnUser = true;
        }

        //remove password reset token from database
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnUser;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new UserPrincipal(userEntity);

//        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true,
//                true, true, true, new ArrayList<>());


//        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(),
//                true, true, true, new ArrayList<>());

//        return new User(username, userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
