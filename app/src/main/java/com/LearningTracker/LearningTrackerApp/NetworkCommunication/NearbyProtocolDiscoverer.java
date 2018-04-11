package com.LearningTracker.LearningTrackerApp.NetworkCommunication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.LearningTracker.LearningTrackerApp.Activities.CorrectedQuestionActivity;
import com.LearningTracker.LearningTrackerApp.DataConversion;
import com.LearningTracker.LearningTrackerApp.Questions.QuestionMultipleChoice;
import com.LearningTracker.LearningTrackerApp.Questions.QuestionShortAnswer;
import com.LearningTracker.LearningTrackerApp.R;
import com.LearningTracker.LearningTrackerApp.database_management.DbTableIndividualQuestionForResult;
import com.LearningTracker.LearningTrackerApp.database_management.DbTableQuestionMultipleChoice;
import com.LearningTracker.LearningTrackerApp.database_management.DbTableQuestionShortAnswer;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by maximerichard on 03.04.18.
 */
public class NearbyProtocolDiscoverer {
    private static final String TAG = "Nearby";
    private final String defaultFileName = "defaultFileName";
    private TextView logView;
    private Context context = null;
    private String currentFileName = "";
    private String pendingFileName = "";
    private WifiCommunication wifiCommunication = null;
    private final SimpleArrayMap<Long, Payload> incomingPayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();

    /**
     * service id. discoverer and advertiser can use this id to
     * verify each other before connecting
     */
    public static final String SERVICE_ID = "LearningTracker";

    private GoogleApiClient mGoogleApiClient;
    private String mRemoteHostEndpoint;
    private boolean mIsConnected;

