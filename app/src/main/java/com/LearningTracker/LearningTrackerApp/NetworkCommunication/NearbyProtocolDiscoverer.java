package com.LearningTracker.LearningTrackerApp.NetworkCommunication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.LearningTracker.LearningTrackerApp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.Charset;

/**
 * Created by maximerichard on 03.04.18.
 */
public class NearbyProtocolDiscoverer {
    private static final String TAG = "Nearby";
    private TextView logView;
    private Context context = null;
    /**
     * service id. discoverer and advertiser can use this id to
     * verify each other before connecting
     */
    public static final String SERVICE_ID = "LearningTracker";

    private GoogleApiClient mGoogleApiClient;
    private String mRemoteHostEndpoint;
    private boolean mIsConnected;

    public NearbyProtocolDiscoverer(Context context, final TextView logView) {

        this.logView = logView;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        logView.append("onConnected: start discovering hosts to send connection requests");
                        startDiscovery();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        logView.append("onConnectionSuspended: " + i);
                        // Try to re-connect
                        mGoogleApiClient.reconnect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        logView.append("onConnectionFailed: " + connectionResult.getErrorCode());
                    }
                })
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    public void connect() {
        logView.append("onStart: connect");
        mGoogleApiClient.connect();
    }

    private void startDiscovery() {
        logView.append("startDiscovery");

        Nearby.Connections.startDiscovery(mGoogleApiClient, SERVICE_ID, new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                        logView.append("onEndpointFound:" + endpointId + ":" + info.getEndpointName());

                        Nearby.Connections
                                .requestConnection(mGoogleApiClient, null, endpointId, new ConnectionLifecycleCallback() {
                                    @Override
                                    public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                                        logView.append("onConnectionInitiated. Token: " + connectionInfo.getAuthenticationToken());
                                        // Automatically accept the connection on both sides"
                                        Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, new PayloadCallback() {
                                            @Override
                                            public void onPayloadReceived(String endpointId, Payload payload) {
                                                if (payload.getType() == Payload.Type.BYTES) {
                                                    logView.append("onPayloadReceived: " + new String(payload.asBytes()));
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
                                        logView.append("onConnectionResult:" + endpointId + ":" + resolution.getStatus());
                                        if (resolution.getStatus().isSuccess()) {
                                            logView.append("Connected successfully");
                                            Nearby.Connections.stopDiscovery(mGoogleApiClient);
                                            mRemoteHostEndpoint = endpointId;
                                            mIsConnected = true;
                                        } else {
                                            if (resolution.getStatus().getStatusCode() == ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED) {
                                                logView.append("The connection was rejected by one or both sides");
                                            } else {
                                                logView.append("Connection to " + endpointId + " failed. Code: " + resolution.getStatus().getStatusCode());
                                            }
                                            mIsConnected = false;
                                        }
                                    }

                                    @Override
                                    public void onDisconnected(String endpointId) {
                                        // We've been disconnected from this endpoint. No more data can be sent or received.
                                        logView.setText("onDisconnected: " + endpointId);
                                    }
                                })
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        if (status.isSuccess()) {
                                            // We successfully requested a connection. Now both sides
                                            // must accept before the connection is established.
                                        } else {
                                            // Nearby Connections failed to request the connection.
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onEndpointLost(String endpointId) {
                        // An endpoint that was previously available for connection is no longer.
                        // It may have stopped advertising, gone out of range, or lost connectivity.
                        logView.append("onEndpointLost:" + endpointId);
                    }
                },
                new DiscoveryOptions(Strategy.P2P_CLUSTER)
        )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            logView.append("Discovering...");
                        } else {
                            logView.append("Discovering failed: " + status.getStatusMessage() + "(" + status.getStatusCode() + ")");
                        }
                    }
                });
    }

    private void sendMessage(String message) {
        logView.append("About to send message: " + message);
        Nearby.Connections.sendPayload(mGoogleApiClient, mRemoteHostEndpoint, Payload.fromBytes(message.getBytes(Charset.forName("UTF-8"))));
    }


}
