import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Objects.ImageObject;
import Objects.Track;
 class miniTrack {
    String artist;
    String name;
public miniTrack(String artist, String name) {
    this.artist = artist;
    this.name = name;}

    public String getArtist() {
        return artist;
    }
    public String getName() {
        return name;
    }
    
}
public class Recommending {
    private static final String API_KEY = "MY_API_KEY";

    

    private static void updateTrackFiles(List<Track> tracks, String accessToken) throws IOException {
        Set<String> existingTrackIds = new HashSet<>();
        JsonArray existingTracksArray = new JsonArray();
    
        // Read existing tracks from Tracks.json
        try (FileReader reader = new FileReader("Tracks.json")) {
            existingTracksArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (int i = 0; i < existingTracksArray.size(); i++) {
                JsonObject track = existingTracksArray.get(i).getAsJsonObject();
                existingTrackIds.add(track.get("id").getAsString());
            }
        } catch (IOException e) {
            System.err.println("Tracks.json file not found. A new file will be created.");
        }
    
        // Append new tracks if they are not already in Tracks.json
        for (Track track : tracks) {
            if (!existingTrackIds.contains(track.getId()) && !isSaved(accessToken, track)) {
                JsonObject trackJson = new JsonObject();
                trackJson.addProperty("imageUrl", track.getImage().getUrl());
                trackJson.addProperty("title", track.getTitle());
                trackJson.addProperty("artist", track.getArtist());
                trackJson.addProperty("album", track.getAlbum());
                trackJson.addProperty("albumType", track.getAlbumType());
                trackJson.addProperty("releaseDate", track.getReleaseDate());
                trackJson.addProperty("duration", track.getDuration());
                trackJson.addProperty("uri", track.getUri());
                trackJson.addProperty("id", track.getId());
                trackJson.addProperty("popularity", track.getPopularity());
                trackJson.addProperty("previewUrl", track.getPreviewUrl());
                existingTracksArray.add(trackJson);
            }
        }
    
        // Write the updated tracks to Tracks.json
        try (FileWriter writer = new FileWriter("src/Tracks.json")) {
            writer.write(existingTracksArray.toString());
        }
    }
    
    

    private static List<Track> getTopTracks(String accessToken) throws IOException {
        HttpGet get = new HttpGet("https://api.spotify.com/v1/me/top/tracks?limit=20");
        get.setHeader("Authorization", "Bearer " + accessToken);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            System.err.println("Failed to fetch top artists. HTTP Status Code: " + statusCode);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.err.println("Response Body: " + responseBody);
            return new ArrayList<>();
        }

        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

        // Check if the "items" field is present
        if (!jsonResponse.has("items")) {
            System.err.println("No artists found in the response.");
            return new ArrayList<>();
        }

        JsonArray tracks = jsonResponse.getAsJsonArray("items");
        List<Track> trackList = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            JsonObject track = tracks.get(i).getAsJsonObject();
            JsonObject album = track.getAsJsonObject("album");
            JsonArray images = album.getAsJsonArray("images");
            String imageUrl = images.size() > 0 ? images.get(0).getAsJsonObject().get("url").getAsString() : "";
            ImageObject image = new ImageObject(50,50,imageUrl);
            String title = track.get("name").getAsString();
            String artist = track.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString();
            String albumName = album.get("name").getAsString();
            String albumType = album.get("album_type").getAsString();
            String releaseDate = album.get("release_date").getAsString();
            int duration = track.get("duration_ms").getAsInt();
            String uri = track.get("uri").getAsString();
            String id = track.get("id").getAsString();
            int popularity = track.get("popularity").getAsInt();
            String previewUrl = track.has("preview_url") && !track.get("preview_url").isJsonNull() ? track.get("preview_url").getAsString() : "";

