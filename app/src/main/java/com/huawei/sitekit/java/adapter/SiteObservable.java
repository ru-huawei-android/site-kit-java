package com.huawei.sitekit.java.adapter;

import com.huawei.hms.site.api.model.Site;

public class SiteObservable {

    private String siteId;
    private String name;
    private String location;
    private String coordinates;
    private String poiTypes;

    public SiteObservable(String siteId, String name, String location, String coordinates, String poiTypes) {
        this.siteId = siteId;
        this.name = name;
        this.location = location;
        this.coordinates = coordinates;
        this.poiTypes = poiTypes;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPoiTypes() {
        return poiTypes;
    }

    public void setPoiTypes(String poiTypes) {
        this.poiTypes = poiTypes;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public static SiteObservable fromSite(Site site) {
        StringBuilder builderPoi = new StringBuilder();
        for (String poi : site.getPoi().getHwPoiTypes()) {
            builderPoi.append(poi).append(" ");
        }
        String coordinated = "Lat b: " + site.getLocation().getLat()
                + " Lng: " + site.getLocation().getLng();
        return new SiteObservable(
                site.getSiteId(), site.getName(), site.getFormatAddress(),
                coordinated, builderPoi.toString()
        );
    }
}
