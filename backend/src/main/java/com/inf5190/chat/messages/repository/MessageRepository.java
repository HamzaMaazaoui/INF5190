package com.inf5190.chat.messages.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Bucket.BlobTargetOption;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.firebase.cloud.StorageClient;
import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.MessageRequest;

import io.jsonwebtoken.io.Decoders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRepository {
    private static final String COLLECTION_NAME = "messages";
    @Autowired
    @Qualifier("storageBucketName")
    private String storageBucketName;


    private final Firestore firestore;
    private final StorageClient storageClient;

    public MessageRepository(Firestore firestore, StorageClient storageClient) {
        this.firestore = firestore;
        this.storageClient = storageClient;
    }

    public List<Message> getMessages(Optional<String> fromId) throws InterruptedException, ExecutionException {
        Query query = this.firestore.collection(COLLECTION_NAME)
                .orderBy("timestamp");

        if (fromId.isPresent()) {
            final DocumentSnapshot snapshot = this.firestore.collection(COLLECTION_NAME)
                    .document(fromId.get()).get()
                    .get();
            if (!snapshot.exists()) {
                throw new DocumentNotFoundException("Document with id " + fromId.get() + " not found.");
            }
            query = query.startAfter(snapshot);
        } else {
            query = query.limitToLast(20);
        }

        final ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();

        return querySnapshotFuture.get().getDocuments().stream().map(doc -> this.toMessage(doc))
                .collect(Collectors.toList());
    }

    public Message createMessage(MessageRequest messageRequest) throws InterruptedException, ExecutionException {
        Timestamp ts = Timestamp.now();

        DocumentReference ref = this.firestore.collection(COLLECTION_NAME).document();

        String imageUrl = null;
        if (messageRequest.imageData() != null) {
            Bucket b = this.storageClient.bucket(storageBucketName);
            String path = String.format("images/%s.%s", ref.getId(), messageRequest.imageData().type());
            b.create(path, Decoders.BASE64.decode(messageRequest.imageData().data()),
                    BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ));
            imageUrl = String.format("https://storage.googleapis.com/%s/%s", storageBucketName, path);
        }

        final FirestoreMessage firestoreMessage = new FirestoreMessage(messageRequest.username(), ts,
                messageRequest.text(), imageUrl);
        ref.create(firestoreMessage).get();

        final Message newMessage = new Message(ref.getId(), messageRequest.username(), ts.toDate().getTime(),
                messageRequest.text(), imageUrl);
        return newMessage;
    }

    private Message toMessage(QueryDocumentSnapshot snapshot) {
        final FirestoreMessage firebaseMessage = snapshot.toObject(FirestoreMessage.class);
        return new Message(snapshot.getId(), firebaseMessage.getUsername(),
                firebaseMessage.getTimestamp().toDate().getTime(), firebaseMessage.getText(),
                firebaseMessage.getImageUrl());
    }

}
