package com.pasc.lib.lbs.location;

import com.pasc.lib.lbs.location.bean.PascLocationData;

public interface PascLocationListener {

    public void onLocationSuccess(PascLocationData data);

    public void onLocationFailure(LocationException e);

}

