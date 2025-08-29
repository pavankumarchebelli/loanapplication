package com.nexa.loanapplication.external;

import com.nexa.loanapplication.config.ServicesProperties;
import com.nexa.loanapplication.dto.external.EligibilityRuleDTO;
import com.nexa.loanapplication.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/** Calls GET /api/v1/loaneligibilityrules and /.../id?<loaneid> */
@Component
public class EligibilityClient {
    private final RestTemplate rest;
    private final ServicesProperties props;

    public EligibilityClient(RestTemplate rest, ServicesProperties props) {
        this.rest = rest; this.props = props;
    }

    public List<EligibilityRuleDTO> listAll(){
        String url = props.getEligibility().getBaseUrl()+"/api/v1/loaneligibilityrules";
        var body = rest.getForObject(url, EligibilityRuleDTO[].class);
        return body == null ? List.of() : List.of(body);
    }

    public EligibilityRuleDTO getById(UUID loanEid) {
        // Excel had a small typo ("eligibilty"); we standardize to "eligibility"
        String url = props.getEligibility().getBaseUrl()
                + "/api/v1/loaneligibilityrules/id?loaneid=" + loanEid;
        EligibilityRuleDTO dto = rest.getForObject(url, EligibilityRuleDTO.class);
        if (dto == null) throw new NotFoundException("Eligibility rule not found: " + loanEid);
        return dto;
    }
}
