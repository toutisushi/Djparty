package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import listener.CurrentMusic;
import model.Vote;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

public class NotificationWatcher implements Runnable {

	CurrentMusic cm;
	
	public NotificationWatcher(CurrentMusic cm) {
		super();
		this.cm = cm;
	}

	@Override
	public void run() {

		while (true) {
			try {

				URL url = new URL("http://localhost:8080/SpringMVC/rest/djparty/getvotes?partyId=1&time=toto");
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

				String output;
				String msgReceived ="";
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
					msgReceived = msgReceived + output;
				}
				ObjectMapper mapper = new ObjectMapper();
				HashMap<Integer,Integer> mapVotes = new HashMap<Integer,Integer>();
				List<Vote> voteList = mapper.readValue(msgReceived, TypeFactory.defaultInstance().constructCollectionType(List.class, Vote.class));
				for (Vote v : voteList)
				{
					System.out.println("User : " + v.getUserid() + " voted for song : " + v.getSongid() + "in the party : " + v.getPartyid());
					if(mapVotes.containsKey(v.getSongid()))
					{
						int voteCount = mapVotes.get(v.getSongid());
						mapVotes.put(v.getSongid(), voteCount++);
					}
					else
					{
						mapVotes.put(v.getSongid(), 1);
					}
				}
				cm.notifyVote(mapVotes);
			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
