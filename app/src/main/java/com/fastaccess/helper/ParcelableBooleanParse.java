package com.fastaccess.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

/**
 * Created by Kosh on 13/12/15 3:04 PM
 */
public class ParcelableBooleanParse extends SparseBooleanArray implements Parcelable {

    public static Creator<ParcelableBooleanParse> CREATOR = new Creator<ParcelableBooleanParse>() {
        @Override public ParcelableBooleanParse createFromParcel(Parcel source) {
            ParcelableBooleanParse read = new ParcelableBooleanParse();
            int size = source.readInt();
            int[] keys = new int[size];
            boolean[] values = new boolean[size];
            source.readIntArray(keys);
            source.readBooleanArray(values);
            for (int i = 0; i < size; i++) {
                read.put(keys[i], values[i]);
            }
            return read;
        }

        @Override public ParcelableBooleanParse[] newArray(int size) {
            return new ParcelableBooleanParse[size];
        }
    };

    public ParcelableBooleanParse() {

    }

    public ParcelableBooleanParse(SparseBooleanArray sparseBooleanArray) {
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.valueAt(i));
        }
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        int[] keys = new int[size()];
        boolean[] values = new boolean[size()];

        for (int i = 0; i < size(); i++) {
            keys[i] = keyAt(i);
            values[i] = valueAt(i);
        }

        dest.writeInt(size());
        dest.writeIntArray(keys);
        dest.writeBooleanArray(values);
    }
}