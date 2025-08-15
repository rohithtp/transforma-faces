package org.apache.myfaces.transforma;

import org.apache.myfaces.transforma.beans.LocaleBean;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Locale;

/**
 * Test class for LocaleBean.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
public class LocaleBeanTest {
    
    @Test
    public void testLocaleBeanCreation() {
        LocaleBean bean = new LocaleBean();
        assertNotNull("LocaleBean should not be null", bean);
    }
    
    @Test
    public void testDefaultLocale() {
        LocaleBean bean = new LocaleBean();
        assertEquals("Default locale should be English", Locale.ENGLISH, bean.getLocale());
    }
    
    @Test
    public void testLocaleChange() {
        LocaleBean bean = new LocaleBean();
        Locale spanish = new Locale("es");
        
        // Test setting locale without FacesContext (for unit testing)
        bean.setLocale(spanish);
        assertEquals("Locale should be changed to Spanish", spanish, bean.getLocale());
    }
    
    @Test
    public void testLanguageCode() {
        LocaleBean bean = new LocaleBean();
        assertEquals("Language code should be 'en'", "en", bean.getLanguage());
    }
    
    @Test
    public void testCountryCode() {
        LocaleBean bean = new LocaleBean();
        assertEquals("Country code should be empty for default locale", "", bean.getCountry());
    }
} 