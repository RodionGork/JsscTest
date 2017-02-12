long lineLengthCounter = 0;

void setup() {
  Serial.begin(9600);
}

void loop() {
  int dataByte = Serial.read();
  if (dataByte < 0) {
    return;
  }
  if (dataByte != '\r') {
    lineLengthCounter += 1;
  } else {
    Serial.print(lineLengthCounter);
    Serial.print("\r");
    lineLengthCounter = 0;
  }
}

