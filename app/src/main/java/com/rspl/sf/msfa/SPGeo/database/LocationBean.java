package com.rspl.sf.msfa.SPGeo.database;

public class LocationBean {
    public LocationBean() {
    }

    public LocationBean(String spNo, String SPNAME, String LAT,
                        String LONG, String STARTDATE,
                        String STARTTIME, String Status, String TEMPNO, String TimeStamp, String AppVisible, String batteryLevel, String distance) {
        this.COLUMN_SPNO = spNo;
        this.COLUMN_SPNAME = SPNAME;
        this.COLUMN_LAT = LAT;
        this.COLUMN_LONG = LONG;
        this.COLUMN_STARTDATE = STARTDATE;
        this.COLUMN_STARTTIME = STARTTIME;
        this.COLUMN_Status = Status;
        this.COLUMN_TEMPNO = TEMPNO;
        this.COLUMN_TIMESTAMP = TimeStamp;
        this.COLUMN_AppVisibility = AppVisible;
        this.COLUMN_BATTERYLEVEL = batteryLevel;
        this.COLUMN_DISTANCE = distance;
    }
    public static final String TABLE_NAME = "Locations";

    public String getColumnSpno() {
        return COLUMN_SPNO;
    }

    public  void setColumnSpno(String columnSpno) {
        COLUMN_SPNO = columnSpno;
    }

    public String getColumnSpname() {
        return COLUMN_SPNAME;
    }

    public  void setColumnSpname(String columnSpname) {
        COLUMN_SPNAME = columnSpname;
    }

    public String getColumnLat() {
        return COLUMN_LAT;
    }

    public  void setColumnLat(String columnLat) {
        COLUMN_LAT = columnLat;
    }

    public String getColumnLong() {
        return COLUMN_LONG;
    }

    public  void setColumnLong(String columnLong) {
        COLUMN_LONG = columnLong;
    }

    public String getColumnStartdate() {
        return COLUMN_STARTDATE;
    }

    public  void setColumnStartdate(String columnStartdate) {
        COLUMN_STARTDATE = columnStartdate;
    }

    public String getColumnStarttime() {
        return COLUMN_STARTTIME;
    }

    public  void setColumnStarttime(String columnStarttime) {
        COLUMN_STARTTIME = columnStarttime;
    }

    public String getCOLUMN_Status() {
        return COLUMN_Status;
    }

    public  void setCOLUMN_Status(String COLUMN_Status) {
        COLUMN_Status = COLUMN_Status;
    }

    public String getColumnTempno() {
        return COLUMN_TEMPNO;
    }

    public  void setColumnTempno(String columnTempno) {
        COLUMN_TEMPNO = columnTempno;
    }

    public String getColumnTimestamp() {
        return COLUMN_TIMESTAMP;
    }

    public  void setColumnTimestamp(String columnTimestamp) {
        COLUMN_TIMESTAMP = columnTimestamp;
    }

    public String COLUMN_SPNO = "";
    public String COLUMN_SPNAME = "";
    public String COLUMN_LAT = "";
    public String COLUMN_LONG = "";
    public String COLUMN_STARTDATE = "";
    public String COLUMN_STARTTIME = "";
    public String COLUMN_Status = "";
    public String COLUMN_TEMPNO = "";
    public String COLUMN_TIMESTAMP = "";

    public String getCOLUMN_DISTANCE() {
        return COLUMN_DISTANCE;
    }

    public void setCOLUMN_DISTANCE(String COLUMN_DISTANCE) {
        this.COLUMN_DISTANCE = COLUMN_DISTANCE;
    }

    public String COLUMN_DISTANCE = "";

    public String getCOLUMN_BATTERYLEVEL() {
        return COLUMN_BATTERYLEVEL;
    }

    public void setCOLUMN_BATTERYLEVEL(String COLUMN_BATTERYLEVEL) {
        this.COLUMN_BATTERYLEVEL = COLUMN_BATTERYLEVEL;
    }

    public String COLUMN_BATTERYLEVEL = "";

    public String getCOLUMN_AppVisibility() {
        return COLUMN_AppVisibility;
    }

    public void setCOLUMN_AppVisibility(String COLUMN_AppVisibility) {
        this.COLUMN_AppVisibility = COLUMN_AppVisibility;
    }

    public String COLUMN_AppVisibility = "";

    public String getCOLUMN_ID() {
        return COLUMN_ID;
    }

    public void setCOLUMN_ID(String COLUMN_ID) {
        this.COLUMN_ID = COLUMN_ID;
    }

    public String COLUMN_ID = "";


    public static String COLUMNID = "ID";
    public static String COLUMNSPNO = "SPNO";
    public static String COLUMNSPNAME = "SPNAME";
    public static String COLUMNLAT = "Latitude";
    public static String COLUMNLONG = "Longitude";
    public static String COLUMNSTARTDATE = "StartDate";
    public static String COLUMNSTARTTIME = "StartTime";
    public static String COLUMNStatus = "StatusUpdate";
    public static String COLUMNTEMPNO = "LocationSeqNo";
    public static String COLUMNTIMESTAMP = "Timestamp";
    public static String COLUMNAPPVISBILITY = "AppVisibility";
    public static String COLUMNBATTERYLEVEL = "BatteryLevel";
    public static String COLUMNDISTANCE = "Distance";

    private int id;
    private String note;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("+COLUMNID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + COLUMNSPNO + " TEXT,"
                    + COLUMNSPNAME + " TEXT," +
                    ""+COLUMNLAT + " TEXT," +
                    ""+COLUMNLONG + " TEXT,"
                    +COLUMNSTARTDATE + " TEXT,"+COLUMNSTARTTIME + " TEXT," +
                    ""+COLUMNStatus + " TEXT,"+COLUMNTEMPNO + " TEXT,"+COLUMNTIMESTAMP + " TEXT,"+ COLUMNAPPVISBILITY + " TEXT," +COLUMNBATTERYLEVEL + " TEXT," +COLUMNDISTANCE + " TEXT)";





}
