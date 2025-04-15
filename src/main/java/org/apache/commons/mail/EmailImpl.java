package org.apache.commons.mail;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

public class EmailImpl extends Email {

    // Override the addBcc method
    @Override
    public Email addBcc(String... emails) throws EmailException {
        super.addBcc(emails);  // Calls the parent class's method
        return this;
    }

    public Email setMsg(String msg) throws EmailException {
        return null;
    }

    // Method to access the protected bccList

    public List<InternetAddress> getBccList() {
        return this.bccList;
    }
    public List<InternetAddress> getReplyList() {
        return this.replyList;
    }
    public MimeMessage getMessage() {
        return this.message;
    }

    public String getCharset() {
        return this.charset;
    }
    public String getContentType() {
        return this.contentType;
    }


	/*
	 * public List<InternetAddress> getCcList() { return this.ccList; }
	 */

    public Map<String, String> getHeaders() {
        return this.headers;
    }

	public List<InternetAddress> getCcList() {
		// TODO Auto-generated method stub
		return this.ccList;
	}
}
