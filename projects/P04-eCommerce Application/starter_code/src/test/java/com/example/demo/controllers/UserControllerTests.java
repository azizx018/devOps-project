package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserControllerTests {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("testpassword");
        request.setUsername("test");
        request.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed",user.getPassword());

    }
    @Test
    public void create_user_sad_path() throws Exception {
        when(encoder.encode("testpassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("test");
        request.setUsername("test");
        request.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());


    }
    @Test
    public void find_user_by_name_happy_path(){
        User user = new User();
        user.setUsername("test1");
        user.setPassword("password1");
        when(userRepository.findByUsername("test1")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("test1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

    }
    @Test
    public void find_user_by_name_sad_path(){
        User user = new User();
        user.setUsername("test1");
        user.setPassword("password1");
        when(userRepository.findByUsername("test1")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("test12");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    public void find_By_id_happy_path(){
        User user = new User();
        Long id = 1L;
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
        final ResponseEntity<User> response = userController.findById(id);

        assertEquals(200,response.getStatusCodeValue());
        assertEquals(1L, user.getId());
    }
    @Test
    public void find_By_id_sad_path(){
        User user = new User();
        Long id = 1L;
        Long id2 = 2L;
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
        final ResponseEntity<User> response = userController.findById(id2);

        assertEquals(404,response.getStatusCodeValue());
        assertNotEquals(2L, user.getId());
    }
}
