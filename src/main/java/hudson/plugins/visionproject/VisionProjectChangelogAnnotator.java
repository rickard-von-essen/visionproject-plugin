package hudson.plugins.visionproject;

import hudson.Extension;
import hudson.MarkupText;
import hudson.MarkupText.SubText;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Extension
public class VisionProjectChangelogAnnotator extends ChangeLogAnnotator {

	static final Pattern VP_ID = Pattern.compile("\\w+-\\d+");

	private static String getId(SubText token) {
		String id = null;
		for (int i = 0;; i++) {
			id = token.group(i);
			
			Matcher res = VP_ID.matcher(id);
			if (res.find()) return res.group();
			
			LOGGER.log(Level.FINE, "{0} is not a Vision Project Issue ID, trying next group.", id);
		}
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

		for (SubText token : text.findTokens(pattern)) {
			String key = null;
			try {
				key = getId(token);
				LOGGER.log(Level.INFO, "Found key: {0} in commit message.", key);
			} catch (Exception e) {
				// this means we've exhausted all groups, and didn't find
				// anything.
				continue;
			}
			String baseUrl = VisionProjectProperty.DESCRIPTOR.getBaseUrl();
			String startTag = String.format(
					"<a href='%s/EditProjectIssueFromMail.do?issueKey=%s'>",
					baseUrl, key);
			LOGGER.log(Level.INFO, "Anchor tag: {0}", startTag);
			token.surroundWith(startTag, "</a>");
		}
	}

	private static final Logger LOGGER = Logger
			.getLogger(VisionProjectChangelogAnnotator.class.getName());

}
