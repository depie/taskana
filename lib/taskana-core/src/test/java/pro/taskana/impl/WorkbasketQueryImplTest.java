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

import pro.taskana.WorkbasketSummary;

/**
 * Test for WorkbasketQueryImpl.
 *
 * @author jsa
 */
@ExtendWith(MockitoExtension.class)
public class WorkbasketQueryImplTest {

    private WorkbasketQueryImpl workbasketQueryImpl;

    @Mock
    private TaskanaEngineImpl taskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @BeforeEach
    public void setup() {
        workbasketQueryImpl = new WorkbasketQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<WorkbasketSummary> result = workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .list();
        assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<WorkbasketSummary> result = workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .list(1, 1);
        assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new WorkbasketSummaryImpl());

        WorkbasketSummary result = workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .single();
        assertNotNull(result);
    }
}
