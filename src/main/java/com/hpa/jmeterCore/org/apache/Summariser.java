//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hpa.jmeterCore.org.apache;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.jmeter.control.TransactionController;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.samplers.Remoteable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterContextService.ThreadCounts;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.util.JOrphanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Summariser extends AbstractTestElement implements Serializable, SampleListener, TestStateListener, NoThreadClone, Remoteable {
    private static final long serialVersionUID = 234L;
    private static final Logger log = LoggerFactory.getLogger(Summariser.class);
    private static final long INTERVAL = (long)JMeterUtils.getPropDefault("summariser.interval", 30);
    private static final boolean TOLOG = JMeterUtils.getPropDefault("summariser.log", true);
    private static final boolean TOOUT = JMeterUtils.getPropDefault("summariser.out", true);
    private static final boolean IGNORE_TC_GENERATED_SAMPLERESULT = JMeterUtils.getPropDefault("summariser.ignore_transaction_controller_sample_result", true);
    private static final int INTERVAL_WINDOW = 5;
    private static final Object LOCK = new Object();
    private static final Map<String, Summariser.Totals> ACCUMULATORS = new ConcurrentHashMap();
    private static int instanceCount;
    private transient Summariser.Totals myTotals;
    private transient String myName;

    public Summariser() {
        this.myTotals = null;
        synchronized(LOCK) {
            ACCUMULATORS.clear();
            instanceCount = 0;
        }
    }

    public Summariser(String name) {
        this();
        this.setName(name);
    }

    public void sampleOccurred(SampleEvent e) {
        SampleResult s = e.getResult();
        if (!IGNORE_TC_GENERATED_SAMPLERESULT || !TransactionController.isFromTransactionController(s)) {
            long now = System.currentTimeMillis() / 1000L;
            SummariserRunningSample myDelta = null;
            SummariserRunningSample myTotal = null;
            boolean reportNow = false;
            synchronized(this.myTotals) {
                if (s != null) {
                    this.myTotals.delta.addSample(s);
                }

                if (now > this.myTotals.last + 5L && now % INTERVAL <= 5L) {
                    reportNow = true;
                    myDelta = new SummariserRunningSample(this.myTotals.delta);
                    this.myTotals.moveDelta();
                    myTotal = new SummariserRunningSample(this.myTotals.total);
                    this.myTotals.last = now;
                }
            }

            if (reportNow) {
                this.formatAndWriteToLog(this.myName, myDelta, "+");
                if (myTotal != null && myDelta != null && myTotal.getNumSamples() != myDelta.getNumSamples()) {
                    this.formatAndWriteToLog(this.myName, myTotal, "=");
                }
            }

        }
    }

    private static StringBuilder longToSb(StringBuilder sb, long l, int len) {
        sb.setLength(0);
        sb.append(l);
        return JOrphanUtils.rightAlign(sb, len);
    }

    private static StringBuilder doubleToSb(DecimalFormat dfDouble, StringBuilder sb, double d, int len, int frac) {
        sb.setLength(0);
        dfDouble.setMinimumFractionDigits(frac);
        dfDouble.setMaximumFractionDigits(frac);
        sb.append(dfDouble.format(d));
        return JOrphanUtils.rightAlign(sb, len);
    }

    public void sampleStarted(SampleEvent e) {
    }

    public void sampleStopped(SampleEvent e) {
    }

    public void testStarted() {
        this.testStarted("local");
    }

    public void testEnded() {
        this.testEnded("local");
    }

    public void testStarted(String host) {
        synchronized(LOCK) {
            this.myName = this.getName();
            this.myTotals = (Summariser.Totals)ACCUMULATORS.get(this.myName);
            if (this.myTotals == null) {
                this.myTotals = new Summariser.Totals();
                ACCUMULATORS.put(this.myName, this.myTotals);
            }

            ++instanceCount;
        }
    }

    public void testEnded(String host) {
        Set<Entry<String, Summariser.Totals>> totals = null;
        synchronized(LOCK) {
            --instanceCount;
            if (instanceCount <= 0) {
                totals = ACCUMULATORS.entrySet();
            }
        }

        if (totals != null) {
            Iterator var3 = totals.iterator();

            while(var3.hasNext()) {
                Entry<String, Summariser.Totals> entry = (Entry)var3.next();
                String name = (String)entry.getKey();
                Summariser.Totals total = (Summariser.Totals)entry.getValue();
                total.delta.setEndTime();
                if (total.delta.getNumSamples() > 0L && total.total.getNumSamples() > 0L) {
                    this.formatAndWriteToLog(name, total.delta, "+");
                }

                total.moveDelta();
                this.formatAndWriteToLog(name, total.total, "=");
            }

        }
    }

    private void formatAndWriteToLog(String name, SummariserRunningSample summariserRunningSample, String type) {
        if (TOOUT || TOLOG && log.isInfoEnabled()) {
            String formattedMessage = format(name, summariserRunningSample, type);
            if (TOLOG) {
                log.info(formattedMessage);
            }

            if (TOOUT) {
                System.out.println(formattedMessage);
            }
        }

    }

    private static String format(String name, SummariserRunningSample summariserRunningSample, String type) {
        DecimalFormat dfDouble = new DecimalFormat("#0.0");
        StringBuilder tmp = new StringBuilder(20);
        StringBuilder sb = new StringBuilder(140);
        sb.append(name);
        sb.append(' ');
        sb.append(type);
        sb.append(' ');
        sb.append(longToSb(tmp, summariserRunningSample.getNumSamples(), 6));
        sb.append(" in ");
        long elapsed = summariserRunningSample.getElapsed();
        long elapsedSec = (elapsed + 500L) / 1000L;
        sb.append(JOrphanUtils.formatDuration(elapsedSec));
        sb.append(" = ");
        if (elapsed > 0L) {
            sb.append(doubleToSb(dfDouble, tmp, summariserRunningSample.getRate(), 6, 1));
        } else {
            sb.append("******");
        }

        sb.append("/s Avg: ");
        sb.append(longToSb(tmp, summariserRunningSample.getAverage(), 5));
        sb.append(" Min: ");
        sb.append(longToSb(tmp, summariserRunningSample.getMin(), 5));
        sb.append(" Max: ");
        sb.append(longToSb(tmp, summariserRunningSample.getMax(), 5));
        sb.append(" Err: ");
        sb.append(longToSb(tmp, summariserRunningSample.getErrorCount(), 5));
        sb.append(" (");
        sb.append(summariserRunningSample.getErrorPercentageString());
        sb.append(')');
        if ("+".equals(type)) {
            ThreadCounts tc = JMeterContextService.getThreadCounts();
            sb.append(" Active: ");
            sb.append(tc.activeThreads);
            sb.append(" Started: ");
            sb.append(tc.startedThreads);
            sb.append(" Finished: ");
            sb.append(tc.finishedThreads);
        }

        return sb.toString();
    }

    private static class Totals {
        private long last;
        private final SummariserRunningSample delta;
        private final SummariserRunningSample total;

        private Totals() {
            this.last = 0L;
            this.delta = new SummariserRunningSample("DELTA");
            this.total = new SummariserRunningSample("TOTAL");
        }

        private void moveDelta() {
            this.total.addSample(this.delta);
            this.delta.clear();
        }
    }
}