            Track newTrack = new Track(image, title, artist, albumName, albumType, releaseDate, duration, uri, id, popularity, previewUrl);
            trackList.add(newTrack);
            System.out.println("Track: " + title + " by " + artist);
        }
        return trackList;
    }
}


        private static List<miniTrack> getSimilarFromSeeds(String accessToken) throws IOException {
            List<Track> trackList = SpotifyApi.getTracks(accessToken, "MY_SEEDS_PLAYLIST_ID");
        
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            List<miniTrack> miniTrackList = Collections.synchronizedList(new ArrayList<>());
        
            for (Track track : trackList) {
                executorService.submit(() -> {
                    try {
                        String artist = URLEncoder.encode(track.getArtist(), StandardCharsets.UTF_8.toString());
                        String title = URLEncoder.encode(track.getTitle(), StandardCharsets.UTF_8.toString());
                        String method = "track.getsimilar";
                        String urlStr = "https://ws.audioscrobbler.com/2.0/?method=" + method +
                                        "&artist=" + artist + "&track=" + title +
                                        "&limit=10&api_key=" + API_KEY + "&format=json"; // Increased limit to filter tracks
                        URL url = new URL(urlStr);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
        
                        int responseCode = conn.getResponseCode();
                        if (responseCode == 200) {
                            // Read response
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder response2 = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response2.append(inputLine);
                            }
                            in.close();
        
                            // Parse response JSON with Gson
                            Gson gson = new Gson();
                            JsonObject jsonResponse = gson.fromJson(response2.toString(), JsonObject.class);
                            JsonArray similarTracks = jsonResponse.getAsJsonObject("similartracks").getAsJsonArray("track");
        
                            // Filter similar tracks
                            List<miniTrack> tempList = new ArrayList<>();
                            miniTrack sameArtistTrack = null;
                            miniTrack differentArtistTrack = null;
        
                            for (JsonElement trackElement : similarTracks) {
                                JsonObject trackObj = trackElement.getAsJsonObject();
                                String trackName = trackObj.get("name").getAsString();
                                String trackArtist = trackObj.getAsJsonObject("artist").get("name").getAsString();
        
                                // Check if the artist matches the original track
                                miniTrack miniTrack = new miniTrack(trackArtist, trackName);
                                if (trackArtist.equalsIgnoreCase(track.getArtist())) {
                                    if (sameArtistTrack == null) {
                                        sameArtistTrack = miniTrack;
                                    }
                                } else {
                                    if (differentArtistTrack == null) {
                                        differentArtistTrack = miniTrack;
                                    }
                                }
        
                                // Stop processing if both types are found
                                if (sameArtistTrack != null && differentArtistTrack != null) {
                                    break;
                                }
                            }
        
                            // Add at least one from each category (if available)
                            if (sameArtistTrack != null) {
                                tempList.add(sameArtistTrack);
                            }
                            if (differentArtistTrack != null) {
                                tempList.add(differentArtistTrack);
                            }
        
                            // Synchronize adding to the shared list
                            synchronized (miniTrackList) {
                                miniTrackList.addAll(tempList);
                            }
                        } else {
                            System.out.println("Error: HTTP " + responseCode);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        
            // Wait for all tasks to complete
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Tasks did not finish in time.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
            // Write the aggregated results to the JSON file
            Gson gson = new Gson();
            try (FileWriter file = new FileWriter("src/TracksBin.json")) {
                gson.toJson(miniTrackList, file);
                System.out.println("Successfully wrote all filtered responses to TracksBin.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            return miniTrackList;
        }
        

        public static List<Track> GetCustom(String accessToken) throws IOException {
            List<miniTrack> miniTrackList = getSimilarFromSeeds(accessToken);
            List<Track> trackList = new ArrayList<>();
            Set<String> existingTrackIds = new HashSet<>();
        
            for (miniTrack miniTrack : miniTrackList) {
                String artist = URLEncoder.encode(miniTrack.getArtist(), StandardCharsets.UTF_8.toString());
                String title = URLEncoder.encode(miniTrack.getName(), StandardCharsets.UTF_8.toString());
                String urlStr = "https://api.spotify.com/v1/search?q=track:" + title + "%20artist:" + artist + "&type=track&limit=1";
        
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // Read response
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
        
                    // Parse response JSON with Gson
                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                    JsonObject tracks = jsonResponse.getAsJsonObject("tracks");
                    JsonArray items = tracks.getAsJsonArray("items");
        
                    if (items.size() > 0) {
                        JsonObject track = items.get(0).getAsJsonObject();
                        JsonObject album = track.getAsJsonObject("album");
                        JsonArray images = album.getAsJsonArray("images");
                        String imageUrl = images.size() > 0 ? images.get(0).getAsJsonObject().get("url").getAsString() : "";
                        ImageObject image = new ImageObject(50, 50, imageUrl);
                        String albumName = album.get("name").getAsString();
                        String albumType = album.get("album_type").getAsString();
                        String releaseDate = album.get("release_date").getAsString();
                        int duration = track.get("duration_ms").getAsInt();
                        String uri = track.get("uri").getAsString();
                        String id = track.get("id").getAsString();
                        int popularity = track.get("popularity").getAsInt();
                        String previewUrl = track.has("preview_url") && !track.get("preview_url").isJsonNull() ? track.get("preview_url").getAsString() : "";
        
                        Track newTrack = new Track(image, miniTrack.getName(), miniTrack.getArtist(), albumName, albumType, releaseDate, duration, uri, id, popularity, previewUrl);
                        if(!existingTrackIds.contains(newTrack.getId())) {
                            existingTrackIds.add(newTrack.getId());
                            trackList.add(newTrack);
                            System.out.println("Track: " + title + " by " + artist);}
                    }
                    
                }
            }
            updateTrackFiles(trackList, accessToken);
            return trackList;
        }


        private static List<miniTrack> getSimilarFromTopTracks(String accessToken) throws IOException {
            List<Track> trackList = Recommending.getTopTracks(accessToken);
        
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            List<miniTrack> miniTrackList = Collections.synchronizedList(new ArrayList<>());
        
            for (Track track : trackList) {
                executorService.submit(() -> {
                    try {
                        String artist = URLEncoder.encode(track.getArtist(), StandardCharsets.UTF_8.toString());
                        String title = URLEncoder.encode(track.getTitle(), StandardCharsets.UTF_8.toString());
                        String method = "track.getsimilar";
                        String urlStr = "https://ws.audioscrobbler.com/2.0/?method=" + method +
                                        "&artist=" + artist + "&track=" + title +
                                        "&limit=10&api_key=" + API_KEY + "&format=json"; // Increased limit to filter tracks
                        URL url = new URL(urlStr);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
        
                        int responseCode = conn.getResponseCode();
                        if (responseCode == 200) {
                            // Read response
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder response2 = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response2.append(inputLine);
                            }
                            in.close();
        
                            // Parse response JSON with Gson
                            Gson gson = new Gson();
                            JsonObject jsonResponse = gson.fromJson(response2.toString(), JsonObject.class);
                            JsonArray similarTracks = jsonResponse.getAsJsonObject("similartracks").getAsJsonArray("track");
        
                            // Filter similar tracks
                            List<miniTrack> tempList = new ArrayList<>();
                            miniTrack sameArtistTrack = null;
                            miniTrack differentArtistTrack = null;
        
                            for (JsonElement trackElement : similarTracks) {
                                JsonObject trackObj = trackElement.getAsJsonObject();
                                String trackName = trackObj.get("name").getAsString();
                                String trackArtist = trackObj.getAsJsonObject("artist").get("name").getAsString();
        
                                // Check if the artist matches the original track
                                miniTrack miniTrack = new miniTrack(trackArtist, trackName);
                                if (trackArtist.equalsIgnoreCase(track.getArtist())) {
                                    if (sameArtistTrack == null) {
                                        sameArtistTrack = miniTrack;
                                    }
                                } else {
                                    if (differentArtistTrack == null) {
                                        differentArtistTrack = miniTrack;
                                    }
                                }
        
                                // Stop processing if both types are found
                                if (sameArtistTrack != null && differentArtistTrack != null) {
                                    break;
                                }
                            }
        
                            // Add at least one from each category (if available)
                            if (sameArtistTrack != null) {
                                tempList.add(sameArtistTrack);
                            }
                            if (differentArtistTrack != null) {
                                tempList.add(differentArtistTrack);
                            }
        
                            // Synchronize adding to the shared list
                            synchronized (miniTrackList) {
                                miniTrackList.addAll(tempList);
                            }
                        } else {
                            System.out.println("Error: HTTP " + responseCode);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        
            // Wait for all tasks to complete
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Tasks did not finish in time.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
            // Write the aggregated results to the JSON file
            Gson gson = new Gson();
            try (FileWriter file = new FileWriter("src/TracksBin.json")) {
                gson.toJson(miniTrackList, file);
                System.out.println("Successfully wrote all filtered responses to TracksBin.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            return miniTrackList;
        }

        public static List<Track> GetTargeted(String accessToken) throws IOException {
            List<miniTrack> miniTrackList = getSimilarFromTopTracks(accessToken);
            List<Track> trackList = new ArrayList<>();
            Set<String> existingTrackIds = new HashSet<>();
            for (miniTrack miniTrack : miniTrackList) {
                String artist = URLEncoder.encode(miniTrack.getArtist(), StandardCharsets.UTF_8.toString());
                String title = URLEncoder.encode(miniTrack.getName(), StandardCharsets.UTF_8.toString());
                String urlStr = "https://api.spotify.com/v1/search?q=track:" + title + "%20artist:" + artist + "&type=track&limit=1";
        
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // Read response
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
        
                    // Parse response JSON with Gson
                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                    JsonObject tracks = jsonResponse.getAsJsonObject("tracks");
                    JsonArray items = tracks.getAsJsonArray("items");
        
                    if (items.size() > 0) {
                        JsonObject track = items.get(0).getAsJsonObject();
                        JsonObject album = track.getAsJsonObject("album");
                        JsonArray images = album.getAsJsonArray("images");
                        String imageUrl = images.size() > 0 ? images.get(0).getAsJsonObject().get("url").getAsString() : "";
                        ImageObject image = new ImageObject(50, 50, imageUrl);
                        String albumName = album.get("name").getAsString();
                        String albumType = album.get("album_type").getAsString();
                        String releaseDate = album.get("release_date").getAsString();
                        int duration = track.get("duration_ms").getAsInt();
                        String uri = track.get("uri").getAsString();
                        String id = track.get("id").getAsString();
                        int popularity = track.get("popularity").getAsInt();
                        String previewUrl = track.has("preview_url") && !track.get("preview_url").isJsonNull() ? track.get("preview_url").getAsString() : "";
                        Track newTrack = new Track(image, miniTrack.getName(), miniTrack.getArtist(), albumName, albumType, releaseDate, duration, uri, id, popularity, previewUrl);
                        if(!existingTrackIds.contains(newTrack.getId())) {
                        existingTrackIds.add(newTrack.getId());
                        trackList.add(newTrack);
                        System.out.println("Track: " + title + " by " + artist);}
                    }
                    
                }
            }
            updateTrackFiles(trackList,accessToken);
            return trackList;
        }

        private static boolean isSaved(String accessToken, Track track) throws IOException {
            HttpGet get = new HttpGet("https://api.spotify.com/v1/me/tracks/contains?ids=" + track.getId());
            get.setHeader("Authorization", "Bearer " + accessToken);
    
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(get)) {
    
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    System.err.println("Failed to fetch tracks. HTTP Status Code: " + statusCode);
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.err.println("Response Body: " + responseBody);
                    return false;
                } else {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    
    
                    // Parse the JSON response
                    Gson gson = new Gson();
                    boolean[] result = gson.fromJson(responseBody, boolean[].class);
    
                    // Return the first element, as the API returns an array of booleans
                    System.out.println("Track is saved: " + track.getTitle() +" by " + track.getArtist() + " " + result[0]);
                    return result.length > 0 && result[0];
                }
            }
        }
    }



