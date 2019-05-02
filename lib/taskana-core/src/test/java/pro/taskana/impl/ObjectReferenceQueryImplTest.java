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

import pro.taskana.ObjectReference;

/**
 * Test for ObjectReferenceQueryImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
public class ObjectReferenceQueryImplTest {

    ObjectReferenceQueryImpl objectReferenceQueryImpl;

    @Mock
    TaskanaEngineImpl taskanaEngine;

    @Mock
    SqlSession sqlSession;

    @BeforeEach
    public void setup() {
        objectReferenceQueryImpl = new ObjectReferenceQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<ObjectReference> result = objectReferenceQueryImpl.valueIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .systemInstanceIn("1", "2")
            .systemIn("superId")
            .list();
        assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<ObjectReference> result = objectReferenceQueryImpl.valueIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .systemInstanceIn("1", "2")
            .systemIn("superId")
            .list(1, 1);
        assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new ObjectReference());

        ObjectReference result = objectReferenceQueryImpl.valueIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .systemInstanceIn("1", "2")
            .systemIn("superId")
            .single();
        assertNotNull(result);
    }
}
