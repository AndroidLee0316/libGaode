package com.pasc.lib.lbs.location;

public interface ILocationFactory {
    public BaseLocationClient create(int scanSpan);
}
