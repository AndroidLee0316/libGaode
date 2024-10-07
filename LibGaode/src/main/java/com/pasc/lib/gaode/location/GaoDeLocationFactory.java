package com.pasc.lib.gaode.location;

import android.content.Context;

import com.pasc.lib.lbs.location.BaseLocationClient;
import com.pasc.lib.lbs.location.ILocationFactory;

public class GaoDeLocationFactory implements ILocationFactory {
  private Context context;

  public GaoDeLocationFactory(Context context) {
    this.context = context;
  }

  @Override public BaseLocationClient create(int i) {
    return new GaoDeClient(context, i);
  }
}
