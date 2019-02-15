package com.example.Gateway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("people")
public class GatewayController {

    @GetMapping(path = "/reservation/names", produces = MediaType.APPLICATION_JSON_VALUE)
        //вернуть бронь со списком зарезервированных мест
    public String getReservationPeople() throws IOException, JSONException {
        String url = "http://localhost:8090/reservations";
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
        List<String> temp = new ArrayList<>();

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());

        JSONArray jsonArray = new JSONArray();

        try{
            jsonArray = jsonObject.getJSONArray("content");
        }catch (JSONException e){
            e.printStackTrace();
            // here write your code to get value from Json object which is not getJSONArray.
        }


        String result = new String();

        for (int i = 0; i < jsonArray.length(); i++) {
            //get the JSON Object
            JSONObject obj = jsonArray.getJSONObject(i);
            String sfname = obj.getString("personID");
            temp.add(sfname);
            //result += result + sfname + "\n";

        }

        System.out.println();

        // RestTemplate
        // Jackson / gson
        for (String a: temp) {
            url = "http://localhost:8070/people/" + a;
            website = new URL(url);
            connection = website.openConnection();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            //System.out.println("HEREEEE:    " + response.toString() + "   <<< HEREEE");

            jsonObject = new JSONObject(response.toString());

            result += response + "\n";

        }

        System.out.print(result);

        return result;

    }

    @DeleteMapping("/delete/{reservationid}")
    //удалить заказ
    void deleteOrder(@PathVariable Long reservationid) throws IOException, JSONException {
        List<Long> temp = getCarsFromReservation(reservationid);

        for (Long i: temp) {
            //System.out.println("HERE I AM");
            URL url = new URL("http://localhost:8060/cars/" + i + "/false");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("Resource content");
            out.close();
            httpCon.getInputStream();
        }
    }

    @PostMapping(path = "/reservation/{id}/add/{car}", produces = MediaType.APPLICATION_JSON_VALUE)
    //добавить место к заказу
    public String addCarToReservation(@PathVariable Long id, @PathVariable Long car) throws IOException, JSONException{

        // Получить значение: Занято ли это место?

        System.out.println(car);

        String url = "http://localhost:8060/cars/" + car;

        System.out.println(url);

        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
        List<String> temp = new ArrayList<>();

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());

        Boolean isCarTaken = jsonObject.getBoolean("taken");

        //System.out.println("IS THE CAR TAKEN? >>>> " + isCarTaken);

        if (isCarTaken == false){
            //System.out.println("HERE I AM");
            website = new URL("http://localhost:8060/cars/" + car + "/true");
            HttpURLConnection httpCon = (HttpURLConnection) website.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("Resource content");
            out.close();
            httpCon.getInputStream();

            //System.out.println("HERE");

            website = new URL("http://localhost:8090/reservations/" + id + "/car/" + car);
            httpCon = (HttpURLConnection) website.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("Resource content");
            out.close();
            httpCon.getInputStream();

        }

        //System.out.println(response.toString());

        // Если не занято, то добавить в бронь по id место*/

        return "lol";

    }


    private List<Long> getCarsFromReservation(Long reservationID) throws IOException, JSONException{

        //System.out.println("THE ID>>>    " + reservationID + "   <<< HEREEE");

        String url = "http://localhost:8090/reservations/" + reservationID;
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
        List<Long> cars = new ArrayList<>();

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());

        JSONArray jsonArray = jsonObject.getJSONArray("carsIDs");

        System.out.println("##### HEREEEE:    [" + response.toString() + "]   <<< HEREEE");

        for (int i = 0; i < jsonArray.length(); i++) {

            cars.add(jsonArray.getLong(i));

        }
        return cars;
    }


}