    public NearbyProtocolDiscoverer(Context context, final TextView logView, WifiCommunication wifiCommunication) {
        this.context = context;
        this.logView = logView;
        this.wifiCommunication = wifiCommunication;
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
        Log.v(TAG, "onStart: connect");
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
                                                if (payload.getType() == Payload.Type.BYTES) {          //ok to receive text up to 3500 characters
                                                    String textReceived = new String(payload.asBytes());
                                                    Log.v(TAG, "onPayloadReceived: " + textReceived);
                                                    if (textReceived.split("///")[0].contains("MULTQ")) {
                                                        QuestionMultipleChoice questionMultipleChoice = DataConversion.textToQuestionMultipleChoice(textReceived);
                                                        addPayloadFilename(questionMultipleChoice.getIMAGE());
                                                        try {
                                                            DbTableQuestionMultipleChoice.addMultipleChoiceQuestion(questionMultipleChoice);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else if (textReceived.split("///")[0].contains("SHRTA")) {
                                                        QuestionShortAnswer questionShortAnswer = DataConversion.textToQuestionShortAnswere(textReceived);
                                                        addPayloadFilename(questionShortAnswer.getIMAGE());
                                                        try {
                                                            DbTableQuestionShortAnswer.addShortAnswerQuestion(questionShortAnswer);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else if (textReceived.split("///")[0].contains("QID")) {
                                                        int id_global = Integer.valueOf(textReceived.split("///")[1]);
                                                        QuestionMultipleChoice questionMultipleChoice = DbTableQuestionMultipleChoice.getQuestionWithId(id_global);
                                                        if (questionMultipleChoice.getQUESTION().length() > 0) {
                                                            questionMultipleChoice.setID(id_global);
                                                            wifiCommunication.launchMultChoiceQuestionActivity(questionMultipleChoice);
                                                        } else {
                                                            QuestionShortAnswer questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(id_global);
                                                            questionShortAnswer.setID(id_global);
                                                            wifiCommunication.launchShortAnswerQuestionActivity(questionShortAnswer);
                                                        }
                                                    } else if (textReceived.split("///")[0].contains("EVAL")) {
                                                        DbTableIndividualQuestionForResult.addIndividualQuestionForStudentResult(textReceived.split("///")[2],textReceived.split("///")[1]);
                                                    } else if (textReceived.split("///")[0].contains("UPDEV")) {
                                                        DbTableIndividualQuestionForResult.setEvalForQuestionAndStudentIDs(Double.valueOf(textReceived.split("///")[1]),textReceived.split("///")[2]);
                                                    } else if (textReceived.split("///")[0].contains("CORR")) {
                                                        Intent mIntent = new Intent(context, CorrectedQuestionActivity.class);
                                                        Bundle bun = new Bundle();
                                                        bun.putString("questionID", textReceived.split("///")[1]);
                                                        mIntent.putExtras(bun);
                                                        context.startActivity(mIntent);
                                                    }
                                                } else if (payload.getType() == Payload.Type.FILE) {
                                                    // Add this to our tracking map, so that we can retrieve the payload later.
                                                    incomingPayloads.put(payload.getId(), payload);
                                                }
                                            }

                                            /**
                                             * Extracts the payloadId and filename from the message and stores it in the
                                             * filePayloadFilenames map. The format is payloadId:filename.
                                             */
                                            private void addPayloadFilename(String payloadFilenameMessage) {
                                                int colonIndex = payloadFilenameMessage.indexOf(":");
                                                if (colonIndex >= 0) {
                                                    String payloadId = payloadFilenameMessage.substring(0, colonIndex);
                                                    String filename = payloadFilenameMessage.substring(colonIndex + 1);
                                                    Long longPayloadId = Long.parseLong(payloadId);
                                                    filePayloadFilenames.put(longPayloadId, filename);
                                                    String direc = context.getFilesDir().getAbsolutePath();
                                                    File directory = new File(context.getFilesDir(),"images");
                                                    File file = new File(directory,defaultFileName);
                                                    if (file.exists()) {
                                                        file.renameTo(new File(file.getParentFile(), filename));
                                                    }
                                                }
                                            }


                                            @Override
                                            public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                                                // Provides updates about the progress of both incoming and outgoing payloads
                                                if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                                                    Long payloadId = update.getPayloadId();
                                                    Payload payload = incomingPayloads.remove(payloadId);
                                                    if (payload != null && payload.getType() == Payload.Type.FILE) {
                                                        // Retrieve the filename that was received in a bytes payload.
                                                        String newFilename = filePayloadFilenames.remove(payloadId);

                                                        File payloadFile = payload.asFile().asJavaFile();

                                                        // Rename the file.
                                                        File directory = new File(context.getFilesDir(),"images");
                                                        if (!directory.exists()) {
                                                            directory.mkdirs();
                                                        }
                                                        if (payloadFile == null) {
                                                            Log.v(TAG, "problem receiving file: file == null when trying to rename it");
                                                        } else if (newFilename == null)  {
                                                            File file = new File(directory,defaultFileName);
                                                            payloadFile.renameTo(file);
                                                            Log.v(TAG, "problem receiving file: fileName == null when trying to rename it");
                                                        } else {
                                                            File file = new File(directory,newFilename);
                                                            moveFile(payloadFile.getAbsolutePath(),file.getAbsolutePath());
                                                            Log.v(TAG, "file: " + file.getAbsolutePath() + " renaming success: " + file.exists());
                                                        }
                                                    }
                                                }
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
        Log.v(TAG, "About to send message: " + message);
        Nearby.Connections.sendPayload(mGoogleApiClient, mRemoteHostEndpoint, Payload.fromBytes(message.getBytes(Charset.forName("UTF-8"))));
    }

    public void sendData(InputStream dataStream) {
        Log.v(TAG, "trying to send data through stream");
        Nearby.Connections.sendPayload(mGoogleApiClient, mRemoteHostEndpoint, Payload.fromStream(dataStream));
    }

    public void stopNearby() {
        if (mGoogleApiClient.isConnected()) {
            if (!mIsConnected || TextUtils.isEmpty(mRemoteHostEndpoint)) {
                Nearby.Connections.stopDiscovery(mGoogleApiClient);
                return;
            }
            sendMessage("Client disconnecting");
            Nearby.Connections.disconnectFromEndpoint(mGoogleApiClient, mRemoteHostEndpoint);
            mRemoteHostEndpoint = null;
            mIsConnected = false;

            mGoogleApiClient.disconnect();
        }
    }

    private void moveFile(String inputFile, String outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

}
