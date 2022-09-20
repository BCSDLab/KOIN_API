package koreatech.in.domain.Bus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BusTerminalEnum {

    KOREATECH("koreatech", "NAI3125301"),
    TERMINAL("terminal", "NAI3112001"),
    ;

    private final String terminalName;

    private final String terminalID;

    public static BusTerminalEnum findByTerminalName(String terminalName) {
        for (BusTerminalEnum busTerminalEnum : BusTerminalEnum.values()) {
            if (busTerminalEnum.getTerminalName().equalsIgnoreCase(terminalName)) {
                return busTerminalEnum;
            }
        }
        return null;
    }

    public static BusTerminalEnum findByTerminalID(String terminalID) {
        for (BusTerminalEnum busTerminalEnum : BusTerminalEnum.values()) {
            if (busTerminalEnum.getTerminalID().equalsIgnoreCase(terminalID)) {
                return busTerminalEnum;
            }
        }
        return null;
    }
}
