package com.sihuatech.sensetime.demo;

import java.io.IOException;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Speaker {
	public void speak(String voiceContent, int volume, int rate) throws IOException {
		ActiveXComponent sap = new ActiveXComponent("Sapi.SpVoice");
		Dispatch sapo = sap.getObject();
		try {
			// 音量 0-100
			sap.setProperty("Volume", new Variant(volume));
			// 语音朗读速度 -10 到 +10
			sap.setProperty("Rate", new Variant(rate));

			Variant defalutVoice = sap.getProperty("Voice");

			Dispatch dispdefaultVoice = defalutVoice.toDispatch();
			Variant allVoices = Dispatch.call(sapo, "GetVoices");
			Dispatch dispVoices = allVoices.toDispatch();

			Dispatch setvoice = Dispatch.call(dispVoices, "Item", new Variant(1)).toDispatch();
			ActiveXComponent voiceActivex = new ActiveXComponent(dispdefaultVoice);
			ActiveXComponent setvoiceActivex = new ActiveXComponent(setvoice);

			Variant item = Dispatch.call(setvoiceActivex, "GetDescription");
			// 执行朗读
			Dispatch.call(sapo, "Speak", new Variant(voiceContent));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sapo.safeRelease();
			sap.safeRelease();
		}
	}
}
