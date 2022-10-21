package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class CartControllerTests {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }
    @Test
    public void add_item_to_cart_happy_path(){
        User user = getUser();
        Item item  = getItem();
        Cart cart = new Cart();
        user.setCart(cart);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        when(cartRepository.save(cart)).thenReturn(cart);
        final ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cartRecord = response.getBody();
        assertNotNull(cartRecord);
        assertEquals(1, cart.getItems().size());

    }
    @Test
    public void add_item_to_cart_sad_path(){
        User user = getUser();
        Item item1  = getItem();
        Cart cart = new Cart();

        user.setCart(cart);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(2);
        request.setQuantity(1);

        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        when(cartRepository.save(cart)).thenReturn(cart);
        final ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private Item getItem() {
        Item item  = new Item();
        item.setName("hammer");
        item.setId(1L);
        item.setPrice(new BigDecimal(12));
        return item;
    }
    private User getUser(){
        User user = new User();
        user.setUsername("Spok1");
        return user;
    }

    @Test
    public void remove_item_from_cart_happy_path(){
        User user = getUser();
        Cart cart = new Cart();
        Item item = getItem();

        cart.addItem(item);
        user.setCart(cart);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartRepository.save(cart)).thenReturn(cart);

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(0,cart.getItems().size());

    }
    @Test
    public void remove_item_from_cart_sad_path(){
        User user = getUser();
        Cart cart = new Cart();
        Item item = getItem();

        cart.addItem(item);
        user.setCart(cart);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(item.getId());
        request.setQuantity(1);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartRepository.save(cart)).thenReturn(cart);

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(0,cart.getItems().size());

    }
}
