package cat.v1.r;

import com.github.rcaller.rstuff.*;
import org.apache.commons.lang.time.StopWatch;

import javax.script.ScriptException;

public class RConnectionProvider {
    //create only one service as rService instead of normal constructor
    private static RService rService = new RService();

    public double[] execute(String RCodeScript, String toReturn) throws ScriptException {
        rService.getRCode().addRCode(RCodeScript);
        synchronized (toReturn) {
            rService.getRCaller().runAndReturnResultOnline(toReturn);
            rService.getRCode().clearOnline();
        }
        return rService.getRCaller().getParser().getAsDoubleArray(toReturn);
    }

    public void terminate() {
        rService.getRCaller().deleteTempFiles();
        rService.getRCaller().StopRCallerOnline();
    }
}