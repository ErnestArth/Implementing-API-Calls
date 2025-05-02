package arthur_ws.my_spring_project.service;

import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAddresses(String userId);
    AddressDTO getAddress(@PathVariable String addressId);
}
