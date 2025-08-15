package org.apache.myfaces.transforma;

import org.apache.myfaces.transforma.beans.LocaleBean;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertNotNull(bean, "LocaleBean should not be null");
    }
    
    @Test
    public void testDefaultLocale() {
        LocaleBean bean = new LocaleBean();
        assertEquals(Locale.ENGLISH, bean.getLocale(), "Default locale should be English");
    }
    
    @Test
    public void testLocaleChange() {
        LocaleBean bean = new LocaleBean();
        Locale spanish = new Locale("es");
        
        // Test setting locale without FacesContext (for unit testing)
        bean.setLocale(spanish);
        assertEquals(spanish, bean.getLocale(), "Locale should be changed to Spanish");
    }
    
    @Test
    public void testLanguageCode() {
        LocaleBean bean = new LocaleBean();
        assertEquals("en", bean.getLanguage(), "Language code should be 'en'");
    }
    
    @Test
    public void testCountryCode() {
        LocaleBean bean = new LocaleBean();
        assertEquals("", bean.getCountry(), "Country code should be empty for default locale");
    }
} 