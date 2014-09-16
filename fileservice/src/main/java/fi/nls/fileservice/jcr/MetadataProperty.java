package fi.nls.fileservice.jcr;

public class MetadataProperty {

    // dataset
    public static final String PATH = "path";
    public static final String VALUE = "value";
    public static final String ISPUBLISHED = "isPublished";
    public static final String LASTMODIFIED = "lastModified";

    // nls:retention
    public static final String NLS_EXPIRES = "nls:expires";

    // nls:metadata
    public static final String NLS_GRIDCELL = "nls:gridCell";
    public static final String NLS_GRIDSYSTEM = "nls:gridSystem";
    public static final String NLS_GRIDSCALE = "nls:gridScale";
    public static final String NLS_WMSLAYER = "nls:wmsLayer";

    public static final String NLS_WMSMINSCALE = "nls:wmsMinScale";
    public static final String NLS_WMSMAXSCALE = "nls:wmsMaxScale";

    public static final String NLS_CATEGORY = "nls:category";
    public static final String NLS_DATASET = "nls:dataset";
    public static final String NLS_DATASETVERSION = "nls:datasetVersion";
    public static final String NLS_PUBLISHED = "nls:published";
    public static final String NLS_RELATED = "nls:related";
    public static final String NLS_CRS = "nls:crs";
    public static final String NLS_INFO = "nls:info";
    public static final String NLS_FILEVERSION = "nls:fileVersion";
    public static final String NLS_YEAR = "nls:year";

    public static final String NLS_PREVMODIFIED = "nls:prevModified";
    public static final String NLS_FILECHANGED = "nls:fileChanged";

    // gmd:metadata
    public static final String GMD_FILEIDENTIFIER = "gmd:fileIdentifier";
    public static final String GMD_TITLE = "gmd:title";
    public static final String GMD_DISTRIBUTIONFORMAT = "gmd:distributionFormat";

    // nls:orthophoto
    public static final String NLS_YEAROFPHOTOGRAPHY = "nls:yearOfPhotography";
    public static final String NLS_ORTHOPHOTOID = "nls:orthophotoID";
    public static final String NLS_ELEVATIONMODEL = "nls:elevationModel";

    // nls:lidar
    public static final String NLS_YEAROFSCANNING = "nls:yearOfScanning";
    public static final String NLS_POINTCLOUDID = "nls:pointCloudID";
    public static final String NLS_PROJECT = "nls:project";
    public static final String NLS_SCANNER = "nls:scanner";
    public static final String NLS_MULTIPULSE = "nls:multipulse";
    public static final String NLS_POINTDENSITY = "nls:pointDensity";
    public static final String NLS_DATEOFSCANNING = "nls:dateOfScanning";
    public static final String NLS_FLIGHTALTITUDE = "nls:flightAltitude";
    public static final String NLS_ELEVATIONPRECISION = "nls:elevationPrecision";
    public static final String NLS_ELEVATIONSYSTEM = "nls:elevationSystem";
    public static final String NLS_TIMEFRAME = "nls:timeframe";

    /*
     * [nls:retention] mixin - nls:expires (LONG)
     * 
     * [nls:datasetfile] mixin - nls:gridCell (STRING) - nls:gridSystem (STRING)
     * - nls:gridScale (LONG) - nls:wmsLayer (STRING) - nls:category (STRING)
     * multiple - nls:datasetVersion (STRING) - nls:published (BOOLEAN) -
     * nls:related (STRING) multiple - nls:crs (STRING) - nls:year (LONG) -
     * nls:info (STRING) - nls:fileVersion (LONG) - nls:elevationModel (STRING)
     * 
     * [gmd:metadata] mixin - gmd:fileIdentifier (STRING) - gmd:title (STRING)
     * multiple - gmd:distributionFormat (STRING) multiple
     * 
     * [nls:orthophoto] mixin - nls:orthophotoYear (LONG) - nls:orthophotoID
     * (LONG)
     * 
     * [nls:lidar] mixin - nls:project (STRING) - nls:scanner (STRING) -
     * nls:multipulse (STRING) - nls:dateOfScan (DATE) - nls:pointDensity
     * (DOUBLE) - nls:pointCloudID (STRING) - nls:flightAltitude (LONG) -
     * nls:elevationAccuracy (DOUBLE) - nls:timeWindow (STRING)
     */
}
