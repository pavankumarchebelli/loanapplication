package com.nexa.loanapplication.dto.external;

import java.math.BigDecimal;
import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private BigDecimal salary;  // <-- used for baseline check
    private Integer creditScore; // <-- used for baseline check
    private String status; // active/inactive, etc.

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
