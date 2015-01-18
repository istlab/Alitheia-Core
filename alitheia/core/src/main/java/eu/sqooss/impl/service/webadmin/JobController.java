package eu.sqooss.impl.service.webadmin;

import java.util.Map;

import org.apache.velocity.VelocityContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;

public class JobController extends ActionController {
    private Scheduler scheduler;

    public JobController() {
        super("jobs.html");

        scheduler = AlitheiaCore.getInstance().getScheduler();
    }

    /**
     * Home; TODO: Unknown what it should do.
     *
     * @param requestParameters
     *            the request parameters
     * @param velocityContext
     *            the velocity context
     */
    @Action
    public void home(Map<String, String> requestParameters, VelocityContext velocityContext) {
        runningJobs(requestParameters, velocityContext);
        waitStats(requestParameters, velocityContext);
        failStats(requestParameters, velocityContext);
    }

    public class FailedJob {
        private String jobType;
        private String exceptionType;
        private String exceptionText;
        private String[] stackTrace;

        public FailedJob(Job job) {
            if (null == job) {
                jobType = Localization.getLbl("not_available_short");
                exceptionType = Localization.getLbl("not_available_short");
                exceptionText = Localization.getLbl("not_available_short");
                stackTrace = new String[0];
            } else {
                Class<? extends Job> clazz = job.getClass();
                if (null != clazz) {
                    jobType = clazz.toString();
                }
                Exception exception = job.getErrorException();
                if (null != exception) {
                    Class<? extends Exception> exceptionClazz = exception.getClass();
                    if (null != exceptionClazz) {
                        exceptionType = exceptionClazz.getPackage().getName() + "." + exceptionClazz.getSimpleName();
                    }
                    exceptionText = exception.getMessage();

                    StackTraceElement[] trace = exception.getStackTrace();
                    if (null != trace) {
                        stackTrace = new String[trace.length];
                        int i = 0;
                        for (StackTraceElement element : trace) {
                            if (null == element) {
                                stackTrace[i++] = Localization.getLbl("not_available_short");
                                continue;
                            }
                            stackTrace[i++] = element.getClassName() +"."+ element.getMethodName() +"(), ("+ element.getFileName() +":"+ element.getLineNumber()+")";
                        }
                    }
                }
            }
        }

        public String getJobType() {
            return jobType;
        }
        public String getExceptionType() {
            return exceptionType;
        }
        public String getExceptionText() {
            return exceptionText;
        }
        public String[] getStackTrace() {
            return stackTrace;
        }
    }

    /**
     * Write a list of failed jobs to the VelocityContext.
     *
     * @param requestParameters
     *            the request parameters
     * @param velocityContext
     *            the velocity context
     */
    @Action("failedJobs")
    public String failedJobs(Map<String, String> requestParameters, VelocityContext velocityContext) {
        Job[] failedQueue = scheduler.getFailedQueue();
        FailedJob[] processedFailedQueue = new FailedJob[failedQueue.length];
        int i = 0;
        for (Job job : failedQueue) {
            processedFailedQueue[i++] = new FailedJob(job);
        }
        velocityContext.put("failedJobs", processedFailedQueue);
        return "jobs_failed_detail.html";
    }

    /**
     * Write stats about failing jobs to the VelocityContext.
     *
     * @param requestParameters
     *            the request parameters
     * @param velocityContext
     *            the velocity context
     */
    @Action("failStats")
    public void failStats(Map<String, String> requestParameters, VelocityContext velocityContext) {
        velocityContext.put("failStats", scheduler.getSchedulerStats().getFailedJobTypes().entrySet());
    }

    /**
     * Write running jobs to the VelocityContext.
     *
     * @param requestParameters
     *            the request parameters
     * @param velocityContext
     *            the velocity context
     */
    @Action("runningJobs")
    public void runningJobs(Map<String, String> requestParameters, VelocityContext velocityContext) {
        velocityContext.put("runningJobs", scheduler.getSchedulerStats().getRunJobs());
    }

    /**
     * Write stats about waiting jobs to the VelocityContext.
     *
     * @param requestParameters
     *            the request parameters
     * @param velocityContext
     *            the velocity context
     */
    @Action("waitStats")
    public void waitStats(Map<String, String> requestParameters, VelocityContext velocityContext) {
        velocityContext.put("waitStats", scheduler.getSchedulerStats().getWaitingJobTypes().entrySet());
    }
}
