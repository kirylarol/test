package by.kirylarol.spendsculptor.dto;

import by.kirylarol.spendsculptor.entities.Identity;
import by.kirylarol.spendsculptor.entities.User;
import jakarta.validation.Valid;

public class FullIdentityDTO {
    @Valid
    private IdentityDTO identityDTO;
     @Valid
     private UserDTO userDTO;

    public FullIdentityDTO(IdentityDTO identityDTO, UserDTO userDTO) {
        this.identityDTO = identityDTO;
        this.userDTO = userDTO;
    }

    public FullIdentityDTO(User user, Identity identity) {
        this.identityDTO = new IdentityDTO(identity.getName(), identity.getSurname());
        this.userDTO = new UserDTO(user.getLogin(), user.getPassword());
    }

    public FullIdentityDTO() {
    }

    public IdentityDTO getIdentityDTO() {
        return identityDTO;
    }

    public void setIdentityDTO(IdentityDTO identityDTO) {
        this.identityDTO = identityDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
