package model;

import java.util.Date;

public class License {
    private String licenseId;
    private Date expiration;

    public String getID() { return licenseId; }
    // getters/setters ...

    public String getLicenseId() { return licenseId.toString(); }
    public void setLicenseId(String licenseID) { this.licenseId = licenseID; }
    public Date getExpiration() { return expiration; }
    public void setExpiration(Date expiration) { this.expiration = expiration; }
}
