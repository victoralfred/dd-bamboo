package com.ddlabs.atlassian.data.repository.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.ddlabs.atlassian.data.dto.ServerConfigDTO;
import com.ddlabs.atlassian.data.entity.MSConfigEntity;
import com.ddlabs.atlassian.data.mapper.ServerConfigMapper;
import com.ddlabs.atlassian.exception.DataAccessException;
import net.java.ao.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ActiveObjectsServerConfigRepositoryTest {

    private ActiveObjectsServerConfigRepository repository;
    
    @Mock
    private ActiveObjects activeObjects;
    
    @Mock
    private ServerConfigMapper serverConfigMapper;
    
    @Mock
    private MSConfigEntity mockEntity;
    
    private ServerConfigDTO testConfig;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new ActiveObjectsServerConfigRepository(activeObjects, serverConfigMapper);
        
        testConfig = new ServerConfigDTO();
        testConfig.setServerType("DatadogMetricServer");
        testConfig.setServerName("Test Server");
        testConfig.setDescription("Test Description");
        testConfig.setClientId("test-client-id");
        testConfig.setClientSecret("test-client-secret");
        
        when(serverConfigMapper.toDto(mockEntity)).thenReturn(testConfig);
    }
    
    @Test
    public void testFindByServerType() throws DataAccessException {
        when(activeObjects.find(eq(MSConfigEntity.class), any(Query.class)))
                .thenReturn(new MSConfigEntity[]{mockEntity});
        
        ServerConfigDTO result = repository.findByServerType("DatadogMetricServer");
        
        assertNotNull(result);
        assertEquals("DatadogMetricServer", result.getServerType());
        assertEquals("Test Server", result.getServerName());
        
        verify(activeObjects).find(eq(MSConfigEntity.class), any(Query.class));
        verify(serverConfigMapper).toDto(mockEntity);
    }
    
    @Test
    public void testFindByServerTypeNotFound() throws DataAccessException {
        when(activeObjects.find(eq(MSConfigEntity.class), any(Query.class)))
                .thenReturn(new MSConfigEntity[]{});
        
        ServerConfigDTO result = repository.findByServerType("NonExistentServer");
        
        assertNull(result);
        
        verify(activeObjects).find(eq(MSConfigEntity.class), any(Query.class));
        verify(serverConfigMapper, never()).toDto(any());
    }
    
    @Test
    public void testFindAll() throws DataAccessException {
        when(activeObjects.find(MSConfigEntity.class))
                .thenReturn(new MSConfigEntity[]{mockEntity, mockEntity});
        
        List<ServerConfigDTO> results = repository.findAll();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        verify(activeObjects).find(MSConfigEntity.class);
        verify(serverConfigMapper, times(2)).toDto(mockEntity);
    }
    
    @Test
    public void testSave() throws DataAccessException {
        when(activeObjects.create(MSConfigEntity.class)).thenReturn(mockEntity);
        when(serverConfigMapper.toDto(mockEntity)).thenReturn(testConfig);
        
        ServerConfigDTO result = repository.save(testConfig);
        
        assertNotNull(result);
        assertEquals("DatadogMetricServer", result.getServerType());
        
        verify(activeObjects).create(MSConfigEntity.class);
        verify(serverConfigMapper).updateEntityFromDto(mockEntity, testConfig);
        verify(mockEntity).save();
        verify(serverConfigMapper).toDto(mockEntity);
    }
    
    @Test
    public void testUpdate() throws DataAccessException {
        when(activeObjects.find(eq(MSConfigEntity.class), any(Query.class)))
                .thenReturn(new MSConfigEntity[]{mockEntity});
        
        repository.update(testConfig);
        
        verify(activeObjects).find(eq(MSConfigEntity.class), any(Query.class));
        verify(serverConfigMapper).updateEntityFromDto(mockEntity, testConfig);
        verify(mockEntity).save();
    }
    
    @Test(expected = DataAccessException.class)
    public void testUpdateNotFound() throws DataAccessException {
        when(activeObjects.find(eq(MSConfigEntity.class), any(Query.class)))
                .thenReturn(new MSConfigEntity[]{});
        
        repository.update(testConfig);
    }
    
    @Test
    public void testDelete() throws DataAccessException {
        when(activeObjects.find(eq(MSConfigEntity.class), any(Query.class)))
                .thenReturn(new MSConfigEntity[]{mockEntity});
        
        repository.delete("DatadogMetricServer");
        
        verify(activeObjects).find(eq(MSConfigEntity.class), any(Query.class));
        verify(activeObjects).delete(mockEntity);
    }
    
    @Test
    public void testDeleteNotFound() throws DataAccessException {
        when(activeObjects.find(eq(MSConfigEntity.class), any(Query.class)))
                .thenReturn(new MSConfigEntity[]{});
        
        repository.delete("NonExistentServer");
        
        verify(activeObjects).find(eq(MSConfigEntity.class), any(Query.class));
        verify(activeObjects, never()).delete(any(MSConfigEntity.class));
    }
}