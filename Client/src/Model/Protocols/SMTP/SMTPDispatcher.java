package Model.Protocols.SMTP;

import Model.MailBox.MailException;
import Utilities.DNS;
import Utilities.TestRegex;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SMTPDispatcher {
    private HashMap<String, SMTP> smtps;

    public SMTPDispatcher() throws SMTPException {
        List<String> domains = DNS.getDomains();
        for(String domain : domains) {
            if((!smtps.containsKey(domain)) || (smtps.get(domain) == null)) {
                SMTP smtp = new SMTP();
                try {
                    smtp.Connect(domain);
                    smtps.put(domain, smtp);
                } catch (SMTPException e) {
                    throw new SMTPException("Domain name " + domain + " seems invalid as SMTP server", e);
                }
            }
        }
    }

    /**
     * Send mails to one or more recipient.
     * Send protocol optimized to send all message at once for one server.
     * @param recipients    Recipients addresses, separated by ';'
     * @param forwarder     Forwarder mail address
     * @param subject       Mail's subject
     * @param corpus        Mail's corpus
     * @return Error list
     * @throws SMTPException Error while sending mails.
     */
    public List<String> SendMail(String recipients, String forwarder, String subject, String corpus) throws SMTPException {
        HashMap<String, ArrayList<String>> recipientTab = sortRecipients(recipients);
        MailConvertor mailConvertor = createMail(forwarder, subject, corpus);

        ArrayList<String> errors = new ArrayList<>();
        for(String domain : recipientTab.keySet()) {
            String recipientsList = String.join(";", recipientTab.get(domain));
            SMTP smtp = smtps.get(domain);
            errors.addAll(smtp.SendMail(recipientsList, mailConvertor));
        }
        return errors;
    }

    /**
     * Sort a recipients' list by domain name
     * @param recipients Recipient list, separated with ';'
     * @return Map<Domain name, Recipient list>
     */
    private HashMap<String, ArrayList<String>> sortRecipients(String recipients) {
        String[] to = recipients.split(";");
        HashMap<String, ArrayList<String>> tabRecipients = new HashMap<>();
        for(String dest : to) {
            String domain = TestRegex.GetDomain(dest.trim());
            ArrayList<String> list;
            if(!tabRecipients.containsKey(domain)) {
                list = new ArrayList<>();
            } else {
                list = tabRecipients.get(domain);
            }
            list.add(dest.trim());
            tabRecipients.put(domain, list);
        }
        return tabRecipients;
    }

    /**
     * Prepare the mail to send.
     * @param forwarder Forwarder's mail address
     * @param subject   Mail's subject
     * @param corpus    Mail's corpus
     * @return MailConvertor with empty recipient
     */
    private MailConvertor createMail(String forwarder, String subject, String corpus) {
        MailConvertor mailConvertor = new MailConvertor();
        mailConvertor.setSubject(subject);
        mailConvertor.setFrom(forwarder);
        mailConvertor.setMessage(corpus);
        return mailConvertor;
    }
}
