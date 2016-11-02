package se.inera.statistikapi.web.rest.exception;

public class TimeFormatException extends RuntimeException {
	private static final long serialVersionUID = -8794628258112019369L;

	private String timeFormatErrorMessage;
	
	public TimeFormatException(String timeFormatErrorMessage) {
		this.timeFormatErrorMessage = timeFormatErrorMessage;
	}

	public String getErrorMessage() {
		return timeFormatErrorMessage;
	}
}
