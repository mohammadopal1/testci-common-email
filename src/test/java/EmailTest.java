import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.Wiser;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class EmailTest {

    private static final String TEST_EMAIL = "testemail@email.com";
    private Email email;
    private MockSmtpServer mockSmtpServer;
    
    //    upol

    @BeforeEach
    void initialize() {
        email = new CustomEmail();
        mockSmtpServer = new MockSmtpServer();
        mockSmtpServer.start();
    }

    @AfterEach
    void cleanup() {
        mockSmtpServer.stop();
    }

    @Test
    void verifyAddBccFunctionality() throws Exception {
        String[] bccEmails = {"bcc1@test.com", "bcc2@test.com"};
        Email result = email.addBcc(bccEmails);
        assertNotNull(result, "The result should not be null");
        List<InternetAddress> bccList = email.getBccAddresses();
        assertEquals(2, bccList.size(), "The BCC list should contain two addresses");
        assertEquals("bcc1@test.com", bccList.get(0).getAddress(), "The first BCC address should match");
    }

    @Test
    void verifyAddBccWithInvalidInput() {
        assertThrows(EmailException.class, () -> email.addBcc((String[]) null), "Null input should throw an exception");
        assertThrows(EmailException.class, () -> email.addBcc(), "Empty array should throw an exception");
    }

    @Test
    void verifyAddCcFunctionality() throws Exception {
        String ccEmail = "cc@test.com";
        Email result = email.addCc(ccEmail);
        assertNotNull(result, "The result should not be null");
        List<InternetAddress> ccList = email.getCcAddresses();
        assertEquals(1, ccList.size(), "The CC list should contain one address");
        assertEquals(ccEmail, ccList.get(0).getAddress(), "The CC email should match");
    }

    @Test
    void verifyAddHeaderFunctionality() {
        String headerName = "X-Priority";
        String headerValue = "1";
        assertDoesNotThrow(() -> email.addHeader(headerName, headerValue), "Adding a header should not throw an exception");
    }

    @Test
    void verifyAddHeaderWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> email.addHeader(null, "validValue"), "Null header name should throw an exception");
        assertThrows(IllegalArgumentException.class, () -> email.addHeader("", "validValue"), "Empty header name should throw an exception");
        assertThrows(IllegalArgumentException.class, () -> email.addHeader("validName", null), "Null header value should throw an exception");
        assertThrows(IllegalArgumentException.class, () -> email.addHeader("validName", ""), "Empty header value should throw an exception");
    }

    @Test
    void verifyAddReplyToFunctionality() throws Exception {
        String replyEmail = "reply@test.com";
        String replyName = "Reply Person";
        Email result = email.addReplyTo(replyEmail, replyName);
        assertNotNull(result, "The result should not be null");
        List<InternetAddress> replyList = email.getReplyToAddresses();
        assertEquals(1, replyList.size(), "The reply-to list should contain one address");
        assertEquals(replyEmail, replyList.get(0).getAddress(), "The reply-to email should match");
    }

    @Test
    void verifyBuildMimeMessageFunctionality() throws Exception {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Test Subject");
        email.setMsg("Test Message");
        assertDoesNotThrow(() -> email.buildMimeMessage(), "Building the MIME message should not throw an exception");
    }

    @Test
    void verifyBuildMimeMessageTwiceThrowsException() throws Exception {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Test Subject");
        email.setMsg("Test Message");

        email.buildMimeMessage();
        assertThrows(IllegalStateException.class, () -> email.buildMimeMessage(), "Building the MIME message twice should throw an exception");
    }

    @Test
    void verifyBuildMimeMessageWithValidInputs() throws Exception {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Valid Test");
        email.setMsg("Valid Message");
        email.buildMimeMessage();
        assertNotNull(email.getMimeMessage(), "The MIME message should be built successfully");
    }

    @Test
    void verifyBuildMimeMessageWithReplyTo() throws Exception {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Reply-To Test");
        email.setMsg("Message with reply-to");
        email.addReplyTo("reply@example.com", "Reply Name");
        email.buildMimeMessage();
        assertEquals(1, email.getReplyToAddresses().size(), "The reply-to list should contain one address");
    }

    @Test
    void verifyBuildMimeMessageWithoutFromAddressThrowsException() throws EmailException {
        email.setHostName("localhost");
        email.addTo(TEST_EMAIL);
        email.setSubject("No From Address");
        email.setMsg("This should fail");

        assertThrows(EmailException.class, () -> email.buildMimeMessage(), "Building the MIME message without a from address should throw an exception");
    }

    @Test
    void verifyBuildMimeMessageWithoutRecipientsThrowsException() throws EmailException {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.setSubject("No Recipients");
        email.setMsg("This should fail");

        assertThrows(EmailException.class, () -> email.buildMimeMessage(), "Building the MIME message without recipients should throw an exception");
    }

    @Test
    void verifyBuildMimeMessageWithTextPlainContent() throws Exception {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Text Content");
        email.setContent("This is plain text", EmailConstants.TEXT_PLAIN);
        email.buildMimeMessage();
        assertEquals("This is plain text", email.getMimeMessage().getContent().toString(), "The message content should match the plain text set");
    }

    @Test
    void verifyBuildMimeMessageWithHeaders() throws Exception {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Headers Test");
        email.setMsg("Message with headers");
        email.addHeader("X-Custom-Header", "CustomValue");
        email.buildMimeMessage();
        assertEquals("CustomValue", email.getMimeMessage().getHeader("X-Custom-Header")[0], "The custom header value should match");
    }

    @Test
    void verifyBuildMimeMessageSetsSentDate() throws Exception {
        email.setHostName("localhost");
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Sent Date Test");
        email.setMsg("Check sent date");
        email.buildMimeMessage();
        assertNotNull(email.getMimeMessage().getSentDate(), "The sent date should be set when building the MIME message");
    }

    @Test
    void verifyGetHostNameFunctionality() {
        String hostname = "localhost";
        email.setHostName(hostname);
        assertEquals(hostname, email.getHostName(), "The hostname should match the set value");
    }

    @Test
    void verifyGetHostNameFromSession() {
        Properties properties = new Properties();
        properties.setProperty(Email.MAIL_HOST, "smtp.example.com");
        Session session = Session.getInstance(properties);
        email.setMailSession(session);
        assertEquals("smtp.example.com", email.getHostName(), "The hostname should be retrieved from the session property");
    }

    @Test
    void verifyGetMailSessionWithValidHostName() throws Exception {
        email.setHostName("smtp.example.com");
        Session session = email.getMailSession();
        assertNotNull(session, "The session should be created successfully");
        assertEquals("smtp.example.com", session.getProperty("mail.smtp.host"), "The SMTP host should be set correctly");
    }

    @Test
    void verifyGetMailSessionWithSSL() throws Exception {
        email.setHostName("smtp.example.com");
        email.setSSLOnConnect(true);
        email.setSslSmtpPort("465");

        Session session = email.getMailSession();
        assertEquals("465", session.getProperty("mail.smtp.socketFactory.port"), "The SSL SMTP port should be set correctly");
        assertEquals("javax.net.ssl.SSLSocketFactory", session.getProperty("mail.smtp.socketFactory.class"), "The SSL socket factory should be set");
    }

    @Test
    void verifyGetMailSessionWithStartTLS() throws Exception {
        email.setHostName("smtp.example.com");
        email.setStartTLSEnabled(true);
        Session session = email.getMailSession();
        assertEquals("true", session.getProperty("mail.smtp.starttls.enable"), "StartTLS should be enabled");
    }

    @Test
    void verifyGetMailSessionWithSocketTimeout() throws Exception {
        email.setHostName("smtp.example.com");
        email.setSocketTimeout(5000);
        Session session = email.getMailSession();
        assertEquals("5000", session.getProperty("mail.smtp.timeout"), "The socket timeout should be set correctly");
    }

    @Test
    void verifyGetMailSessionWithConnectionTimeout() throws Exception {
        email.setHostName("smtp.example.com");
        email.setSocketConnectionTimeout(10000);
        Session session = email.getMailSession();
        assertEquals("10000", session.getProperty("mail.smtp.connectiontimeout"), "The connection timeout should be set correctly");
    }

    @Test
    void verifyGetMailSession() throws Exception {
        email.setHostName("localhost");
        Session session = email.getMailSession();
        assertNotNull(session, "The mail session should not be null");
    }

    @Test
    void verifyGetSentDate() {
        Date testDate = new Date();
        email.setSentDate(testDate);
        assertEquals(testDate, email.getSentDate(), "The sent date should match the set date");
    }

    @Test
    void verifySetSentDateNull() {
        email.setSentDate(null);
        Date currentDate = new Date();
        assertTrue(Math.abs(currentDate.getTime() - email.getSentDate().getTime()) < 1000, "The sent date should default to the current date when set to null");
    }

    @Test
    void verifyGetSocketConnectionTimeout() {
        int timeout = 30000;
        email.setSocketConnectionTimeout(timeout);
        assertEquals(timeout, email.getSocketConnectionTimeout(), "The timeout should match the set value");
    }

    @Test
    void verifySendFunctionality() throws Exception {
        email.setHostName("localhost");
        email.setSmtpPort(25);
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAIL);
        email.setSubject("Test Subject");
        email.setMsg("Test Message");

        String messageId = email.send();
        assertNotNull(messageId, "The message ID should not be null");
        assertTrue(mockSmtpServer.hasReceivedMessage(TEST_EMAIL), "The mock server should have received the email");
    }

    @Test
    void verifySetFromFunctionality() throws Exception {
        Email result = email.setFrom(TEST_EMAIL);
        assertNotNull(result, "The result should not be null");
        assertEquals(new InternetAddress(TEST_EMAIL), email.getFromAddress(), "The from address should match the set value");
    }

    @Test
    void verifyUpdateContentTypeFunctionality() {
        String contentType = "text/plain";
        email.updateContentType(contentType);
        assertDoesNotThrow(() -> email.updateContentType(contentType), "Updating the content type should not throw an exception");
    }

    @Test
    void verifyAddToFunctionality() throws Exception {
        String toEmail = "to@test.com";
        Email result = email.addTo(toEmail);
        assertNotNull(result, "The result should not be null");
        List<InternetAddress> toList = email.getToAddresses();
        assertEquals(1, toList.size(), "The to list should contain one address");
        assertEquals(toEmail, toList.get(0).getAddress(), "The to email should match");
    }

    @Test
    void verifySetSubjectFunctionality() {
        String subject = "Test Subject";
        email.setSubject(subject);
        assertEquals(subject, email.getSubject(), "The subject should match the set value");
    }

    @Test
    void verifySetMsgNullThrowsException() {
        assertThrows(EmailException.class, () -> email.setMsg(null), "Setting a null message should throw an exception");
    }

    @Test
    void verifyAddToInvalidEmailThrowsException() {
        assertThrows(EmailException.class, () -> email.addTo("invalid-email"), "Adding an invalid email should throw an exception");
    }

    private static class CustomEmail extends Email {
        @Override
        public Email setMsg(String msg) throws EmailException {
            if (msg == null) {
                throw new EmailException("Message cannot be null");
            }
            return this;
        }
    }

    public static class MockSmtpServer {
        private Wiser mockMailServer;
        private static final int SMTP_PORT = 25;

        public void start() {
            mockMailServer = new Wiser();
            mockMailServer.setPort(SMTP_PORT);
            mockMailServer.start();
        }

        public void stop() {
            if (mockMailServer != null) {
                mockMailServer.stop();
            }
        }

        public boolean hasReceivedMessage(String recipient) {
            return mockMailServer.getMessages().stream()
                    .anyMatch(message -> message.getEnvelopeReceiver().equals(recipient));
        }
    }
}