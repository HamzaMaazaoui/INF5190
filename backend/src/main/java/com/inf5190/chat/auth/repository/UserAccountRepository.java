package com.inf5190.chat.auth.repository;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

@Repository
public class UserAccountRepository {
    private static final String COLLECTION_NAME = "userAccounts";

    private final Firestore firestore;

    public UserAccountRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public FirestoreUserAccount getUserAccount(String username) throws InterruptedException, ExecutionException {
        ApiFuture<DocumentSnapshot> userAccountFuture = firestore.collection(COLLECTION_NAME).document(username).get();

        DocumentSnapshot account = userAccountFuture.get();
        if (!account.exists()) {
            return null;
        }
        return account.toObject(FirestoreUserAccount.class);
    }

    public void setUserAccount(FirestoreUserAccount userAccount) throws InterruptedException, ExecutionException {
        ApiFuture<WriteResult> userAccountFuture = firestore.collection(
                COLLECTION_NAME)
                .document(userAccount.getUsername()).set(userAccount);
        userAccountFuture.get();
    }
}
