package org.apache.myfaces.transforma.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Locale;

/**
 * Managed bean for handling locale and internationalization in the Transforma-Faces application.
 * 
 * @author Transforma-Faces Team
 * @version 1.0.0
 */
@ManagedBean
@SessionScoped
public class LocaleBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Locale locale;
    
    /**
     * Default constructor to ensure locale is initialized.
     */
    public LocaleBean() {
        this.locale = Locale.ENGLISH;
    }
    
    /**
     * Get the current locale.
     * 
     * @return the current locale
     */
    public Locale getLocale() {
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        return locale;
    }
    
    /**
     * Set the locale and update the FacesContext.
     * 
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null && facesContext.getViewRoot() != null) {
                facesContext.getViewRoot().setLocale(locale);
            }
        } catch (Exception e) {
            // Handle cases where FacesContext is not available (e.g., unit tests)
            // Just set the locale property
        }
    }
    
    /**
     * Change locale to English.
     */
    public void changeToEnglish() {
        setLocale(Locale.ENGLISH);
    }
    
    /**
     * Change locale to Spanish.
     */
    public void changeToSpanish() {
        setLocale(new Locale("es"));
    }
    
    /**
     * Change locale to French.
     */
    public void changeToFrench() {
        setLocale(Locale.FRENCH);
    }
    
    /**
     * Get the current language code.
     * 
     * @return the language code
     */
    public String getLanguage() {
        return getLocale().getLanguage();
    }
    
    /**
     * Get the current country code.
     * 
     * @return the country code
     */
    public String getCountry() {
        return getLocale().getCountry();
    }
    
    /**
     * Get the locale as a string representation.
     * 
     * @return the locale as a string
     */
    public String getLocaleString() {
        return getLocale().toString();
    }
} 