package com.nexa.loanapplication.external;

import com.nexa.loanapplication.config.ServicesProperties;
import com.nexa.loanapplication.dto.external.UserDTO;
import com.nexa.loanapplication.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class UsersClient {
    private static final String BASE = "/api/users";

    private final RestTemplate rest;
    private final ServicesProperties props;

    public UsersClient(RestTemplate rest, ServicesProperties props) {
        this.rest = rest;
        this.props = props;
    }

    public UserDTO getById(UUID id) {
        String url = props.getUsers().getBaseUrl() + BASE + "/" + id;
        UserDTO dto = rest.getForObject(url, UserDTO.class);
        if (dto == null) throw new NotFoundException("User not found: " + id);
        return dto;
    }

    public boolean exists(UUID id) {
        try {
            return getById(id) != null;
        } catch (NotFoundException e) {
            return false;
        }
    }
}
