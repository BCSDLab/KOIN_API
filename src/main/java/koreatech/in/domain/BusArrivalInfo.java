package koreatech.in.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BusArrivalInfo {
    private Integer arrprevstationcnt;
    private Integer arrtime;
    private String nodeid;
    private String nodenm;
    private String routeid;
    private Integer routeno;
    private String routetp;
    private String vehicletp;

    public Integer getArrprevstationcnt() {
        return arrprevstationcnt;
    }

    public void setArrprevstationcnt(Integer arrprevstationcnt) {
        this.arrprevstationcnt = arrprevstationcnt;
    }

    public Integer getArrtime() {
        return arrtime;
    }

    public void setArrtime(Integer arrtime) {
        this.arrtime = arrtime;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getNodenm() {
        return nodenm;
    }

    public void setNodenm(String nodenm) {
        this.nodenm = nodenm;
    }

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public Integer getRouteno() {
        return routeno;
    }

    public void setRouteno(Integer routeno) {
        this.routeno = routeno;
    }

    public String getRoutetp() {
        return routetp;
    }

    public void setRoutetp(String routetp) {
        this.routetp = routetp;
    }

    public String getVehicletp() {
        return vehicletp;
    }

    public void setVehicletp(String vehicletp) {
        this.vehicletp = vehicletp;
    }

    public String[] cast () {
        return new String[] {
                "arrprevstationcnt",
                "arrtime",
                "routeno", };
    }

    public String[] reflection () {
        List<String> fieldsList = new ArrayList<String>();
        Class cls = this.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field: fields) {
            fieldsList.add(field.getName());
        }
        int size = fieldsList.size();
        return fieldsList.toArray(new String[size]);
    }
}
