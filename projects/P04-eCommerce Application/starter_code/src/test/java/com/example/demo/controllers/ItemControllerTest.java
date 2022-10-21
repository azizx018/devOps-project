package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);
    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    private Item getItem() {
        Item item  = new Item();
        item.setName("hammer");
        item.setId(1L);
        item.setPrice(new BigDecimal(12));
        return item;
    }
    private List<Item> getListedItems(){
        Item item = getItem();
        List<Item> listedItems = new ArrayList<>();
        listedItems.add(item);
        return listedItems;
    }

    @Test
    public void get_items_happy_path(){
        List<Item> listedItems = getListedItems();
        when(itemRepository.findAll()).thenReturn(listedItems);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(listedItems, response.getBody());
    }
    @Test
    public void get_Item_By_id(){
        Item item = getItem();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(item.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item.getId(), response.getBody().getId());

    }
    @Test
    public void get_Items_By_Name_Happy_Path(){
        Item item = getItem();
        List<Item> listedItems = new ArrayList<>();
        listedItems.add(item);
        when(itemRepository.findByName(item.getName())).thenReturn(listedItems);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item.getName(), response.getBody().get(0).getName());
    }
    @Test
    public void get_Items_By_Name_Sad_Path(){
        Item item = getItem();
        List<Item> listedItems = new ArrayList<>();
        listedItems.add(item);
        when(itemRepository.findByName(item.getName())).thenReturn(listedItems);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Screw");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
