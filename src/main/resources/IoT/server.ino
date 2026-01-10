#include <dummy.h>
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

#define LDR_RL 10000   // 10k resistor used in LDR divider
#define LDR_PIN 34

#define SOIL_PIN 32
#define SOIL_POWER 25

#define SENSOR_ID_LIGHT "LGHT00DC3D95"
#define SENSOR_ID_HUM   "HMDT00E9656F"
#define SENSOR_ID_TEMP  "TEMP00BD730A"
#define SENSOR_ID_SOIL  "SOIL00BD730A"

DHT dht(DHTPIN, DHTTYPE);
WiFiClientSecure client;
HTTPClient http;

void setup() {
  Serial.begin(115200);
  dht.begin();

  pinMode(SOIL_POWER, OUTPUT);
  digitalWrite(SOIL_POWER, LOW);
  analogSetPinAttenuation(SOIL_PIN, ADC_11db);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) delay(500);

  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
  delay(2000);

  client.setInsecure();
}

void loop() {

  float temp = dht.readTemperature();
  float hum  = dht.readHumidity();
  if (isnan(temp) || isnan(hum)) return;

  int lightRaw = analogRead(LDR_PIN);
  float lightPercent = 100.0 - ((lightRaw / 4095.0) * 100.0);

  // ---------- Light Lux --------------
  float voltage = (4095 - lightRaw) * (3.3 / 4095.0);


  // Prevent divide by zero
  if (voltage < 0.05) voltage = 0.05;
  if (voltage > 3.25) voltage = 3.25;

  float ldrResistance = (3.3 * LDR_RL / voltage) - LDR_RL;

  // Safe lux calculation
  float lux = 1250.0 / pow(ldrResistance / 1000.0, 1.4);

  // Cap lux to realistic range
  if (lux > 20000) lux = 20000;

  // ---------- Soil Moisture ----------
  digitalWrite(SOIL_POWER, HIGH);
  delay(300);
  int soilRaw = analogRead(SOIL_PIN);
  digitalWrite(SOIL_POWER, LOW);

  int soilPercent = map(soilRaw, 4095, 1500, 0, 100);
  soilPercent = constrain(soilPercent, 0, 100);

  unsigned long long nowMs =
      (unsigned long long) time(nullptr) * 1000ULL;

  String url = String(FIREBASE_DB_URL) + "/live/esp32_1.json?auth=" + FIREBASE_DB_SECRET;

  http.begin(client, url);
  http.addHeader("Content-Type", "application/json");

  String payload = "{";
  payload += "\"sensorIdTemp\":\"" + String(SENSOR_ID_TEMP) + "\",";
  payload += "\"sensorIdHum\":\"" + String(SENSOR_ID_HUM) + "\",";
  payload += "\"sensorIdSoil\":\"" + String(SENSOR_ID_SOIL) + "\",";
  payload += "\"sensorIdLight\":\"" + String(SENSOR_ID_LIGHT) + "\",";
  payload += "\"temperature\":" + String(temp,1) + ",";
  payload += "\"humidity\":" + String(hum,1) + ",";
  payload += "\"lightRaw\":" + String(lightRaw) + ",";
  payload += "\"lightPercent\":" + String(lightPercent,1) + ",";
  payload += "\"lightLux\":" + String(lux,1) + ",";
  payload += "\"soilRaw\":" + String(soilRaw) + ",";
  payload += "\"soilPercent\":" + String(soilPercent) + ",";
  payload += "\"timestamp\":" + String(nowMs);
  payload += "}";

  http.PATCH(payload);
  http.end();

  delay(5000);
}
