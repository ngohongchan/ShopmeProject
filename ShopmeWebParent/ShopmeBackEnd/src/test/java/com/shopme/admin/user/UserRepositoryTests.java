package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User user = new User("chan1@gmail.com", "chan123", "chan", "ngo");
		user.addRole(roleAdmin);
		
		User savedUser = userRepository.save(user);
		assertThat(savedUser.getId()).isGreaterThan(0);
	} 
	
	
	@Test
	public void testCreateNewUserWithTwoRole() {
		User user = new User("chan123456@gmail.com", "$2a$10$hcHD2s1x28Yq/uiu5tSuguITqfwBToz9NZLoHN3.Lp0PYH8yGVuFy", "ronaldo", "cr7");
		Role roleEditor = new Role(1);
		Role roleAssistant = new Role(2);
		
		user.addRole(roleEditor); 
		user.addRole(roleAssistant);
		
		User savedUser = userRepository.save(user);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = userRepository.findAll();
		listUsers.forEach(user -> System.out.print(user));
	}
	
	@Test
	public void testGetUserById() {
		User user = userRepository.findById(1).get();
		System.out.print(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetail() {
		User user = userRepository.findById(1).get();
		user.setEnabled(true);
		user.setEmail("channgo1@gmail.com");
		
		userRepository.save(user);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User user = userRepository.findById(4).get();
		Role roleEditor = new Role(3);
		Role roleSalePerson = new Role(4);
		
		user.getRoles().remove(roleEditor);
		user.addRole(roleSalePerson);
		
		userRepository.save(user);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 8;
		userRepository.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "channgo@gmail.com";
		User user = userRepository.getUserByEMail(email);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = userRepository.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisabledUser() {
		Integer id = 1;
		userRepository.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = userRepository.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "chan";
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = userRepository.findAll(keyword, pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}
}
