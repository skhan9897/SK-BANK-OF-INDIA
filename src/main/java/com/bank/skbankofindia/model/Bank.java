package com.bank.skbankofindia.model;

public class Bank {
    private int id;
    private String bankCode;
    private String bankName;
    private String sector;
    private String headquarters;
    private String websiteUrl;
    private String logoUrl;
    private boolean active;

    public Bank() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getHeadquarters() { return headquarters; }
    public void setHeadquarters(String headquarters) { this.headquarters = headquarters; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
