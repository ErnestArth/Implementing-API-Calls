package arthur_ws.my_spring_project.ui.controller;


import arthur_ws.my_spring_project.service.AddressService;
import arthur_ws.my_spring_project.service.UserService;
import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import arthur_ws.my_spring_project.shared.dto.UserDto;
import arthur_ws.my_spring_project.ui.model.request.UserDetails;
import arthur_ws.my_spring_project.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public UserRest getUser(@PathVariable String id) {
        UserRest returnUser = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnUser);

        return returnUser;
    }


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public UserRest addUser(@RequestBody UserDetails userDetails) throws Exception {
        UserRest returnUser = new UserRest();

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto addedUser = userService.addUser(userDto);
        returnUser = modelMapper.map(addedUser, UserRest.class);

        return returnUser;
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public UserRest updateUser( @PathVariable String id, @RequestBody UserDetails userDetails) {
        UserRest returnUser = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnUser);

        return returnUser;
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnUser = new OperationStatusModel();

        returnUser.setOperationResult(RequestOperationName.DELETE.name());
        userService.deleteUser(id);

        returnUser.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnUser;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "30") int limit) {
        List<UserRest> returnUser = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            returnUser.add(userRest);
        }
        return returnUser;
    }

    //http:localhost:8080/my-spring-project/users/userId/addresses
    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public List<AddressesRest> getUserAddress(@PathVariable String id) {
       List<AddressesRest> returnUser = new ArrayList<>();

        List<AddressDTO> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            returnUser = new ModelMapper().map(addressesDto, listType);
        }

        return returnUser;
    }
}
