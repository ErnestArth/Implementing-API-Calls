package arthur_ws.my_spring_project.service;

import arthur_ws.my_spring_project.shared.dto.AddressDTO;
import arthur_ws.my_spring_project.ui.model.response.AddressesRest;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAddresses(String userId);
}
