package pro.taskana.impl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketType;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.mappings.DistributionTargetMapper;
import pro.taskana.mappings.TaskMapper;
import pro.taskana.mappings.WorkbasketAccessMapper;
import pro.taskana.mappings.WorkbasketMapper;

/**
 * Unit Test for workbasketServiceImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
public class WorkbasketServiceImplTest {

    @Spy
    @InjectMocks
    private WorkbasketServiceImpl cutSpy;

    @Mock
    private WorkbasketMapper workbasketMapperMock;

    @Mock
    private TaskMapper taskMapperMock;

    @Mock
    private SqlSession sqlSessionMock;

    @Mock
    private DistributionTargetMapper distributionTargetMapperMock;

    @Mock
    private WorkbasketAccessMapper workbasketAccessMapperMock;

    @Mock
    private TaskService taskServiceMock;

    @Mock
    private TaskQuery taskQueryMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineImplMock;

    @Mock
    private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateWorkbasket_WithDistibutionTargets()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        final int distTargetAmount = 2;
        WorkbasketImpl expectedWb = createTestWorkbasket(null, "Key-1");
        doNothing().when(workbasketMapperMock).insert(expectedWb);
        doReturn(expectedWb).when(cutSpy).getWorkbasket(any());
        when(taskanaEngineImplMock.domainExists(any())).thenReturn(true);

        Workbasket actualWb = cutSpy.createWorkbasket(expectedWb);
        cutSpy.setDistributionTargets(expectedWb.getId(), createTestDistributionTargets(distTargetAmount));

        verify(taskanaEngineImplMock, times(4)).openConnection();
        verify(workbasketMapperMock, times(3)).insert(any());
        verify(cutSpy, times(distTargetAmount + 1)).getWorkbasket(any());
        verify(distributionTargetMapperMock, times(1)).deleteAllDistributionTargetsBySourceId(any());
        verify(distributionTargetMapperMock, times(distTargetAmount)).insert(any(), any());
        verify(workbasketMapperMock, times(3)).findByKeyAndDomain(any(), any());
        verify(workbasketMapperMock, times(1)).update(any());
        verify(taskanaEngineImplMock, times(4)).returnConnection();
        verify(taskanaEngineImplMock, times(4)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(3)).domainExists(any());
        verifyNoMoreInteractions(taskQueryMock, taskServiceMock, workbasketMapperMock, workbasketAccessMapperMock,
            distributionTargetMapperMock,
            taskanaEngineImplMock, taskanaEngineConfigurationMock);
        assertThat(actualWb.getId(), not(equalTo(null)));
        assertThat(actualWb.getId(), startsWith("WBI"));
        assertThat(actualWb.getCreated(), not(equalTo(null)));
        assertThat(actualWb.getModified(), not(equalTo(null)));
    }

    @Test
    public void testCreateWorkbasket_DistibutionTargetNotExisting()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        WorkbasketImpl expectedWb = createTestWorkbasket("ID-1", "Key-1");
        doNothing().when(workbasketMapperMock).insert(expectedWb);
        when(taskanaEngineImplMock.domainExists(any())).thenReturn(true);

        cutSpy.createWorkbasket(expectedWb);
        String id1 = "4711";
        List<String> destinations = new ArrayList<>(Arrays.asList(id1));
        assertThrows(WorkbasketNotFoundException.class,
            () -> cutSpy.setDistributionTargets(expectedWb.getId(), destinations));
        verify(taskanaEngineImplMock, times(3)).openConnection();
        verify(workbasketMapperMock, times(1)).insert(expectedWb);
        verify(workbasketMapperMock, times(1)).findById(any());
        verify(workbasketMapperMock, times(1)).findByKeyAndDomain(any(), any());
        verify(cutSpy, times(1)).getWorkbasket(any());
        verify(taskanaEngineImplMock, times(3)).returnConnection();
        verify(taskanaEngineImplMock, times(2)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(1)).domainExists(any());
        verifyNoMoreInteractions(taskQueryMock, taskServiceMock, workbasketMapperMock, workbasketAccessMapperMock,
            distributionTargetMapperMock,
            taskanaEngineImplMock, taskanaEngineConfigurationMock);
    }

    // TODO Add stored-check. Not getWorkbasket() because permissions are not set with this action here.
    @Disabled
    @Test
    public void testCreateWorkbasket_NotCreated() {
        WorkbasketImpl expectedWb = createTestWorkbasket(null, "Key-1");
        doNothing().when(workbasketMapperMock).insert(expectedWb);
        when(workbasketMapperMock.findById(any())).thenThrow(WorkbasketNotFoundException.class);

        assertThrows(Exception.class, () -> cutSpy.createWorkbasket(expectedWb));
        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(workbasketMapperMock, times(1)).insert(expectedWb);
        verify(workbasketMapperMock, times(1)).findById(expectedWb.getId());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(taskQueryMock, taskServiceMock, workbasketMapperMock, workbasketAccessMapperMock,
            distributionTargetMapperMock,
            taskanaEngineImplMock, taskanaEngineConfigurationMock);
    }

    @Test
    public void testDeleteWorkbasketIsUsed()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        Workbasket wb = createTestWorkbasket("WBI:0", "wb-key");
        List<TaskSummary> usages = Arrays.asList(new TaskSummaryImpl(), new TaskSummaryImpl());

        assertThrows(WorkbasketNotFoundException.class, () -> cutSpy.deleteWorkbasket(wb.getId()));
        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(cutSpy, times(1)).getWorkbasket(wb.getId());
        verify(taskanaEngineImplMock, times(0)).getTaskService();
        verify(taskServiceMock, times(0)).createTaskQuery();
        verify(taskQueryMock, times(0)).workbasketIdIn(wb.getId());
        verify(taskQueryMock, times(0)).count();
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        verifyNoMoreInteractions(taskQueryMock, taskServiceMock, workbasketAccessMapperMock,
            distributionTargetMapperMock, taskanaEngineConfigurationMock);
    }

    private WorkbasketImpl createTestWorkbasket(String id, String key) {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setId(id);
        workbasket.setKey(key);
        workbasket.setName("Workbasket " + id);
        workbasket.setDescription("Description WB with Key " + key);
        workbasket.setType(WorkbasketType.PERSONAL);
        workbasket.setDomain("DOMAIN_A");
        return workbasket;
    }

    private List<String> createTestDistributionTargets(int amount)
        throws InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, DomainNotFoundException {
        List<String> distributionsTargets = new ArrayList<>();
        amount = (amount < 0) ? 0 : amount;
        for (int i = 0; i < amount; i++) {
            WorkbasketImpl wb = createTestWorkbasket("WB-ID-" + i, "WB-KEY-" + i);
            cutSpy.createWorkbasket(wb);
            distributionsTargets.add(wb.getId());
        }
        return distributionsTargets;
    }
}
