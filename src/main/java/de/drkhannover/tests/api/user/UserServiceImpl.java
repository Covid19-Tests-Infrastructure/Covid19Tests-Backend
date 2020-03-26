package de.drkhannover.tests.api.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.drkhannover.tests.api.user.jpa.User;

/**
 * @author Marcel
 */
@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository repository;

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public <T extends User> void storeUser(@Nonnull T user) {
		User stUser = new User((User) user);
		if (user.getUsername().isBlank()) {
			throw new IllegalArgumentException("User must be valid.");
		}
		repository.save(stUser);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public @Nonnull User findUserByUsername(@Nullable String username) throws UsernameNotFoundException {
		Optional<User> user = repository.findByusername(username);
		user.orElseThrow(() -> new UsernameNotFoundException("no user with this name found"));
		return user.get();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public @Nonnull User findUserById(int id) throws UsernameNotFoundException {
		Optional<User> user = repository.findById(id);
		user.orElseThrow(() -> new UsernameNotFoundException("Id was not found in database"));
		return user.map(User::new).get();
	}

	@Override
	public @Nonnull UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findUserByUsername(username);
	}

	/**
	 * {@inheritDoc}.
	 */
	public boolean isUserInDatabase(@Nullable User user) {
		if (user != null) {
			try {
				this.findUserById(user.getId());
				return true;
			} catch (UsernameNotFoundException e) {
				try {
					this.findUserByUsername(user.getUsername());
					return true;
				} catch (UsernameNotFoundException ex) {
				}
			}
		}
		return false;
	}

	@Override
	public List<User> findUsers() {
		var list = new ArrayList<User>();
		Iterable<User> optList = repository.findAll();
		for (User user : optList) {
			list.add(user);
		}
		return list;
	}
}
