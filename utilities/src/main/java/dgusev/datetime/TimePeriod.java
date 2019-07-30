package dgusev.datetime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;

import java.util.Date;

/** Hierarchical time period. List of such periods can be stored in DB.  */

@ToString
@CommonsLog
@EqualsAndHashCode
public class TimePeriod {

    @Getter private final long           id;
    @Getter private final String         title;
    @Getter private final long           parentId;
    @Getter private final Date           startDate;
    @Getter private final TimePeriodType periodType;

    /***/
    public TimePeriod(long id, String title, long parentId, Date startDate, TimePeriodType periodType) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.startDate = startDate;
        this.periodType = periodType;
    }

}
