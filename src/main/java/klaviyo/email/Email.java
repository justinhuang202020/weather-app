package klaviyo.email;

import java.util.List;

public interface Email {
	public boolean sendEmail();
	public void addEmailText(String message);
	public void addRecipients(List<String> addRecipients);
	public void addSubject(String subject);
	public String getSubject();
	public String getText();
	public List<String> getRecipients();
}
