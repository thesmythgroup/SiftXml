package org.tsg.siftxml;

import java.util.ArrayList;
import java.util.List;

class Tracker {

    List<String> tracker = new ArrayList<String>();

    public void trim(int depth) {
        while (depth < tracker.size()) {
            tracker.remove(depth);
        }
    }

    public String track(int depth, String name) {
        trim(depth);
        if (depth > tracker.size()) {
            tracker.add(name);
        } else {
            tracker.set(depth, name);
        }
        return toString();
    }

    @Override
    public String toString() {
        String msg = "";
        for (String s : tracker) {
            msg += s + " > ";
        }
        if (!"".equals(msg)) {
            msg = msg.substring(0, msg.length() - 3);
        }
        return msg;
    }
}