package hu.okrim.trucksimulatortimer;

public abstract class TimeFormatController {
    public static String createTimeText(int seconds){
        boolean secondsAreNegative = false;
        //If the seconds are negative we invert them
        if(seconds < 0){
            seconds *= -1;
            secondsAreNegative = true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int time = seconds;
        String h = String.valueOf(time / 3600);
        time = time % 3600;
        String m = String.valueOf(time / 60);
        time = time % 60;
        String s = String.valueOf(time);
        if(!secondsAreNegative){
            stringBuilder.append(h.length() == 1 ? "0" + h : h);
        }
        else{
            stringBuilder.append(h.length() == 1 ? "-0" + h : h);
        }
        stringBuilder.append(":");
        stringBuilder.append(m.length() == 1 ? "0" + m : m);
        stringBuilder.append(":");
        stringBuilder.append(s.length() == 1 ? "0" + s : s);
        return stringBuilder.toString();
    }
}
