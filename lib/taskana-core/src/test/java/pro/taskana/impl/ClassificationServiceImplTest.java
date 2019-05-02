package pro.taskana.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.Classification;
import pro.taskana.JobService;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.jobs.ScheduledJob;
import pro.taskana.mappings.ClassificationMapper;
import pro.taskana.mappings.JobMapper;

/**
 * Unit Test for ClassificationServiceImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
public class ClassificationServiceImplTest {

    @Spy
    @InjectMocks
    private ClassificationServiceImpl cutSpy;
    @Mock
    private ClassificationMapper classificationMapperMock;
    @Mock
    private TaskanaEngineImpl taskanaEngineImplMock;
    @Mock
    private ClassificationQueryImpl classificationQueryImplMock;
    @Mock
    private SqlSession sqlSessionMock;
    @Mock
    private JobService jobServiceMock;

    @Test
    public void testCreateClassificationQuery() {
        cutSpy.createClassificationQuery();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
    }

    @Test
    public void testThrowExceptionIdIfClassificationIsCreatedWithAnExplicitId()
        throws DomainNotFoundException, InvalidArgumentException,
        NotAuthorizedException, ClassificationAlreadyExistException {
        Classification classification = createDummyClassification();
        when(taskanaEngineImplMock.domainExists(any())).thenReturn(true);
        InvalidArgumentException e = assertThrows(InvalidArgumentException.class,
            () -> cutSpy.createClassification(classification));
        assertEquals(e.getMessage(), "ClassificationId should be null on creation");

    }

    private Classification createDummyClassification() {
        return this.createDummyClassification("ID: 1");
    }

    private Classification createDummyClassification(String id) {

        ClassificationImpl classificationImpl = new ClassificationImpl();
        classificationImpl.setDescription("A DUMMY FOR TESTING A SERVICE");
        classificationImpl.setName("SERVICE-DUMMY");
        classificationImpl.setDomain("DOMAIN_A");
        classificationImpl.setServiceLevel("P2D");
        classificationImpl.setId(id);
        classificationImpl.setKey("ABC111");
        classificationImpl.setParentId("");
        classificationImpl.setParentKey("");
        return classificationImpl;
    }

    /**
     * This is the mock of a jobRunner.
     */
    private class JobRunnerMock implements JobMapper {

        @Override
        public void insertJob(ScheduledJob job) {

        }

        @Override
        public List<ScheduledJob> findJobsToRun() {
            return null;
        }

        @Override
        public void update(ScheduledJob job) {

        }

        @Override
        public void delete(ScheduledJob job) {

        }
    }
}
