package koreatech.in.domain.Bus;

import lombok.Getter;

@Getter
public enum BusTerminalEnum {

    KOREATECH("koreatech", "NAI3125301"),
    TERMINAL("terminal", "NAI3112001"),
    ;

    private final String terminalName;

    private final String terminalID;

    BusTerminalEnum(String terminalName, String terminalID) {
        this.terminalName = terminalName;
        this.terminalID = terminalID;
    }

    public static BusTerminalEnum findByTerminalName(String terminalName) {
        for (BusTerminalEnum busTerminalEnum : BusTerminalEnum.values()) {
            if (busTerminalEnum.getTerminalName().equals(terminalName)) {
                return busTerminalEnum;
            }
        }
        return null;
    }

    public static BusTerminalEnum findByTerminalID(String terminalID) {
        for (BusTerminalEnum busTerminalEnum : BusTerminalEnum.values()) {
            if (busTerminalEnum.getTerminalID().equals(terminalID)) {
                return busTerminalEnum;
            }
        }
        return null;
    }
}
