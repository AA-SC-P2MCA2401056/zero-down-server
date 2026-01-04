#include <dummy.h>

#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <HTTPClient.h>
#include <time.h>
#include "DHT.h"

#define WIFI_SSID "Railwire"
#define WIFI_PASSWORD "parvathy123"

#define FIREBASE_DB_URL "https://zero-down-4d3b1-default-rtdb.asia-southeast1.firebasedatabase.app"
#define FIREBASE_DB_SECRET "S6EjzsfVIXIO9iBqZwZtp9vSVDjr0jxtK3yFybUG"

#define DHTPIN 21
#define DHTTYPE DHT11
#define LDR_PIN 34
#define ONE_WIRE_BUS 19 // DS18B20 DATA pin


#define SENSOR_ID_LIGHT "LGHT00DC3D95"
#define SENSOR_ID_HUM "HMDT00E9656F"
#define SENSOR_ID_TEMP "TEMP00BD730A"
#define SENSOR_ID_SOIL "SOIL00BD730A"


DHT dht(DHTPIN, DHTTYPE);
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature ds18b20(&oneWire);


WiFiClientSecure client;
HTTPClient http;

void setup() {
  Serial.begin(115200);
  dht.begin();
  ds18b20.begin();

  //  Connect Wi-Fi FIRST
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");

  //  Sync time AFTER Wi-Fi
  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
  delay(2000);

  //  HTTPS
  client.setInsecure();
}

void loop() {
  float temp = dht.readTemperature();
  float hum  = dht.readHumidity();

  ds18b20.requestTemperatures();
  float temp1 = ds18b20.getTempCByIndex(0);   // DS18B20

  if (isnan(temp) || isnan(hum)) {
    Serial.println("DHT read failed");
    delay(5000);
    return;
  }

  int lightRaw = analogRead(LDR_PIN);
  float lightPercent = 100.0 - ((lightRaw / 4095.0) * 100.0);

  //  Proper Unix timestamp in milliseconds
  unsigned long long nowMs =
      (unsigned long long) time(nullptr) * 1000ULL;

  String url = String(FIREBASE_DB_URL) +
               "/live/esp32_1.json?auth=" +
               FIREBASE_DB_SECRET;

  http.begin(client, url);
  http.addHeader("Content-Type", "application/json");

  String payload = "{";
  payload += "\"sensorIdTemp\":\"" + String(SENSOR_ID_TEMP) + "\",";
  payload += "\"sensorIdHum\":\"" + String(SENSOR_ID_HUM) + "\",";
  payload += "\"sensorIdSoil\":\"" + String(SENSOR_ID_SOIL) + "\",";
  payload += "\"sensorIdLight\":\"" + String(SENSOR_ID_LIGHT) + "\",";
  payload += "\"temperature\":" + String(temp, 1) + ",";
  payload += "\"soil\":" + String(temp1, 1) + ",";
  payload += "\"humidity\":" + String(hum, 1) + ",";
  payload += "\"lightRaw\":" + String(lightRaw) + ",";
  payload += "\"lightPercent\":" + String(lightPercent, 1) + ",";
  payload += "\"timestamp\":" + String(nowMs);
  payload += "}";

  int httpResponseCode = http.PATCH(payload);

  Serial.print("HTTP Response: ");
  Serial.println(httpResponseCode);

  http.end();
  delay(5000);
}
