package com.ihc.readpal;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {

    private String description;
    private int color;

    public Note(int color, String description) {
        this.color = color;
        this.description = description;
    }

    protected Note(Parcel in) {
        description = in.readString();
        color = in.readInt();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(color);
    }
}
