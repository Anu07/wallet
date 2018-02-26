package app.src.com.walletapp.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anu Bhalla on 25/02/18.
 */

public class Country implements Parcelable {

    String countryName;
   /* String countryCode;
    String countryIso;
    String countryIso3;
*/
    public Country(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.countryName);
    }

    protected Country(Parcel in) {
        this.countryName = in.readString();
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
