package com.tobelinker.greenrobot.eventbus.gradle;

import java.util.Objects;

public class SubscriberMethod {
    public String targetEventType;
    private String method;
    private String targetMethod;
    private String eventType;
    private boolean handled;

    public SubscriberMethod() {
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getTargetEventType() {
        return targetEventType;
    }

    public void setTargetEventType(String targetEventType) {
        this.targetEventType = targetEventType;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubscriberMethod that = (SubscriberMethod) o;
        return Objects.equals(targetEventType, that.targetEventType) &&
                Objects.equals(method, that.method) &&
                Objects.equals(targetMethod, that.targetMethod) &&
                Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetEventType, method, targetMethod, eventType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SubscriberMethod{");
        sb.append("targetEventType='").append(targetEventType).append('\'');
        sb.append(", method='").append(method).append('\'');
        sb.append(", targetMethod='").append(targetMethod).append('\'');
        sb.append(", eventType='").append(eventType).append('\'');
        sb.append(", handled=").append(handled);
        sb.append('}');
        return sb.toString();
    }
}
