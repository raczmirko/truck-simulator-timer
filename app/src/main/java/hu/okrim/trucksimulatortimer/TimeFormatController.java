package hu.okrim.trucksimulatortimer;

public abstract class TimeFormatController {
    public static String createTimeText(int seconds){
        StringBuilder stringBuilder = new StringBuilder();
        int time = seconds;
        String h = String.valueOf(time / 3600);
        time = time % 3600;
        String m = String.valueOf(time / 60);
        time = time % 60;
        String s = String.valueOf(time);

        stringBuilder.append(h.length() == 1 ? "0" + h : h);
        stringBuilder.append(":");
        stringBuilder.append(m.length() == 1 ? "0" + m : m);
        stringBuilder.append(":");
        stringBuilder.append(s.length() == 1 ? "0" + s : s);

        return stringBuilder.toString();
    }
}
