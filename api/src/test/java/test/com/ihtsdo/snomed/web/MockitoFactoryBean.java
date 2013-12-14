package test.com.ihtsdo.snomed.web;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

/**
 * A {@link FactoryBean} for creating mocked beans based on Mockito so that they 
 * can be {@link @Autowired} into Spring test configurations.
 *
 * @author Mattias Severson, Jayway
 *
 * @see FactoryBean
 * @see org.mockito.Mockito
 */
public class MockitoFactoryBean<T> implements FactoryBean<T> {

    private Class<T> classToBeMocked;

    /**
     * Creates a Mockito mock instance of the provided class.
     * @param classToBeMocked The class to be mocked.
     */
    public MockitoFactoryBean(Class<T> classToBeMocked) {
        this.classToBeMocked = classToBeMocked;
    }

    @Override
    public T getObject() throws Exception {
        return Mockito.mock(classToBeMocked);
    }

    @Override
    public Class<?> getObjectType() {
        return classToBeMocked;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}