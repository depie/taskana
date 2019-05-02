package pro.taskana.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.WorkbasketAccessItem;

/**
 * Test for WorkbasketAccessItemQueryImpl.
 *
 * @author jsa
 */
@ExtendWith(MockitoExtension.class)
public class WorkbasketAccessItemQueryImplTest {

    private WorkbasketAccessItemQueryImpl workbasketAccessItemQueryImpl;

    @Mock
    private TaskanaEngineImpl taskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @BeforeEach
    public void setup() {
        workbasketAccessItemQueryImpl = new WorkbasketAccessItemQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<WorkbasketAccessItem> result = workbasketAccessItemQueryImpl.accessIdIn("test", "asd")
            .list();
        assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<WorkbasketAccessItem> result = workbasketAccessItemQueryImpl.accessIdIn("test", "asd")
            .list(1, 1);
        assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new WorkbasketAccessItemImpl());

        WorkbasketAccessItem result = workbasketAccessItemQueryImpl.accessIdIn("test", "asd")
            .single();
        assertNotNull(result);
    }
}
