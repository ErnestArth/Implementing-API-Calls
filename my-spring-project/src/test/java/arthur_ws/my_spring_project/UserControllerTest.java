package arthur_ws.my_spring_project;

import arthur_ws.my_spring_project.service.UserService;
import arthur_ws.my_spring_project.service.implementation.UserServiceImplementation;
import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import arthur_ws.my_spring_project.shared.dto.UserDto;
import arthur_ws.my_spring_project.ui.controller.UserController;
import arthur_ws.my_spring_project.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceImplementation userServiceImplementation;

    @Mock
    UserService userService;

    UserDto userDto;

    String userId = "jduusud";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDto = new UserDto();
        userDto.setFirstName("Asamoah");
        userDto.setLastName("Gyan");
        userDto.setEncryptedPassword("udhdsvd");
        userDto.setEmail("asamoah@gmail.com");
        userDto.setUserId(userId);
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setAddresses(getAddressesDTO());
    }

    @Test
    final void testGetUser() {

        when(userService.getUserByUserId(anyString())).thenReturn( userDto);

        UserRest userRest = userController.getUser(userId);

        assertNotNull(userRest);
        assertEquals(userId, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
//        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
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
}
