package com.nexa.loanapplication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public class ServicesProperties {
    public static class Endpoint {
        private String baseUrl;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }

    private Endpoint eligibility = new Endpoint();
    private Endpoint points = new Endpoint();
    private Endpoint loantypes = new Endpoint();
    private Endpoint audit = new Endpoint();
    private Endpoint repayment = new Endpoint();
    private Endpoint users = new Endpoint();

    public Endpoint getUsers(){ return users; }
    public void setUsers(Endpoint users){ this.users = users; }
    public Endpoint getRepayment(){ return repayment; }
    public Endpoint getEligibility() { return eligibility; }
    public Endpoint getPoints() { return points; }
    public Endpoint getLoantypes() { return loantypes; }
    public Endpoint getAudit() { return audit; }
}
