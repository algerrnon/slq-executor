package com.testme.webservices.messages;

public class ErrorMessage {
	public ErrorMessage(String message) {
		this.message = message;
	}

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ErrorMessage that = (ErrorMessage) o;

		return message != null ? message.equals(that.message) : that.message == null;
	}

	@Override
	public int hashCode() {
		return message != null ? message.hashCode() : 0;
	}
}
