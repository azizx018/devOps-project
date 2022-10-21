package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }
    private Item getItem() {
        Item item  = new Item();
        item.setName("hammer");
        item.setId(1L);
        item.setPrice(new BigDecimal(12));
        return item;
    }
    private Cart getCart(){
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(getUser());
        cart.setTotal(new BigDecimal(12));
        List<Item> itemsList = new ArrayList<>();
        itemsList.add(getItem());
        cart.setItems(itemsList);
        return cart;
    }
    private User getUser(){
        User user = new User();
        Cart cart = new Cart();
        user.setId(1L);
        user.setUsername("Spok1");
        user.setCart(cart);
        return user;
    }


    @Test
    public void submit_Order_Happy_Path(){
        User user = getUser();
        user.setCart(getCart());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        UserOrder order = UserOrder.createFromCart(user.getCart());
        when(orderRepository.save(order)).thenReturn(order);

        final ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(order.getItems(),response.getBody().getItems());

    }
    @Test
    public void submit_Order_Sad_Path(){
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("Carl!");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    public void get_Orders_For_User_Happy_Path(){
        User user = getUser();
        user.setCart(getCart());
        UserOrder order = UserOrder.createFromCart(user.getCart());
        List<UserOrder> ordersList = new ArrayList<>();
        ordersList.add(order);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(ordersList);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ordersList.size(), response.getBody().size());


    }
    @Test
    public void get_Orders_For_User_Sad_Path(){
        User user = getUser();
        user.setCart(getCart());
        UserOrder order = UserOrder.createFromCart(user.getCart());
        List<UserOrder> ordersList = new ArrayList<>();
        ordersList.add(order);
        when(userRepository.findByUsername("Carl1")).thenReturn(user);
        when(orderRepository.findByUser(order.getUser())).thenReturn(ordersList);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }
}
