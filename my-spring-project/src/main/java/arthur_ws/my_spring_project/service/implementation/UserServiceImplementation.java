package arthur_ws.my_spring_project.service.implementation;

import arthur_ws.my_spring_project.UserRepository;
import arthur_ws.my_spring_project.entity.AddressEntity;
import arthur_ws.my_spring_project.entity.UserEntity;
import arthur_ws.my_spring_project.exceptions.UserServiceException;
import arthur_ws.my_spring_project.service.UserService;
import arthur_ws.my_spring_project.shared.dto.AddressDTO;
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

    @Override
    public UserDto addUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("User already exists");
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
        userEntity.setEmailVerificationStatus(false);

        UserEntity savedUserDetails = userRepository.save(userEntity);

        UserDto returnUser = modelMapper.map(savedUserDetails, UserDto.class);

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


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) throw new UsernameNotFoundException(username);

        return new User(username, userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
