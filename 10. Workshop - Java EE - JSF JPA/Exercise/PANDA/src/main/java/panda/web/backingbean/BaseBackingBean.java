package panda.web.backingbean;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseBackingBean {

    @Inject
    protected ExternalContext externalContext;

    @Inject
    protected FacesContext facesContext;

    @Inject
    private Logger logger;

    protected void redirect(String url) {
        try {
            externalContext.redirect(url);
        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            logger.log(Level.SEVERE, e, () -> "Failed redirect to: " + url);
        }
    }

    protected void addMessage(String message) {
        addMessageRunnable(message).run();
    }

    protected Runnable addMessageRunnable(String message) {
        return () -> facesContext.addMessage(null, new FacesMessage(message));
    }
}
