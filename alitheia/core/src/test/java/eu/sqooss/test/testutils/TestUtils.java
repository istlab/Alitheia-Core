package eu.sqooss.test.testutils;

import static org.mockito.Mockito.*;
import com.google.inject.Provider;

public class TestUtils 
{
    public static<T> Provider<T> provide(T val) 
    {
        @SuppressWarnings("unchecked")
        Provider<T> ret = (Provider<T>) mock(Provider.class);
        when(ret.get()).thenReturn(val);
        return ret;
    }
}
