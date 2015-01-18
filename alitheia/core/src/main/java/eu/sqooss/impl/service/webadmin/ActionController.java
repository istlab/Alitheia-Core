package eu.sqooss.impl.service.webadmin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;

public abstract class ActionController {
    
    /** The debug flag */
    private boolean debug = false;
    
    private enum MessageLevel {
        SUCCESS, INFO, WARNING, DANGER
    }

    /** Multiple collections for informational messages for the user. */
    private Map<MessageLevel, List<String>> messages;
    
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Action {
        String value() default "";
    }

    /** The default template */
    private String template;

    /**
     * Instantiates a new action controller with a given default template
     *
     * @param template the template
     */
    public ActionController(String template) {
        this.template = template;
    }

    /**
     * Inits the VelocityContext with commonly used information
     *
     * @param velocityContext the velocity context
     */
    private void initVC(VelocityContext velocityContext) {
        // Simple string substitutions
        velocityContext.put("APP_NAME", Localization.getLbl("app_name"));
        velocityContext.put("COPYRIGHT", "Copyright 2007-2015"
                + "<a href=\"http://www.sqo-oss.eu/about/\">"
                + "&nbsp;SQO-OSS Consortium Members" + "</a>");
        velocityContext.put("LOGO", "<img src='/logo' id='logo' alt='Logo' />");
        velocityContext.put("UPTIME", WebAdminRenderer.getUptime());
    }
    
    /**
     * Sets the default template.
     *
     * @param velocityContext the velocity context that the template should be assigned to
     */
    private void setDefaultTemplate(VelocityContext velocityContext) {
        velocityContext.put("contentPath", template);
    }
    
    private void writeDebug(Map<String, String> requestParameters, VelocityContext velocityContext) {
        velocityContext.put("DEBUG_REQUEST_PARAMETERS", requestParameters);
    }
    
    /**
     * Clear all messages for the upcoming action
     */
    private void clearMessages() {
        messages = new HashMap<>();
        for (MessageLevel level : MessageLevel.values()) {
            messages.put(level, new ArrayList<String>());
        }
    }
    
    /**
     * Adds the message.
     *
     * @param messageLevel the message level
     * @param message the message
     */
    private void addMessage(MessageLevel messageLevel, String message) {
        messages.get(messageLevel).add(message);
    }
    
    /**
     * Adds the success message;
     *
     * @param message the message
     */
    public void addSuccess(String message) {
        addMessage(MessageLevel.SUCCESS, message);
    }
    
    /**
     * Adds the info message;
     *
     * @param message the message
     */
    public void addInfo(String message) {
        addMessage(MessageLevel.INFO, message);
    }
    
    /**
     * Adds the warning message;
     *
     * @param message the message
     */
    public void addWarning(String message) {
        addMessage(MessageLevel.WARNING, message);
    }
    
    /**
     * Adds the danger message;
     *
     * @param message the message
     */
    public void addDanger(String message) {
        addMessage(MessageLevel.DANGER, message);
    }

    /**
     * Render an action
     *
     * @param action the name action
     * @param velocityContext the velocity context
     * @param requestParameters the request parameters
     */
    public void render(String action, VelocityContext velocityContext,
            Map<String, String> requestParameters) {
        
        beginAction(requestParameters, velocityContext);

        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Action.class)) {
                Action actionAnnotation = method.getAnnotation(Action.class);
                String path = actionAnnotation.value();
                if (action.equals(path)) {
                    try {
                        if (String.class == method.getReturnType()) {
                            String customTemplate = (String) method.invoke(this, requestParameters, velocityContext);

                            // Put template path
                            if (null != customTemplate) {
                                velocityContext.put("contentPath", customTemplate);
                            } else {
                                setDefaultTemplate(velocityContext);
                            }
                        } else {
                            method.invoke(this, requestParameters, velocityContext);
                            setDefaultTemplate(velocityContext);
                        }
                        finalizeAction(requestParameters, velocityContext);
                        return;
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        setDefaultTemplate(velocityContext);
        finalizeAction(requestParameters, velocityContext);
    }

    /**
     * Method to be overridden for stuff that should be executed before any action
     *
     * @param requestParameters the request parameters
     * @param velocityContext the velocity context
     */
    protected void beforeAction(Map<String, String> requestParameters, VelocityContext velocityContext){};
    
    /**
     * Method to be overridden for stuff that should be executed after any action
     *
     * @param requestParameters the request parameters
     * @param velocityContext the velocity context
     */
    protected void afterAction(Map<String, String> requestParameters, VelocityContext velocityContext){};
    
    /**
     * Checks if the debug flag is set.
     *
     * @return true, if debug is enabled
     */
    public boolean isDebug() {
        return debug;
    }
    
    /**
     * Sets the debug flag.
     *
     * @param debug true for debug information
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    /**
     * Prepare for next action.
     *
     * @param requestParameters the request parameters
     * @param velocityContext the velocity context
     */
    private void beginAction(Map<String, String> requestParameters, VelocityContext velocityContext) {
        clearMessages();
        
        initVC(velocityContext);
        
        if (isDebug()) {
            writeDebug(requestParameters, velocityContext);
        }
        
        beforeAction(requestParameters, velocityContext);
    }
    
    /**
     * Finalize everything for the action to complete.
     *
     * @param requestParameters the request parameters
     * @param velocityContext the velocity context
     */
    private void finalizeAction(Map<String, String> requestParameters, VelocityContext velocityContext) {
        for (MessageLevel level : MessageLevel.values()) {
            velocityContext.put("MESSAGES_" + level.name(), messages.get(level));
        }
        afterAction(requestParameters, velocityContext);
    }
}
