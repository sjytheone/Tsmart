package com.sjy.bushelper;

public class Eventenum {
	public enum EventEn{
		ROUTE_SHOWORHIDETOOLBAR(100),
		SHOW_FUNCTIONDISPLAYFRAGMENT(102),
		THEME_CHANGED(103);

		private final int value;
		EventEn(int value){
			this.value = value;
		}
		public int getValue(){
			return value;
		}
	};
	
}
