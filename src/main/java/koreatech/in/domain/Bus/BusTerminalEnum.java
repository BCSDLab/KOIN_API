package koreatech.in.domain.Bus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BusTerminalEnum {

    KOREATECH(MajorStationEnum.KOREATECH, "NAI3125301"),
    TERMINAL(MajorStationEnum.TERMINAL, "NAI3112001"),
    ;

    private final MajorStationEnum terminal;

    private final String terminalID;

    public static BusTerminalEnum findByTerminalName(String terminalName) throws IllegalArgumentException {
        for (BusTerminalEnum busTerminalEnum : BusTerminalEnum.values()) {
            if (busTerminalEnum.getTerminal().getEngName().equalsIgnoreCase(terminalName)) {
                return busTerminalEnum;
            }
        }
        throw new IllegalArgumentException();
    }

    public static BusTerminalEnum findByTerminalID(String terminalID) throws IllegalArgumentException {
        for (BusTerminalEnum busTerminalEnum : BusTerminalEnum.values()) {
            if (busTerminalEnum.getTerminalID().equalsIgnoreCase(terminalID)) {
                return busTerminalEnum;
            }
        }
        throw new IllegalArgumentException();
    }
}
