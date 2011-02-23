package hudson.plugins.visionproject;

import hudson.Extension;
import hudson.MarkupText;
import hudson.MarkupText.SubText;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Extension
public class VisionProjectChangelogAnnotator extends ChangeLogAnnotator {
	private static String getId(SubText token) {
		return token.group(0);
	}
	
	@Override
	public void annotate(AbstractBuild<?, ?> build, Entry change,
			MarkupText text) {
		Pattern pattern = null;
		String regex = VisionProjectProperty.DESCRIPTOR.getRegex();
		try {
			pattern = Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			LOGGER.log(Level.WARNING, "Cannot compile pattern: {0}", regex);
			return;
		}
					
		for(SubText token : text.findTokens(pattern)) {
			String key = null;
			try {
				key = getId(token);
			} catch (Exception e) {
				// this means we've exhausted all groups, and didn't find anything.
				continue;
			}
			String baseUrl = VisionProjectProperty.DESCRIPTOR.getBaseUrl();
			token.surroundWith(String.format("<a href='%s/EditProjectIssueFromMail.do?issueKey=%d'>", baseUrl, key), "</a>");
		}
	}

	private static final Logger LOGGER = Logger.getLogger(VisionProjectChangelogAnnotator.class.getName());

}
