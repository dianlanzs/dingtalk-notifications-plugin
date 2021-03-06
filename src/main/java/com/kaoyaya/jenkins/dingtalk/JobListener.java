package com.kaoyaya.jenkins.dingtalk;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;

@Extension
public class JobListener extends RunListener<AbstractBuild> {


    public JobListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onStarted(AbstractBuild r, TaskListener listener) {
        getService(r, listener).start();
    }

    @Override
    public void onCompleted(AbstractBuild r, @Nonnull TaskListener listener) {
        Result result = r.getResult();
        if (null != result && result.equals(Result.SUCCESS)) {
            getService(r, listener).success();
        } else if (null != result && result.equals(Result.FAILURE)) {
            getService(r, listener).failed();
            // } else if (null != result && result.equals(Result.ABORTED)) {
        } else {
            getService(r, listener).abort();
        }
    }

    private DingdingService getService(AbstractBuild build, TaskListener listener) {
        Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof Notifier) {
                return ((Notifier) publisher).newDingdingService(build, listener);
            }
        }
        return null;
    }
}
