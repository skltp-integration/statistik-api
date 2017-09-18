package se.inera.statistikapi.web.rest;


import se.inera.statistikapi.web.rest.exception.TimeFormatException;

public class TimeValidator {

    private static final String TIME_ALLOWED_REGEX = "([0-9]+[s|m|h|d|w])";
    private static final int MAX_WEEKS = 22;
    private static final int MAX_DAYS = MAX_WEEKS*14;
    private static final int MAX_HOURS = MAX_DAYS*24;
    private static final int MAX_MINUTES = MAX_HOURS*60;
    private static final int MAX_SECONDS = MAX_MINUTES*60;

    private static final String TIME_WRONG_FORMAT_ERROR_MESSAGE = "Time har ett falaktigt format.";
    private static final String TIME_EXCEEDS_MAX_ERROR_MESSAGE = "Time får max vara " + MAX_WEEKS + " veckor bakåt i tiden.";


    private TimeValidator() {

    }

    public static void validateTime(String time) {
        if(time == null) {
            return;
        }
        time = time.toLowerCase();

        if(!time.matches(TIME_ALLOWED_REGEX)) {
            throw new TimeFormatException(TIME_WRONG_FORMAT_ERROR_MESSAGE);
        }

        if(time.endsWith("s") && getTimeNumberPart(time) > MAX_SECONDS) {
            throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
        }

        if(time.endsWith("m") && getTimeNumberPart(time) > MAX_MINUTES) {
            throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
        }

        if(time.endsWith("h") && getTimeNumberPart(time) > MAX_HOURS) {
            throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
        }

        if(time.endsWith("d") && getTimeNumberPart(time) > MAX_DAYS) {
            throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
        }

        if(time.endsWith("w") && getTimeNumberPart(time) > MAX_WEEKS) {
            throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
        }
    }

    private static int getTimeNumberPart(String time) {
        return Integer.parseInt(time.substring(0, time.length()-1));
    }
}
