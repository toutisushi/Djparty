package process;

import java.io.IOException;
import java.util.List;

import model.Song;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class NotificationSender implements Runnable{

	
	private List<Song> songs;
	
	
	public NotificationSender(List<Song> songs) {
		super();
		this.songs = songs;
	}


	@Override
	public void run() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			String json = ow.writeValueAsString(songs);
			System.out.println("THREAD " + json);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
//		mapper.convertValue
//		(,TypeFactory.defaultInstance().constructCollectionType(List.class, Song.class));
//		StringRequestEntity requestEntity = new StringRequestEntity(
//			    JSON_STRING,
//			    "application/json",
//			    "UTF-8");
//
//			PostMethod postMethod = new PostMethod("http://example.com/action");
//			postMethod.setRequestEntity(requestEntity);
//
//			int statusCode = httpClient.executeMethod(postMethod);
		
	}

}
