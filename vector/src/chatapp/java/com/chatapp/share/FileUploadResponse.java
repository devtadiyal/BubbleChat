package com.chatapp.share;

import com.google.gson.annotations.SerializedName;

public class FileUploadResponse {

	@SerializedName("result")
	private String result;

	@SerializedName("message")
	private String message;

	public String getResult(){
		return result;
	}

	public String getMessage(){
		return message;
	}
}