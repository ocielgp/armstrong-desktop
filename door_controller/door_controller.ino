// led rgb common anode cathode
const byte pinLedR = 11;
const byte pinLedG = 10;
const byte pinLedB = 9;
const byte pinRelay = 8; // normally opened
const byte pinButton = 7;

boolean isButtonPressed = true;
unsigned long doorOpenedMillis = millis();
long interval = 0;

void setup() {
  Serial.begin(9600);

  pinMode(pinLedR, OUTPUT);
  pinMode(pinLedG, OUTPUT);
  pinMode(pinLedB, OUTPUT);

  pinMode(pinRelay, OUTPUT);
  pinMode(pinButton, INPUT_PULLUP);
}

void loop() {
  if (digitalRead(pinButton) == HIGH) { // open door
    ledGreen();
    // isButtonPressed = true;
    startTimer(4000);
  } else {
    /* if (isButtonPressed) {
      ledOff();
      isButtonPressed = false;
    } */

    if (interval > 0 && (millis() - doorOpenedMillis >= interval)) {
      ledOff();
    }

    if (Serial.available() > 0) {
      String data = Serial.readString();
      // Serial.print("I received: " + data);

      if (data.equals("GREEN")) {
        ledGreen();
      } else if (data.equals("YELLOW")) {
        ledYellow();
      } else if (data.equals("RED")) {
        ledRed();
      } else if (data.equals("PURPLE")) {
        ledPurple();
      }
    }
  }
}

/**
   milliseconds: milliseconds to turn led off
**/
void startTimer(long milliseconds) {
  interval = milliseconds;
  doorOpenedMillis = millis();
}

void ledGreen() {
  analogWrite(pinLedR, 0);
  analogWrite(pinLedG, 255);
  analogWrite(pinLedB, 0);
  digitalWrite(pinRelay, HIGH);
  startTimer(4000);
}

void ledYellow() {
  analogWrite(pinLedR, 255);
  analogWrite(pinLedG, 255);
  analogWrite(pinLedB, 0);
  digitalWrite(pinRelay, HIGH);
  startTimer(4000);
}

void ledRed() {
  analogWrite(pinLedR, 255);
  analogWrite(pinLedG, 0);
  analogWrite(pinLedB, 0);
  digitalWrite(pinRelay, LOW);
  startTimer(2000);
}

void ledPurple() {
  analogWrite(pinLedR, 128);
  analogWrite(pinLedG, 0);
  analogWrite(pinLedB, 128);
  digitalWrite(pinRelay, LOW);
  startTimer(2000);
}

void ledOff() {
  interval = 0;
  analogWrite(pinLedR, 0);
  analogWrite(pinLedG, 0);
  analogWrite(pinLedB, 0);
  digitalWrite(pinRelay, LOW);
}
