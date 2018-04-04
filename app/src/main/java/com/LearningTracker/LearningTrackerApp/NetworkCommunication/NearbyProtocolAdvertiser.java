package com.LearningTracker.LearningTrackerApp.NetworkCommunication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by maximerichard on 03.04.18.
 */
public class NearbyProtocolAdvertiser implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView logView = null;

    private GoogleApiClient mGoogleApiClient;

    private List<String> mRemotePeerEndpoints = new ArrayList<>();

    private static final String TAG = "Nearby";

    // client's name that's visible to other devices when connecting
    public static final String CLIENT_NAME = "Teacher";

    /**
     * service id. discoverer and advertiser can use this id to
     * verify each other before connecting
     */
    public static final String SERVICE_ID = "LearningTracker";

    /**
     * The connection strategy we'll use.
     * P2P_STAR denotes there is 1 advertiser and N discoverers
     */
    public static final Strategy STRATEGY = Strategy.P2P_CLUSTER;

    public NearbyProtocolAdvertiser(Context context, TextView logView) {
        this.logView = logView;
        final TextView logViewFinal = logView;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        logViewFinal.append("\nonConnected: advertises on the network as the host");
                        startAdvertising();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        logViewFinal.append("\nonConnectionSuspended: " + i);
                        mGoogleApiClient.reconnect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        logViewFinal.append("\nonConnectionFailed: " + connectionResult);
                    }
                })
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    private void startAdvertising() {
        Nearby.Connections.startAdvertising(
                mGoogleApiClient,
                CLIENT_NAME,
                SERVICE_ID,
                mConnectionLifecycleCallback,
                new AdvertisingOptions(STRATEGY))
                .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                    @Override
                    public void onResult(Connections.StartAdvertisingResult result) {
                        logView.append("\nstartAdvertising:onResult:" + result);
                        if (result.getStatus().isSuccess()) {
                            logView.append("\nAdvertising...");
                        }
                    }
                });
    }

    @Override
    public void onConnected(Bundle bundle) {
        startAdvertising();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * These callbacks are made when other devices:
     * 1. tries to initiate a connection
     * 2. completes a connection attempt
     * 3. disconnects from the connection
     */
    private final ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            logView.append("\nonConnectionInitiated. Token: " + connectionInfo.getAuthenticationToken());
            // Automatically accept the connection on both sides"
            Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    if (payload.getType() == Payload.Type.BYTES) {
                        logView.append("\nonPayloadReceived: " + new String(payload.asBytes()));
                        Nearby.Connections.sendPayload(mGoogleApiClient, endpointId, Payload.fromBytes("ACK".getBytes(Charset.forName("UTF-8"))));
                    }
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    // Provides updates about the progress of both incoming and outgoing payloads
                }
            });
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution resolution) {
            logView.append("\nonConnectionResult");
            if (resolution.getStatus().isSuccess()) {
                if (!mRemotePeerEndpoints.contains(endpointId)) {
                    mRemotePeerEndpoints.add(endpointId);
                }
                logView.append("\nConnected! (endpointId=" + endpointId + ")");
            } else {
                logView.append("\nConnection to " + endpointId + " failed. Code: " + resolution.getStatus().getStatusCode());
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            // We've been disconnected from this endpoint. No more data can be sent or received.
            logView.append("\nonDisconnected: " + endpointId);
        }
    };
}