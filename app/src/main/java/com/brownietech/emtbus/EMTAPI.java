package com.brownietech.emtbus;

import android.location.Location;
import android.util.Log;

import org.glob3.mobile.generated.MarksRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EMTAPI {

    public static final String USER_EMT = "WEB.SERV.mdelacalle@glob3mobile.com";
    public static final String PASSWORD_EMT = "379F1DA7-D1D1-4004-B8CB-BED610EBB5FC";

    public static void renderCloseStops(MarksRenderer _busStopsRenderer, Location location){

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openbus.emtmadrid.es:9443/emt-proxy-server/last/geo/GetStopsFromXY.php?").newBuilder();
       // HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openbus.emtmadrid.es:9443/emt-proxy-server/last/geo/GetStopsFromStop.php?").newBuilder();

        String url = urlBuilder.build().toString();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idClient", USER_EMT)
                .addFormDataPart("passKey", PASSWORD_EMT)
              //  .addFormDataPart("idStop", "2242")
                .addFormDataPart("latitude", String.valueOf(location.getLatitude()))
                .addFormDataPart("longitude", String.valueOf(location.getLongitude()))
                .addFormDataPart("radius", "1000")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {

                    // do something wih the result
                    Log.e("***", "reSPONSE:" + response.body().string());
                }
            }
      });
    }

    public static void getArrivesFromStop(String idStop, final EMTListener listener ){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openbus.emtmadrid.es:9443/emt-proxy-server/last/geo/GetArriveStop.php?").newBuilder();
        String url = urlBuilder.build().toString();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idClient", USER_EMT)
                .addFormDataPart("passKey", PASSWORD_EMT)
                .addFormDataPart("idStop", idStop)
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                String responseSt ="";

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                     try {
                        ArrayList<Arrive> arrives = new ArrayList<>();

                        responseSt = response.body().string();
                         Log.e("***", "RESPONSE:"+ responseSt);

                        JSONObject jsonObject = new JSONObject(responseSt);

                        if(jsonObject.get("arrives") instanceof JSONArray){
                            JSONArray arrivesJsonArray = jsonObject.getJSONArray("arrives");

                            for (int i = 0; i < arrivesJsonArray.length(); i++) {
                                JSONObject arriveJSON = arrivesJsonArray.getJSONObject(i);
                                arrives.add(new Arrive(
                                        arriveJSON.getString("stopId"),
                                        arriveJSON.getString("lineId"),
                                        arriveJSON.getString("destination"),
                                        arriveJSON.getString("busId"),
                                        arriveJSON.getInt("busTimeLeft")
                                ));
                            }
                        }else{
                            JSONObject arriveJSON = jsonObject.getJSONObject("arrives");
                            arrives.add(new Arrive(
                                    arriveJSON.getString("stopId"),
                                    arriveJSON.getString("lineId"),
                                    arriveJSON.getString("destination"),
                                    arriveJSON.getString("busId"),
                                    arriveJSON.getInt("busTimeLeft")
                            ));
                        }
                        listener.onSuccess(arrives);

                    } catch (JSONException e) {
                         Log.e("***", "ERROR PARSING THE RESPONSE:"+ responseSt);
                         Log.e("***", e.toString());

                         listener.onError("No hay información sobre autobuses para esta parada");
                    }


                }
            }

        });
    }

    public static void testEMTAPI(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openbus.emtmadrid.es:9443/emt-proxy-server/last/geo/GetArriveStop.php?").newBuilder();
        String url = urlBuilder.build().toString();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idClient", USER_EMT)
                .addFormDataPart("passKey", PASSWORD_EMT)
                .addFormDataPart("idStop", "2281")
                .build();



        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {

                    // do something wih the result
                    Log.e("***", "reSPONSE:" + response.body().string());
                }
            }


        });
    }


}
