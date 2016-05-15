package net.vaemendis.hccd;

import java.util.List;

public interface UserConfiguration {

    int getGridColNumber();

    int getGridRowNumber();

    boolean useExcelFormat();

    char getDelimiter();

    List<Integer> getCardFilter();
}
