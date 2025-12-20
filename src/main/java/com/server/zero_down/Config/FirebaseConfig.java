package com.server.zero_down.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("C:/firebase/zero-down-4d3b1-firebase-adminsdk-fbsvc-20dc7bf67c.json");
            // 👆 replace with your actual path

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(
                            "https://zero-down-4d3b1-default-rtdb.asia-southeast1.firebasedatabase.app"
                    )
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println(" Firebase Admin initialized");

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}

