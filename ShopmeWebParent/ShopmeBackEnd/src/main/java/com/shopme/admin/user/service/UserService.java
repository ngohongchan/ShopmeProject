package com.shopme.admin.user.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import com.shopme.admin.user.exceprion.UserNotFoundException;
import com.shopme.admin.user.repository.RoleRepository;
import com.shopme.admin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	public static int USERS_PER_PAGE = 4;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User getByEmail(String email) {
		return userRepository.getUserByEMail(email);
	}

	public List<User> listAll() {
		return (List<User>) userRepository.findAll();
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return userRepository.findAll(keyword, pageable);
		}
		
		return userRepository.findAll(pageable);
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		
		if(isUpdatingUser) {
			User existingUser = userRepository.findById(user.getId()).get();
			
			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());	
			} else {
				encodePassword(user);
			}
		} else {
			encodePassword(user);
		}
		
		return userRepository.save(user);
	}
	
	public User updateAccount(User userInForm) {
		User userInDB = userRepository.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}
		
		if(userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}
		
		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		userInDB.setRoles(userInForm.getRoles());
		
		return userRepository.save(userInDB);
	}
	
	private void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User userByEMail = userRepository.getUserByEMail(email);
		
		if(userByEMail == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew && userByEMail != null) {
			return false;
		} else {
			if(userByEMail.getId() != id) {
				return false;
			}
		}
		
		return true;	
	}
	
	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} catch(NoSuchElementException ex) {
			throw new UserNotFoundException("Could not findd any user with ID " + id);
		}
		
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepository.countById(id);
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with Id " + id);
		}
		
		userRepository.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepository.updateEnabledStatus(id, enabled);
	}
}