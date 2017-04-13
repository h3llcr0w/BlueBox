#include <SoftwareSerial.h>
#include <DS3232RTC.h>
#include <Time.h> 
#include <Wire.h> 

SoftwareSerial mySerial(10, 11); // RX, TX
SoftwareSerial ble(5, 6); // RX, TX
int light=0;
String content="NO NEW MESSAGES";
void setup()  
{
  pinMode(13,OUTPUT);
  pinMode(3,OUTPUT);
  setSyncProvider(RTC.get); 
  Serial.begin(9600);
  mySerial.begin(9600);
  ble.begin(9600);
  clear();
}

void loop() // run over and over
{ 
  message();
  nextLine();
  digitalClockDisplay();  
  lightup();
  delay(800);
}

void message()
{
  String content_old=content;
  if(ble.available())
  { 
  content = "";
  beep();
  }
  char character;
  while(ble.available()) {
      character = ble.read();
      content.concat(character);
  }
  if(content.equalsIgnoreCase("Lights"))
  {
    light=(light==0)?230:0;
    content=content_old;
  }
  if (content != "") {
    clear();
    mySerial.print(content);
  }
  
}



void clear()
{
  mySerial.write(254);
mySerial.write(1);
delay(1);
mySerial.write(254);
mySerial.write(2);
delay(1);
}


void nextLine()
{
  mySerial.write(254);
  mySerial.write(191);
  delay(1);
}


void beep()
{
  digitalWrite(13,HIGH);
  delay(80);
  digitalWrite(13,LOW);
  delay(80);
  digitalWrite(13,HIGH);
  delay(80);
  digitalWrite(13,LOW);
  delay(80);
}


void digitalClockDisplay(void)
{
    // digital clock display of the time
    mySerial.print(hour());
    printDigits(minute());
    printDigits(second());
    mySerial.print(',');
    mySerial.print(day());
    mySerial.print('/');
    mySerial.print(month());
    mySerial.print('/');
    mySerial.print((year())%100); 
}

void printDigits(int digits)
{
    // utility function for digital clock display: prints preceding colon and leading 0
    mySerial.print(':');
    if(digits < 10)
        mySerial.print('0');
    mySerial.print(digits);
}

void lightup()
{
  analogWrite(3,light);
}
