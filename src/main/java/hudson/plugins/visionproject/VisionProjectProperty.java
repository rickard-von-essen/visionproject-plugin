package hudson.plugins.visionproject;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class VisionProjectProperty extends JobProperty<AbstractProject<?,?>> {

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends JobPropertyDescriptor {
    	private String regex;
    	public URI baseurl;
    	
        public DescriptorImpl() {
            super(VisionProjectProperty.class);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
        	return false;
        }

        public String getDisplayName() {
        	return "VisionProject";
        }
        
        @Override
        public VisionProjectProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {
        	return new VisionProjectProperty();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) {
            try {
                regex = req.getParameter("visionproject.regex");
                baseurl = URI.create(req.getParameter("visionproject.base"));
            	
			} catch (IllegalArgumentException illegalUri) {
		
			}
            save();
            return true;
        }

        public String getBaseUrl() {
            return "http://visionproject";
        }
        
        public String getRegex() {
        	if(regex == null) return "\\b[0-9.]*[0-9]\\b";
        	return regex;
        }
                
        /**
         * Checks if the Issue RegEx compiles.
         */
        public FormValidation doRegexCheck(@QueryParameter String value) {
            if(Util.fixEmpty(value)==null) {
                return FormValidation.error("No Issue ID regex");
            }
            try {
                Pattern.compile(value);
                return FormValidation.ok();
            } catch (PatternSyntaxException e) {
                return FormValidation.error("Pattern cannot be compiled");
            }
        }

        /**
         * Checks if the VisionProject URL is accessible and exists.
         */
        public FormValidation doUrlCheck(@QueryParameter final String value)
                throws IOException, ServletException {
            // this can be used to check existence of any file in any URL, so admin only
            if (!Hudson.getInstance().hasPermission(Hudson.ADMINISTER)) return FormValidation.ok();
            return new FormValidation.URLCheck() {
                @Override
                protected FormValidation check() throws IOException, ServletException {
                    String url = Util.fixEmpty(value);
                    if(url==null) {
                        return FormValidation.error("No VisionProject base URL");
                    }
                    // TODO: No validation for now.
                    return FormValidation.ok();
                }
            }.check();
        }
    }
}
