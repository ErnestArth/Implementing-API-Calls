package arthur_ws.my_spring_project;

import arthur_ws.my_spring_project.entity.AddressEntity;
import arthur_ws.my_spring_project.entity.UserEntity;
import arthur_ws.my_spring_project.exceptions.UserServiceException;
import arthur_ws.my_spring_project.service.implementation.UserServiceImplementation;
import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import arthur_ws.my_spring_project.shared.dto.UserDto;
import arthur_ws.my_spring_project.shared.dto.Utilities;
import arthur_ws.my_spring_project.ui.model.response.AddressesRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceImplementationTest {

    @InjectMocks
    UserServiceImplementation userServiceImplementation;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    Utilities utilities;

    String userId = "beew7ew";
    String encryptedPassword = "bdjkdds";

    UserEntity userEntity;

    @BeforeEach
    void SetUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setFirstName("Asamoah");
        userEntity.setLastName("Gyan");
        userEntity.setUserId(userId);
        userEntity.setEmail("danojo72@gmail.com");
        userEntity.setEmailVerificationToken("bdudhvdvhd");
        userEntity.setAddresses(getAddressesEntity());
    }


    @Test
    final void testGetUser() {

       when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userServiceImplementation.getUser("danojo72@gmail.com");

        assertNotNull(userDto);
        assertEquals("Asamoah", userDto.getFirstName());
    }



    @Test
    final void testGetUser_UserNameNotFoundException() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                ()-> {
            userServiceImplementation.getUser("danojo72@gmail.com");
                });
    }


    @Test
    final void testAddUser_UserServiceException() {

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDTO());
        userDto.setEmail("danojo72@gmail.com");
        userDto.setFirstName("Asamoah");
        userDto.setLastName("Gyan");
        userDto.setPassword("18383");

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        assertThrows(UserServiceException.class,
                ()-> {
            userServiceImplementation.addUser(userDto);
                });
    }


    @Test
    final void testAddUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utilities.generateAddressId(anyInt())).thenReturn("hjeu7w7c");
        when(utilities.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder. encode(anyString())).thenReturn(encryptedPassword);
        when( userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDTO());
        userDto.setEmail("danojo72@gmail.com");
        userDto.setFirstName("Asamoah");
        userDto.setLastName("Gyan");
        userDto.setPassword("18383");

        UserDto savedUserDetails =  userServiceImplementation.addUser(userDto);
        assertNotNull(savedUserDetails);
        assertEquals(userEntity.getFirstName(), savedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), savedUserDetails.getLastName());
        assertNotNull(savedUserDetails.getUserId());
        assertEquals(savedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utilities, times(savedUserDetails.getAddresses().size())).generateAddressId(10);
        verify(bCryptPasswordEncoder, times(1)).encode("18383");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    private List<AddressDTO> getAddressesDTO() {
        AddressDTO shippingAddressDTO = new AddressDTO();
        shippingAddressDTO.setType("shipping");
        shippingAddressDTO.setCity("Tema");
        shippingAddressDTO.setStreetName("73 bdsh 7wqh");
        shippingAddressDTO.setCountry("Ghana");
        shippingAddressDTO.setPostalCode("Abs uwe");

        AddressDTO billingAddressDTO = new AddressDTO();
        billingAddressDTO.setType("billing");
        billingAddressDTO.setCity("Tema");
        billingAddressDTO.setStreetName("73 bdsh 7wqh");
        billingAddressDTO.setCountry("Ghana");
        billingAddressDTO.setPostalCode("Abs uwe");

        List<AddressDTO> addresses = new ArrayList<>();
        addresses.add(shippingAddressDTO);
        addresses.add(billingAddressDTO);

        return  addresses;
    }

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDTO> addresses = getAddressesDTO();

        Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
        return new ModelMapper().map(addresses, listType);
    }
}
