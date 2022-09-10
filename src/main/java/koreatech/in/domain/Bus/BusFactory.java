package koreatech.in.domain.Bus;

import koreatech.in.util.BeanUtil;

public class BusFactory {
    public static Bus createBus(String busType) {
        Bus bus = null;
        switch (busType) {
            case "express":
                bus = (ExpressBus) BeanUtil.getBean("expressBus");
                break;
            case "school":
                bus = (SchoolBus) BeanUtil.getBean("schoolBus");
                break;
            case "city":
                bus = (CityBus) BeanUtil.getBean("cityBus");
                break;
        }
        return bus;
    }
}
