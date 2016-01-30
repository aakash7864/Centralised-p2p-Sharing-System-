package org.cs550.commands;

public enum MessageCommand {
	SERVER_RESP_REGISTRY_SUCCESS_MSG("REGISTERED ! NOW YOU CAN SEARCH IN INDEX SERVER"), SERVER_RESP_CONN_ESTABLISHED(
			"YOUR CONNECTION IS EDTABLISHED. PLEASE REGISTER"), SERVER_RESP_REGISTRY_NOT_SUCCESS("THERE IS SOME PROBLEM WHILE REGISTING. PLEASE TRY AGIAN"), SERVER_RESP_FILE_UPDATE_SUCCESS(
			"ALL FILES ARE UPDATED SUCESSFULLY");
	private String message;

	private MessageCommand(String msg) {
		this.message = msg;
	}

	public String getMessage() {
		return this.message;
	}

}
