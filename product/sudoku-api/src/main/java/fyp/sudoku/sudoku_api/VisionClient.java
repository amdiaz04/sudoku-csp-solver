package fyp.sudoku.sudoku_api;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component
public class VisionClient {

  @Value("${vision.url:http://localhost:8000}")
  private String visionUrl;

  private final RestTemplate rest = new RestTemplate();

  /** Sends the photo to the vision service and returns the 81-char puzzle string. */
  public String extractPuzzle(MultipartFile file) throws IOException {
    ByteArrayResource image = new ByteArrayResource(file.getBytes()) {
      @Override
      public String getFilename() {
        return file.getOriginalFilename();
      }
    };
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("image", image);

    try {
      return rest.postForObject(visionUrl + "/extract", new HttpEntity<>(body), JsonNode.class)
          .path("puzzle").asText();
    } catch (HttpClientErrorException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read a puzzle from the image");
    } catch (RestClientException e) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Vision service unavailable");
    }
  }
}

