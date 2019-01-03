package klaviyo.email;

import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * 
 * @author Justin Huang
 * This class implements the email interface tailored specifically for html emails
 *
 */
public class HtmlEmail implements Email{
	//please fill this with your own email
	private final String from = "*****";
	//please fill this with your own email password
	private final String password = "****";
	private String subject;
	private String message;
	private List<String> recipients;
	//empty constructor
	public HtmlEmail() {
		
	}
	/**
	 * 
	 * @param m: message of the email
	 * @param s: subject of the email 
	 * @param rc: list of recipients
	 */
	public HtmlEmail(String m, String s, List<String>rc) {
		subject = s;
		message = m;
		recipients = rc;
		
	}
	/**
	 * Uses gmail's smtp servers to send an email.
	 * 
	 */
	@Override
	public boolean sendEmail() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		  });
		try {
			Message mm = new MimeMessage(session);
			mm.setFrom(new InternetAddress(from));
			for (String r:recipients) {
				mm.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(r));
			}	
			mm.setSubject(subject);
			mm.setContent(message, "text/html");
			Transport.send(mm);
		} catch (MessagingException e) {
			return false;
		}
	
		return true;
	}

	@Override
	public void addEmailText(String m) {
		message = m;
		
	}

	@Override
	public void addRecipients(List<String> addRc) {
		recipients = addRc;
		
	}

	@Override
	public void addSubject(String s) {
		subject= s;
		
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public String getText() {
		return message;
	}

	@Override
	public List<String> getRecipients() {
		return recipients;
	}

}
