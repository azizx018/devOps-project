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
        User user = new User();
        Item item  = new Item();
        Cart cart = new Cart();
        String username = "Spok1";

        user.setUsername(username);
        Long id = 1l;
        item.setName("hammer");
        item.setId(id);
        item.setPrice(new BigDecimal(12));
        user.setCart(cart);
        when(userRepository.findByUsername(username)).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(id);
        request.setQuantity(1);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

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
        User user = new User();
        Item item1  = new Item();
        Cart cart = new Cart();
        String username = "Spok1";

        user.setUsername(username);
        Long id = 1l;
        item1.setName("hammer");
        item1.setId(id);
        item1.setPrice(new BigDecimal(12));
        user.setCart(cart);
        when(userRepository.findByUsername(username)).thenReturn(user);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(username);
        request.setItemId(2);
        request.setQuantity(1);

        when(itemRepository.findById(id)).thenReturn(Optional.of(item1));

        when(cartRepository.save(cart)).thenReturn(cart);
        final ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
