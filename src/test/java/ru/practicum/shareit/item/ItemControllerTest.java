package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.storage.InMemoryStorage;
import ru.practicum.shareit.user.UserInMemoryServiceImpl;
import ru.practicum.shareit.user.dto.NewUserRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemControllerTest {
    @Autowired
    private ItemInMemoryServiceImpl itemService;

    @Autowired
    private InMemoryStorage inMemoryStorage;

    @Autowired
    private UserInMemoryServiceImpl userService;

    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private NewUserRequest newUserRequest;

    @BeforeEach
    void setUp() {
        inMemoryStorage.getUsers().clear();
        inMemoryStorage.getUsersItems().clear();
        itemService.dropIdCounter();
        userService.dropIdCounter();

        this.newItemRequest = new NewItemRequest();
        this.updateItemRequest = new UpdateItemRequest();
        this.newUserRequest = new NewUserRequest();

        newItemRequest.setName("Test Item");
        newItemRequest.setDescription("Test Description");
        newItemRequest.setAvailable(true);

        updateItemRequest.setName("Updated Item");
        updateItemRequest.setDescription("Updated Description");
        updateItemRequest.setAvailable(false);

        newUserRequest.setName("New User");
        newUserRequest.setEmail("test@ewmail.com");
        userService.addUser(newUserRequest);
    }


    @Test
    public void shellAddAndGetItem() {
        ItemDto itemDto = itemService.addItem(1L, newItemRequest);
        assertNotNull(itemDto);
        assertEquals(newItemRequest.getName(), itemDto.getName());
        assertEquals(newItemRequest.getDescription(), itemDto.getDescription());
        assertEquals(newItemRequest.getAvailable(), itemDto.getAvailable());

        assertEquals(1, inMemoryStorage.getUsersItems().size());

        Item item = inMemoryStorage.getUsersItems().get(1L).getFirst();
        assertEquals(newItemRequest.getName(), item.getName());
        assertEquals(newItemRequest.getDescription(), item.getDescription());
        assertEquals(newItemRequest.getAvailable(), item.getAvailable());
        assertEquals(1L, item.getOwner());
    }

    @Test
    public void shellNotAddItemIfUserUnregistered() {
        assertThrows(NotFoundException.class, () -> itemService.addItem(2L, newItemRequest));
    }

    @Test
    public void shellUpdateItem() {
        ItemDto itemDto = itemService.addItem(1L, newItemRequest);
        assertNotNull(itemDto);

        ItemDto updateItem = itemService.updateItem(1L, 1L, updateItemRequest);
        assertNotNull(updateItem);

        Item updatedItem = inMemoryStorage.getUsersItems().get(1L).getFirst();
        assertEquals(updateItemRequest.getName(), updatedItem.getName());
        assertEquals(updateItemRequest.getDescription(), updatedItem.getDescription());
        assertEquals(updateItemRequest.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    public void shellNotUpdateItemIfUserIdOrItemIdWrong() {
        ItemDto itemDto = itemService.addItem(1L, newItemRequest);
        assertNotNull(itemDto);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(2L, 1L, updateItemRequest));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 2L, updateItemRequest));
    }

    @Test
    public void shellReturnAllUsersItems() {
        NewItemRequest newItemRequest2 = new NewItemRequest();
        newItemRequest2.setName("New Item2");
        newItemRequest2.setDescription("New Description2");
        newItemRequest2.setAvailable(true);

        ItemDto itemDto1 = itemService.addItem(1L, newItemRequest2);
        assertNotNull(itemDto1);
        ItemDto itemDto2 = itemService.addItem(1L, newItemRequest2);
        assertNotNull(itemDto2);

        List<Item> userItems = inMemoryStorage.getUsersItems().get(1L);
        assertNotNull(userItems);
        assertEquals(2, userItems.size());

        assertEquals(itemDto1.getName(), userItems.get(0).getName());
        assertEquals(1L, userItems.get(0).getId());
        assertEquals(itemDto1.getDescription(), userItems.get(0).getDescription());
        assertEquals(itemDto1.getAvailable(), userItems.get(0).getAvailable());

        assertEquals(itemDto2.getName(), userItems.get(1).getName());
        assertEquals(2L, userItems.get(1).getId());
        assertEquals(itemDto2.getDescription(), userItems.get(1).getDescription());
        assertEquals(itemDto2.getAvailable(), userItems.get(1).getAvailable());
    }

    @Test
    public void shellRemoveAllUsersItemsIfUserDeleted() {
        NewItemRequest newItemRequest2 = new NewItemRequest();
        newItemRequest2.setName("New Item2");
        newItemRequest2.setDescription("New Description2");
        newItemRequest2.setAvailable(true);

        ItemDto itemDto1 = itemService.addItem(1L, newItemRequest2);
        assertNotNull(itemDto1);
        ItemDto itemDto2 = itemService.addItem(1L, newItemRequest2);
        assertNotNull(itemDto2);

        List<Item> userItems = inMemoryStorage.getUsersItems().get(1L);
        assertNotNull(userItems);
        assertEquals(2, userItems.size());


        userService.deleteUser(1L);
        assertEquals(0, inMemoryStorage.getUsersItems().size());
    }

    @Test
    public void shellReturnSearchedItemsByTextInNameOrDescriptionOnlyAvailableItems() {
        NewItemRequest newItemRequest2 = new NewItemRequest();
        newItemRequest2.setName("New Item");
        newItemRequest2.setDescription("New Description");
        newItemRequest2.setAvailable(true);

        //available = false
        NewItemRequest newItemRequest3 = new NewItemRequest();
        newItemRequest3.setName("Test Item");
        newItemRequest3.setDescription("Test Description");
        newItemRequest3.setAvailable(false);

        ItemDto itemDto1 = itemService.addItem(1L, newItemRequest);
        assertNotNull(itemDto1);
        ItemDto itemDto2 = itemService.addItem(1L, newItemRequest2);
        assertNotNull(itemDto2);
        ItemDto itemDto3 = itemService.addItem(1L, newItemRequest3);
        assertNotNull(itemDto3);

        System.out.println(inMemoryStorage.getUsersItems().get(1L));

        //searched by name
        List<ItemDto> nameSearchedItems = itemService.searchItemsByText("Test Item");
        assertNotNull(nameSearchedItems);
        assertEquals(1, nameSearchedItems.size());
        ItemDto searchedItem = nameSearchedItems.getFirst();
        assertEquals(itemDto1.getName(), searchedItem.getName());
        assertEquals(itemDto1.getDescription(), searchedItem.getDescription());
        assertEquals(itemDto1.getAvailable(), searchedItem.getAvailable());

        //searched by description
        List<ItemDto> descriptionSearchedItems = itemService.searchItemsByText("Test Description");
        assertNotNull(descriptionSearchedItems);
        assertEquals(1, descriptionSearchedItems.size());
        ItemDto descriptionSearchedItem = descriptionSearchedItems.getFirst();
        assertEquals(itemDto1.getName(), descriptionSearchedItem.getName());
        assertEquals(itemDto1.getDescription(), descriptionSearchedItem.getDescription());
        assertEquals(itemDto1.getAvailable(), descriptionSearchedItem.getAvailable());
    }


}