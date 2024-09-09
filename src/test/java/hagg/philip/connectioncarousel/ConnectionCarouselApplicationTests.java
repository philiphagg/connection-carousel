package hagg.philip.connectioncarousel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hagg.philip.connectioncarousel.domain.HttpResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.nimbusds.jose.util.IOUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = ConnectionCarouselApplication.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("integration-test")
class ConnectionCarouselApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    private static final String EXPECTED_FOLDER = "src/test/resources/expected/";
    private static final boolean GENERATE_TEST_DATA = false;

    @Test
    void assertNotBuildGeneratingTestData() {
        assertFalse(GENERATE_TEST_DATA);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void verifyResponse() {
        var actual = webTestClient.get()
                .uri("/api/load-balance?path=/some-path")
                .exchange()
                .expectStatus().isOk()
                .expectBody(HttpResponse.class)
                .returnResult()
                .getResponseBody();

        var expected = "Hello from http://www.instance.two.io";


        assertEquals(expected, actual.getBody());
    }

    @Test
    void verifyJsonResponse() {
        var actual = webTestClient.get()
                .uri("/api/load-balance?path=/some-path")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        var expected = "Hello from http://www.instance.two.io";

        assertPayloadEquals(actual);
    }

    @SneakyThrows
    private void assertPayloadEquals(String payload) {
        String fileName = EXPECTED_FOLDER + "test" + ".json";
        System.out.println("fileName = " + fileName);;
        if (GENERATE_TEST_DATA) {
            writeToFile(Path.of(fileName), payload);
        } else {
            JSONAssert.assertEquals(
                    readFileToString(new File(fileName), StandardCharsets.UTF_8),
                    payload, true);
        }
    }

    private static void writeToFile(Path path, String payload) throws IOException {
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(payload);

        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(path.toFile(), actualObj);
    }

}
