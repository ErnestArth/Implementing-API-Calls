package arthur_ws.my_spring_project.service.implementation;

import arthur_ws.my_spring_project.AddressRepository;
import arthur_ws.my_spring_project.UserRepository;
import arthur_ws.my_spring_project.entity.AddressEntity;
import arthur_ws.my_spring_project.entity.UserEntity;
import arthur_ws.my_spring_project.service.AddressService;
import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImplementation implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> returnUser = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            return returnUser;
        }

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity : addresses) {
            returnUser.add(modelMapper.map(addressEntity, AddressDTO.class));
        }

        return returnUser;
    }
}
