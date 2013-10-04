package edu.neu.android.mhealth.uscteensver1.threads;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import android.os.AsyncTask;

public class SendEmailTask extends AsyncTask<Message, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Message... messages) {
        try {
            Transport.send(messages[0]);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
	
}
