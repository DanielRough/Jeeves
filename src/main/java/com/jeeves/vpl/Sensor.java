package com.jeeves.vpl;


//I have absolutely no idea whether this will work. 
	public class Sensor{
		private static Sensor accelSensor = new Sensor("Accelerometer","/img/icons/accelerometer.png",new String[]{"Stopped","Started","Moving","Stationary"});
		private static Sensor locSensor = new Sensor("Location","/img/icons/location.jpg",new String[]{});
		private static Sensor smsSensor = new Sensor("SMS","/img/icons/sms.jpg",new String[]{"Message Sent","Message Received"});
		public static Sensor[] sensors = new Sensor[]{accelSensor,locSensor,smsSensor};
		public Sensor(String name, String image, String[] values){
			this.name = name;
			this.image = image;
			this.values = values;
		}
		public String getimage() {
			return image;
		}
		public String getname(){
			return name;
		}
		public String[] getvalues() {
			return values;
		}
		private String name;
		private String image;
		private String[] values;
	}
