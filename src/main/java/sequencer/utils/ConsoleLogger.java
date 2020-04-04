package sequencer.utils;

public class ConsoleLogger implements Logger {

    @Override
    public void log(byte[] component, String s) {
        System.out.println(new String(component) + ": " + s);
    }

    @Override
    public void log(byte[] component, String s1, String s2) {
        System.out.println(new String(component) + ": " + s1 + " " + s2);
    }
}
