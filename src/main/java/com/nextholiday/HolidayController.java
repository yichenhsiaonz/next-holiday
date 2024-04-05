package com.nextholiday;

import java.net.*;
import java.util.Locale;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.json.*;
import java.util.ArrayList;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HolidayController {

    @FXML Label freedomLabel;
    @FXML Button freedomButton;
    @FXML Button nextButton;
    @FXML Button previousButton;

    private LocalDate currentDate;
    private int index = 0;
    private int length;
    private ArrayList<String> holidays;
    private Locale locale;
    private String country;

    @FXML
    private void initialize() {
        locale = Locale.getDefault();
        country = locale.getCountry();
        System.out.println(country);
        currentDate = LocalDate.now();
        nextButton.setDisable(true);
        previousButton.setDisable(true);
    }

    @FXML
    private void getFreedom() {
        String url = "https://date.nager.at/Api/v2/NextPublicHolidays/" + country;
        try {
            URI uri = new URI(url);
            URL urlObj = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String response = new String(connection.getInputStream().readAllBytes());
                setFreedom(response);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setFreedom(String response) {
        String freedom = response;
        JSONArray jsonArray = new JSONArray(freedom);
        length = jsonArray.length();
        holidays = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            // get local county code



            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("localName");
            String date = jsonObject.getString("date");
            String note = "";
            if(jsonObject.get("counties").getClass() == JSONArray.class) { 
                JSONArray counties = jsonObject.getJSONArray("counties");
                StringBuilder sb = new StringBuilder();
                sb.append(" in ");
                for (int j = 0; j < counties.length(); j++) {
                    sb.append(counties.getString(j));
                    if(j < counties.length() - 1){
                        sb.append(", ");
                    }
                }
                if(counties.length() > 1){
                    sb.append(" counties");
                } else {
                    sb.append(" county");
                }
                note = sb.toString();
            }
            LocalDate nextHoliday = LocalDate.parse(date);
            long daysUntil = ChronoUnit.DAYS.between(currentDate, nextHoliday);
            holidays.add(name + " on " + date + " in " + daysUntil + " days" + note);
        }
        
        Platform.runLater(() -> {
            index = 0;
            freedomLabel.setText(holidays.get(0));
            previousButton.setDisable(true);
            if(length > 1) {
                nextButton.setDisable(false);
            } else {
                nextButton.setDisable(true);
            }
        });
    }

    @FXML
    private void onNextButton() {
        index++;
        freedomLabel.setText(holidays.get(index));
        if (index == length - 1) {
            nextButton.setDisable(true);
        }
        if (index > 0) {
            previousButton.setDisable(false);
        }
    }

    @FXML
    private void onPreviousButton() {
        index--;
        freedomLabel.setText(holidays.get(index));
        if (index == 0) {
            previousButton.setDisable(true);
        }

        if (index < length - 1) {
            nextButton.setDisable(false);
        }
    }

}
