package net.makeshot.upload;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.makeshot.ini.Reader;
import net.makeshot.logs.LOG;
import net.makeshot.main.Notifications;
import net.makeshot.sound.Play;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Immio {
	Reader read = new Reader();

	Immio(String pathu, String type) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			URI url = new URI("http://imm.io/store/");
			HttpPost httppost = new HttpPost(url);
			FileBody bin = new FileBody(new File(pathu));
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("image", bin);
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			String answer;
			if (responseEntity != null) {
				answer = EntityUtils.toString(responseEntity);
				parse(answer, type, pathu);
			} else {
				if (net.makeshot.settings.Static.tooltip == 1)
					Notifications.showNotification(false, "ops :(", pathu);
				if (net.makeshot.settings.Static.playSound == 1)
					Play.error();
			}

		} catch (URISyntaxException | ParseException | IOException e) {
			if (net.makeshot.settings.Static.tooltip == 1)
				Notifications.showNotification(false, "ops :(", pathu);
			if (net.makeshot.settings.Static.playSound == 1)
				Play.error();
			LOG.error(e);
		}
	}

	private void parse(String s, String type, String pathu) {
		new ToMakeshot(new JSONObject(s.substring(26).replaceAll("}}", "}"))
				.get("uri").toString(), type, pathu);
	}
}
