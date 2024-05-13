package com.inf5190.chat.messages;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.MessageRequest;
import com.inf5190.chat.messages.repository.FirestoreMessage;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:firebase.properties")
public class ITestMessageController {
    private final FirestoreMessage message1 = new FirestoreMessage("u1", Timestamp.now(), "t1", null);
    private final FirestoreMessage message2 = new FirestoreMessage("u2", Timestamp.now(), "t2", null);

    @Value("${firebase.project.id}")
    private String firebaseProjectId;

    @Value("${firebase.emulator.port}")
    private String emulatorPort;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Firestore firestore;

    private String messagesEndpointUrl;
    private String loginEndpointUrl;

    @BeforeAll
    public static void checkRunAgainstEmulator() {
        checkEmulators();
    }

    @BeforeEach
    public void setup() throws InterruptedException, ExecutionException {
        this.messagesEndpointUrl = "http://localhost:" + port + "/messages";
        this.loginEndpointUrl = "http://localhost:" + port + "/auth/login";

        this.firestore.collection("messages").document("1")
                .create(this.message1).get();
        this.firestore.collection("messages").document("2")
                .create(this.message2).get();
    }

    @AfterEach
    public void testDown() {
        this.restTemplate.delete(
                "http://localhost:" + this.emulatorPort + "/emulator/v1/projects/"
                        + this.firebaseProjectId
                        + "/databases/(default)/documents");
    }

    @Test
    public void getMessageNotLoggedIn() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(this.messagesEndpointUrl,
                String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void postMessageNotLoggedIn() {
        ResponseEntity<String> response = this.restTemplate.postForEntity(this.messagesEndpointUrl,
                new MessageRequest("username", "text", null), String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void postMessage() {
        final String token = this.login();

        final HttpEntity<MessageRequest> request = this
                .createRequestWithAuthHeader(new MessageRequest("username", "text", null), token);
        final ResponseEntity<Message> response = this.restTemplate.postForEntity(this.messagesEndpointUrl,
                request, Message.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        final Message actualMessage = response.getBody();
        assertThat(actualMessage.id()).isNotEmpty();
        assertThat(actualMessage.username()).isEqualTo("username");
        assertThat(actualMessage.text()).isEqualTo("text");
        assertThat(actualMessage.imageUrl()).isNull();
    }

    @Test
    public void getMessages() {
        final String token = this.login();

        final HttpHeaders header = this.createHeadersWithAuthHeader(token);
        final HttpEntity<Object> headers = new HttpEntity<Object>(header);
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(this.messagesEndpointUrl,
                HttpMethod.GET, headers, Message[].class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        final Message[] actualMessages = response.getBody();
        assertThat(actualMessages.length).isEqualTo(2);

        final Message m1 = actualMessages[0];
        assertThat(m1.id()).isEqualTo("1");
        assertThat(m1.username()).isEqualTo(this.message1.getUsername());
        assertThat(m1.text()).isEqualTo(this.message1.getText());
        assertThat(m1.imageUrl()).isNull();

        final Message m2 = actualMessages[1];
        assertThat(m2.id()).isEqualTo("2");
        assertThat(m2.username()).isEqualTo(this.message2.getUsername());
        assertThat(m2.text()).isEqualTo(this.message2.getText());
        assertThat(m2.imageUrl()).isNull();
    }

    @Test
    public void getMessagesWithMoreThanTwentyMessages() throws InterruptedException, ExecutionException {
        for (int i = 11; i <= 30; i++) {
            this.firestore.collection("messages").document(Integer.toString(i))
                    .create(new FirestoreMessage("u" + i, Timestamp.now(), "t" + i, null)).get();
        }

        final String token = this.login();

        final HttpHeaders header = this.createHeadersWithAuthHeader(token);
        final HttpEntity<Object> headers = new HttpEntity<Object>(header);
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(this.messagesEndpointUrl,
                HttpMethod.GET, headers, Message[].class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        final Message[] actualMessages = response.getBody();
        assertThat(actualMessages.length).isEqualTo(20);

        final Message m1 = actualMessages[0];
        assertThat(m1.id()).isEqualTo("11");

        final Message m2 = actualMessages[actualMessages.length - 1];
        assertThat(m2.id()).isEqualTo("30");

    }

    @Test
    public void getMessagesWithFromId() {
        final String token = this.login();

        final HttpHeaders header = this.createHeadersWithAuthHeader(token);
        final HttpEntity<Object> headers = new HttpEntity<Object>(header);
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(
                this.messagesEndpointUrl + "?fromId=1",
                HttpMethod.GET, headers, Message[].class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        final Message[] actualMessages = response.getBody();
        assertThat(actualMessages.length).isEqualTo(1);

        final Message m1 = actualMessages[0];
        assertThat(m1.id()).isEqualTo("2");
        assertThat(m1.username()).isEqualTo(this.message2.getUsername());
        assertThat(m1.text()).isEqualTo(this.message2.getText());
        assertThat(m1.imageUrl()).isNull();
    }

    @Test
    public void getMessagesWithFromIdInvalid() {
        final String token = this.login();

        final HttpHeaders header = this.createHeadersWithAuthHeader(token);
        final HttpEntity<Object> headers = new HttpEntity<Object>(header);
        final ResponseEntity<String> response = this.restTemplate.exchange(
                this.messagesEndpointUrl + "?fromId=AAA",
                HttpMethod.GET, headers, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    private String login() {
        LoginResponse response = this.restTemplate.postForObject(this.loginEndpointUrl,
                new LoginRequest("username", "password"),
                LoginResponse.class);

        return response.token();
    }

    private HttpEntity<MessageRequest> createRequestWithAuthHeader(MessageRequest messageRequest, String token) {
        HttpHeaders header = this.createHeadersWithAuthHeader(token);
        return new HttpEntity<MessageRequest>(
                messageRequest,
                header);
    }

    private HttpHeaders createHeadersWithAuthHeader(String token) {
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + token);
        return header;
    }

    private static void checkEmulators() {
        final String firebaseEmulator = System.getenv().get("FIRESTORE_EMULATOR_HOST");
        if (firebaseEmulator == null || firebaseEmulator.length() == 0) {
            System.err.println(
                    "**********************************************************************************************************");
            System.err.println(
                    "******** You need to set FIRESTORE_EMULATOR_HOST=localhost:8181 in your system properties. ********");
            System.err.println(
                    "**********************************************************************************************************");
        }
        assertThat(firebaseEmulator).as(
                "You need to set FIRESTORE_EMULATOR_HOST=localhost:8181 in your system properties.")
                .isNotEmpty();
        final String storageEmulator = System.getenv().get("FIREBASE_STORAGE_EMULATOR_HOST");
        if (storageEmulator == null || storageEmulator.length() == 0) {
            System.err.println(
                    "**********************************************************************************************************");
            System.err.println(
                    "******** You need to set FIREBASE_STORAGE_EMULATOR_HOST=localhost:9199 in your system properties. ********");
            System.err.println(
                    "**********************************************************************************************************");
        }
        assertThat(storageEmulator).as(
                "You need to set FIREBASE_STORAGE_EMULATOR_HOST=localhost:9199 in your system properties.")
                .isNotEmpty();
    }
}
