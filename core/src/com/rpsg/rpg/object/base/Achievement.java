package com.rpsg.rpg.object.base;

public class Achievement extends BaseTask{
	
	public static String fileName = "achievement.es";

	public static Achievement fromJSON(int value) {
		return fromJSON(value, new Achievement());
	}

	public boolean canEnd() {
		return end.test();
	}
	
}
