package sequencer.utils;

public class ConsoleLogger implements Logger {

    @Override
    public void info(byte[] component, String s) {
        log(Logger.INFO, component, s);
    }

    @Override
    public void info(byte[] component, String s1, String s2) {
        log(Logger.INFO, component, s1, s2);
    }

    @Override
    public void info(byte[] component, String s1, String s2, String s3) {
        log(Logger.INFO, component, s1+s2+s3);
    }

    @Override
    public void warn(byte[] component, String s) {
        log(Logger.WARN, component, s);
    }

    @Override
    public void warn(byte[] component, String s1, String s2) {
        log(Logger.WARN, component, s1, s2);
    }

    @Override
    public void error(byte[] component, String s) {
        log(Logger.ERROR, component, s);
    }

    @Override
    public void error(byte[] component, String s1, String s2) {
        log(Logger.ERROR, component, s1, s2);
    }

    @Override
    public void log(byte[] level, byte[] component, String s) {
        System.out.println(new String(level) + ": " + new String(component) + ": " + s);
    }

    @Override
    public void log(byte[] level, byte[] component, String s1, String s2) {
        log(level, component, s1+s2);
    }
}
