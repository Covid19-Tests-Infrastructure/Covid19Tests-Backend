package de.drkhannover.tests.api.user.dto;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenDto implements Serializable {
	private static final long serialVersionUID = 6642753539861620823L;
	public TokenDto(String token, Date expiration) {
		super();
		this.token = token;
		String pattern = "MM/dd/yyyy HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		this.expiration = df.format(expiration);
	}
	String token;
	String expiration;
}
