package de.drkhannover.tests.api.user.jpa;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.drkhannover.tests.api.form.dto.AddressDto;
import de.drkhannover.tests.api.form.dto.FormDto;
import de.drkhannover.tests.api.user.UserRole;
import de.drkhannover.tests.api.user.dto.SettingsDto;
import de.drkhannover.tests.api.user.dto.UserDto;

@Entity
@Table(name = "users")
@ParametersAreNonnullByDefault
public class User implements UserDetails, GrantedAuthority {
	
	@Transient
	private static final long serialVersionUID = 1247927619185438543L;

	@Transient
	public static final String DEFAULT_ALGO = "BCRYPT";
	
	@Transient
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Nullable
    @Transient
    private PasswordEncoder encoder;
    
	/**
	 * Creates a new user in the {@link StoredUserDetails#DEFAULT_REALM} and encodes
	 * the password. <br>
	 * This type should only be used for users which aren't use any other
	 * authentication methods (realms) than a {@link UserDetailsService}.
	 * 
	 * @param userName    used name of the user (unique per realm!)
	 * @param rawPassword not encrypted password which will be encrypted with bcrypt
	 * @param isActive    decide if the user can log in or not
	 * @return new instance of StoredUserDetails.
	 */
	public static @Nonnull User createDefaultUser(String userName, String rawPassword) {
		var newUser = new User(userName, null, true, UserRole.DEFAULT);
		newUser.encodeAndSetPassword(rawPassword);
		return newUser;
	}
	@SuppressWarnings("unused") // spring
	private User() {}

	/**
	 * Unique identifier (primary key) for local user.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected int id;

	@Nonnull
	@Column(unique = true, nullable = false, length = 50)
	protected String username;

	@Column
	protected boolean isActive;

	@OneToOne(cascade = { CascadeType.ALL })
	@Nullable
	protected Password passwordEntity;

	@Nonnull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	protected UserRole role;

	@OneToOne(cascade = { CascadeType.ALL }, targetEntity = de.drkhannover.tests.api.user.jpa.PersonalSettings.class)
	protected PersonalSettings profileConfiguration;

	public User(String username, @Nullable Password passwordEntity, boolean isActive, UserRole role) {
		this.username = username;
		this.passwordEntity = passwordEntity;
		this.isActive = isActive;
		this.role = role;
		this.profileConfiguration = new PersonalSettings();
	}

	public User(final User user) {
		this.id = user.id;
		this.role = user.role;
		this.username = user.username;
		this.isActive = user.isActive;
		this.passwordEntity = user.passwordEntity;
		this.profileConfiguration = user.profileConfiguration;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Overrides the old username - it have to pe unique per realm and max length
	 * 50.
	 * 
	 * @param userName unique string per realm
	 */
	public void setUsername(String username) {
		if (username.length() > 50) {
			throw new IllegalArgumentException("The max length for a username is 50");
		}
		this.username = username;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Nullable
	public Password getPasswordEntity() {
		return passwordEntity;
	}

	public void setPasswordEntity(@Nullable Password password) {
		this.passwordEntity = password;
	}

	/**
	 * Single authority role of the user. Only one role at a time is supported.
	 * 
	 * @return name of a role
	 */
	public UserRole getRole() {
		return role;
	}

	/**
	 * Sets a single authority role to the user. Old role will be overridden.
	 * 
	 * @param role name of a role
	 */
	public void setRole(UserRole role) {
		this.role = role;
	}

	/**
	 * {@link PersonalSettings} of the user where extra settings are stored like
	 * email addressees.
	 * 
	 * @return associated profile of this StoredUser - if no exists, a new will be
	 *         generated
	 */
	@Nonnull
	public PersonalSettings getProfileConfiguration() {
		final var profileConfiguration2 = profileConfiguration;
		if (profileConfiguration2 != null) {
			return profileConfiguration2;
		} else {
			final var conf = new PersonalSettings();
			profileConfiguration = conf;
			return conf;
		}
	}

	public void setProfileConfiguration(@Nullable PersonalSettings profileConfiguration) {
		this.profileConfiguration = profileConfiguration;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(this);
	}

	@Override
	public String getPassword() {
		return passwordEntity.getPasswordString();
	}

	/**
	 * Is unique and is never null or empty. It can be used as identifier.
	 * 
	 * @return name of the user which is unique per realm
	 */
	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return isActive;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isActive;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isActive;
	}

	@Override
	public boolean isEnabled() {
		return isActive;
	}

	@Override
	public String getAuthority() {
		return role.name();
	}

	/**
	 * Encodes a raw string to bcrypt hash sets the password entity.
	 * 
	 * @param rawPassword
	 * @return the hashed value - never null but may be empty
	 */
	public String encodeAndSetPassword(String rawPassword) {
		var passwordEntityLocal = passwordEntity;
		if (passwordEntityLocal != null) {
			@Nonnull final String encodedPass = encode(rawPassword);
			passwordEntityLocal.setPasswordString(encodedPass);
			passwordEntityLocal.setHashAlgorithm(DEFAULT_ALGO);
		} else {
			@Nonnull final String encodedPass = encode(rawPassword);
			passwordEntityLocal = new Password(encodedPass, DEFAULT_ALGO);
		}
		setPasswordEntity(passwordEntityLocal);
		return getPassword();
	}

	private @Nonnull String encode(String rawPassword) {
		final String encodedPass = getEncoder().encode(rawPassword);
		if (encodedPass == null) {
			throw new RuntimeException("BCryptPasswordEncoder returned null as return value - this should not happen");
		}
		return encodedPass;
	}

    /**
     * Returns springs default {@link BCryptPasswordEncoder}. 
     * @return default instance of {@link BCryptPasswordEncoder}
     */
    public @Nonnull PasswordEncoder getEncoder() {
        PasswordEncoder encoder2 = encoder;
        if (encoder2 != null) {
            return encoder2;
        } else {
            encoder = new BCryptPasswordEncoder();
            return getEncoder();
        }
    }
    
    public UserDto asDto() {
    	return UserDto.transformToDto(this);
    }
}
