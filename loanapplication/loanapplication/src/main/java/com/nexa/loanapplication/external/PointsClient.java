package com.nexa.loanapplication.external;

import com.nexa.loanapplication.config.ServicesProperties;
import com.nexa.loanapplication.dto.external.LoanPointDTO;
import com.nexa.loanapplication.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/** Calls GET /api/v1/loanpoints/loanpointid?<id> */
@Component
public class PointsClient {

    private static final String BASE = "/api/v1/loanpoints"; // Excel
    private final RestTemplate rest;
    private final ServicesProperties props;

    public PointsClient(RestTemplate rest, ServicesProperties props) {
        this.rest = rest; this.props = props;
    }

    public List<LoanPointDTO> listAll(){
        String url = props.getPoints().getBaseUrl() + BASE;
        var body = rest.getForObject(url, LoanPointDTO[].class);
        return body == null ? List.of() : List.of(body);
    }

    public LoanPointDTO getById(UUID loanPointId) {
        String url = props.getPoints().getBaseUrl()
                + "/api/v1/loanpoints/loanpointid?id=" + loanPointId;
        LoanPointDTO dto = rest.getForObject(url, LoanPointDTO.class);
        if (dto == null) throw new NotFoundException("Loan points rule not found: " + loanPointId);
        return dto;
    }
}
