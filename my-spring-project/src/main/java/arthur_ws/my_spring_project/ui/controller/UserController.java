package arthur_ws.my_spring_project.ui.controller;


import arthur_ws.my_spring_project.service.AddressService;
import arthur_ws.my_spring_project.service.UserService;
import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import arthur_ws.my_spring_project.shared.dto.UserDto;
import arthur_ws.my_spring_project.ui.model.request.PasswordResetModel;
import arthur_ws.my_spring_project.ui.model.request.PasswordResetRequestModel;
import arthur_ws.my_spring_project.ui.model.request.UserDetails;
import arthur_ws.my_spring_project.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressesService;

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

    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
       List<AddressesRest> returnUser = new ArrayList<>();

        List<AddressDTO> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnUser = new ModelMapper().map(addressesDto, listType);

            for (AddressesRest addressesRest : returnUser) {
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserAddress(id, addressesRest.getAddressId())).withSelfRel();
                addressesRest.add(selfLink);
            }
        }

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(id).withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                        .methodOn(UserController.class).getUserAddresses(id))
                .withSelfRel();

        return CollectionModel.of(returnUser, userLink, selfLink);
    }

    //getting an address of the addresses
    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDTO addressDTO = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
       AddressesRest returnUser = modelMapper.map(addressDTO, AddressesRest.class);

       //adding links. Representational model
        //http://localhost:8080/users/userId
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");

        //http://localhost:8080/users/userId/addresses
        Link userAddressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");

        //http://localhost:8080/users/userId/addresses/addressId
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();
        return EntityModel.of(returnUser, userLink, userAddressLink, selfLink);
    }

    // email verification endpoint
    //http://localhost:8080/my-spring-project/users/email-verification
    @PostMapping(path = "/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public OperationStatusModel verifyEmailToken(@RequestBody String token) {

        System.out.println("Received token: " + token);
        OperationStatusModel returnUser = new OperationStatusModel();
        returnUser.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnUser.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        else {
            returnUser.setOperationResult(RequestOperationStatus.ERROR.name());
        }


        return returnUser;
    }


    //http://localhost:8080/my-spring-project/users/password-reset-request
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnUser = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnUser.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnUser.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnUser.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return returnUser;
    }

    //http://localhost:8080/my-spring-project/users/password-reset
    @PostMapping(path = "/password-reset", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnUser = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(passwordResetModel.getToken(), passwordResetModel.getPassword());

        returnUser.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnUser.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnUser.setOperationResult(RequestOperationStatus.SUCCESS.name());

        }
        return returnUser;
    }
}

