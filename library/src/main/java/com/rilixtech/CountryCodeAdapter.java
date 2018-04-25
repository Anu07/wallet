package com.rilixtech;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import com.rilixtech.CountryCodeDialog.ItemRecyclerViewClickListener;

/**
 * Created by hbb20 on 11/1/16. item
 *
 * Move all code unrelated with RecyclerView item to parent dialog.
 * Updated by joielechong on 6 June 2017
 */
class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder> {

  private List<Country> mCountries;
  private CountryCodePicker mCountryCodePicker;
  private ItemRecyclerViewClickListener mListener;

  CountryCodeAdapter(List<Country> countries, CountryCodePicker codePicker, ItemRecyclerViewClickListener listener) {
    this.mCountries = countries;
    this.mCountryCodePicker = codePicker;
    this.mListener = listener;
  }

  @Override public CountryCodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    View rootView = inflater.inflate(R.layout.layout_recycler_country_tile, viewGroup, false);
    return new CountryCodeViewHolder(rootView);
  }

  @Override public void onBindViewHolder(CountryCodeViewHolder viewHolder, final int i) {
    final int position = viewHolder.getAdapterPosition();
    viewHolder.setCountry(mCountries.get(position));
    viewHolder.rlyMain.setOnClickListener(mListener);
  }

  @Override public int getItemCount() {
    return mCountries.size();
  }

  class CountryCodeViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout rlyMain;
    AppCompatTextView tvName, tvCode;
    AppCompatImageView imvFlag;
    LinearLayout llyFlagHolder;
    View viewDivider;

    CountryCodeViewHolder(View itemView) {
      super(itemView);
      rlyMain = (RelativeLayout) itemView;
      tvName = rlyMain.findViewById(R.id.country_name_tv);
      tvCode = rlyMain.findViewById(R.id.code_tv);
      imvFlag = rlyMain.findViewById(R.id.flag_imv);
      llyFlagHolder = rlyMain.findViewById(R.id.flag_holder_lly);
      viewDivider = rlyMain.findViewById(R.id.preference_divider_view);
    }

    private void setCountry(Country country) {
      if (country != null) {
        viewDivider.setVisibility(View.GONE);
        tvName.setVisibility(View.VISIBLE);
        tvCode.setVisibility(View.VISIBLE);
        llyFlagHolder.setVisibility(View.VISIBLE);
        String countryNameAndCode = tvName.getContext()
            .getString(R.string.country_name_and_code, country.getName(),
                country.getIso().toUpperCase());
        tvName.setText(countryNameAndCode);
        if (!mCountryCodePicker.isHidePhoneCode()) {
          tvCode.setText(
              tvCode.getContext().getString(R.string.phone_code, country.getPhoneCode()));
        } else {
          tvCode.setVisibility(View.GONE);
        }
        if (mCountryCodePicker.getTypeFace() != null) {
          tvCode.setTypeface(mCountryCodePicker.getTypeFace());
          tvName.setTypeface(mCountryCodePicker.getTypeFace());
        }
        imvFlag.setImageResource(CountryUtils.getFlagDrawableResId(country));

        if (mCountryCodePicker.getDialogTextColor() != mCountryCodePicker.getDefaultContentColor()) {
          int color = mCountryCodePicker.getDialogTextColor();
          tvCode.setTextColor(color);
          tvName.setTextColor(color);
        }
      } else {
        viewDivider.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.GONE);
        tvCode.setVisibility(View.GONE);
        llyFlagHolder.setVisibility(View.GONE);
      }
    }
  }
}

