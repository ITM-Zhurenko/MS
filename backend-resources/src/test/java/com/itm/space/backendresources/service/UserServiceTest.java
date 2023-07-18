package com.itm.space.backendresources.service;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest extends BaseIntegrationTest {
    @MockBean
    private Keycloak keycloakClient;
    @MockBean
    private RealmResource realmResource;
    @MockBean
    private UsersResource usersResource;
    @MockBean
    private UserResource userResource;
    @MockBean
    private Response response;
    private UserService userService;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    public void init() {
        when(keycloakClient.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

    @Test
    public void createUserTest() throws URISyntaxException {
        UserRequest userRequest = new UserRequest(
                "John228",
                "john@gmail.com",
                "firstName",
                "John",
                "Johnovich");
        when(response.getLocation()).thenReturn(new URI("user_id"));
        when(usersResource.create(any())).thenReturn(response);
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);
        userService.createUser(userRequest);
        verify(usersResource, atMostOnce()).create(any());
        verify(response, atLeastOnce()).getStatusInfo();
    }

    @Test
    public void getUserByIdTest() {
        UUID uuid = UUID.randomUUID();
        UserRepresentation user = new UserRepresentation();
        user.setEmail("user@gmail.com");

        when(usersResource.get(any())).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(user);
        when(userResource.roles()).thenReturn(mock(RoleMappingResource.class));
        when(userResource.roles().getAll()).thenReturn(mock(MappingsRepresentation.class));
        when(userResource.roles().getAll().getRealmMappings()).thenReturn(new ArrayList<>());
        when(usersResource.create(any())).thenReturn(response);
        when(userResource.groups()).thenReturn(new ArrayList<>());

        UserResponse answer = userService.getUserById(uuid);
        Assertions.assertEquals(user.getEmail(), answer.getEmail());
    }

}
