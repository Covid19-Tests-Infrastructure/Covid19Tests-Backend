package de.drkhannover.tests.api.user.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "user_configuration")
public class PersonalSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int configurationId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private User user; 
    
    @Column
    private String facility;
    
    public String lastname;
    public String firstlame;
    public String phoneNumber;
    public String fax;
    public String email;
    public String addressStreet;
    public String addressZip;
    public String addressOrt;
    public String addressHnumber;
    public String lanr;
    public String bsnr;

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}
    
}
