/**
 * Copyright 2018 StrongJoshua (strongjoshua@hotmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.mygdx.game.gamestate.UI.console.sjconsole;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.TimeUtils;
import lombok.Getter;
import lombok.Setter;

public class LogEntry {
	@Getter @Setter
    private String text;
	@Getter
	private LogLevel level;
	private long timeStamp;

	protected LogEntry (String msg, LogLevel level) {
		this.text = msg;
		this.level = level;
		timeStamp = TimeUtils.millis();
	}

    public Color getColor () {
		return level.getColor();
	}

	protected String toConsoleString () {
		String r = "";
		if (level.equals(LogLevel.COMMAND)) {
			r += level.getIdentifier();
		}
		r += text;
		return r;
	}

	@Override public String toString () {
		return timeStamp + ": " + level.getIdentifier() + text;
	}
}
