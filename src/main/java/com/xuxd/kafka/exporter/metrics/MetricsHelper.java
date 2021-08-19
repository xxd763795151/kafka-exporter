package com.xuxd.kafka.exporter.metrics;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 11:03:54
 **/
public class MetricsHelper {

    public static final ConcurrentHashMap<String, String> METRICS_DESCRIPTION_CACHE = new ConcurrentHashMap<>();

    public static void updateLabelValue(String[] labels, String label, String value) {
        if (labels == null || labels.length == 0) {
            return;
        }
        int length = labels.length;
        for (int i = 0; i < length; i++) {
            if (label.equals(labels[i]) && ++i < length) {
                labels[i] = value;
                return;
            }
        }
    }

    public static String[] copyLabels(String[] labels) {
        Preconditions.checkArgument(labels != null && labels.length != 0, "label is null");
        String[] copy = new String[labels.length];
        System.arraycopy(labels, 0, copy, 0, labels.length);

        return copy;
    }

    public static String uniqueID(String name, String[] labels) {
        Preconditions.checkNotNull(labels);
        return new StringBuilder(name).append("#").append(Arrays.asList(labels)).toString();
    }

}
