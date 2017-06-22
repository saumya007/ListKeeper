package com.example.saumyamehta.listkeeper.beans;

//import io.realm.RealmObject;     extends RealmObject

/**
 * Created by saumyamehta on 6/22/17.
 */

public class Drops {
    public Drops() {
    }

    public Drops(String what, long added, long when, boolean completed) {
        this.what = what;
        this.added = added;
        this.when = when;
        this.completed = completed;
    }

    private String what;
    private long added;
    private long when;
    private boolean completed;

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
