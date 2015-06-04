var MetadataProperty;
if (typeof importClass != "function") { // check for Nashorn (Java8) vs Rhino (Java6/7)
    MetadataProperty = Java.type("fi.nls.fileservice.jcr.MetadataProperty");
} else {
    MetadataProperty = Packages.fi.nls.fileservice.jcr.MetadataProperty;
}

function getStringArray(length) {
    if (typeof importClass != "function") { // check for Nashorn (Java8) vs Rhino (Java6/7)
        var StringArr = Java.type("java.lang.String[]");
        return new StringArr(length);
    } else {
        return java.lang.reflect.Array.newInstance(java.lang.String, length);
    }
}

// lookup tables (mappings) are used to convert parameters from dataset paths
// to canonical file service format
var crsMappings = {};
crsMappings['etrs89'] = 'etrs-tm35fin';
crsMappings['etrs-tm35fin'] = 'etrs-tm35fin';
crsMappings['TM35FIN'] = 'etrs-tm35fin';
crsMappings['tm35fin'] = 'etrs-tm35fin';

var formatMappings = {};
formatMappings['gml'] = 'XML/GML';
formatMappings['shp'] = 'ESRI shape';
formatMappings['shape'] = 'ESRI shape';
formatMappings['mif'] = 'MapInfo mif';
formatMappings['jp2'] = 'JPEG2000';
formatMappings['tif'] = 'TIFF';
formatMappings['tiff'] = 'TIFF';
formatMappings['ascii_grid'] = 'ascii grid';
formatMappings['ascii_xyz'] = 'ascii xyz';
formatMappings['laz'] = 'LAZ';
formatMappings['txt'] = 'txt';
formatMappings['png'] = 'PNG';

/**
 * defaultMapper maps given value directly to a string without transformations
 */
function defaultMapper(outputProperty) {
    return function(value, outputProperties) {
        outputProperties.put(outputProperty, value);
    };
}

function noopMapper() {
    return function() {};
}

function decimalLocaleConversionMapper(outputProperty) {
    return function(value, outputProperties) {
        outputProperties.put(outputProperty, value.replace(',', '.'));
    };
}

function dateFormattingMapper(outputProperty, formatString) {
    return function(value, outputProperties) {
        var dateFormat = new Packages.java.text.SimpleDateFormat(formatString);
        var date = dateFormat.parse(value);
        outputProperties.put(outputProperty, date);
    };
}

/**
 * lookupTableMapper uses a lookup table to replace the value and maps the
 * result to a string
 */
function lookupTableMapper(outputProperty, lookupTable) {
    return function(value, outputProperties) {
        outputProperties.put(outputProperty, lookupTable[value]);
    };
}

function getPropertiesFromCsv(node, encoding, mappers, outputProperties) {
    var result = parseCSV(getStreamFromNode(node), ';', encoding);
    for (prop in result) {
        if (mappers.hasOwnProperty(prop)) {
            mappers[prop](result[prop], outputProperties);
        }
        //TODO: unable to handle property
    }
}

function getPropertiesFromPath(pattern, path, mappers, outputProperties) {
    var pathVariables = pattern.exec(path);
    if (pathVariables.length > 1) {
        // pathVariables[0] is the whole path string, let's remove that
        pathVariables.splice(0, 1);
        for (var i = 0; i < pathVariables.length; i++) {
            if (typeof pathVariables[i] !== "undefined") {
                mappers[i](pathVariables[i], outputProperties);
            }
        }
    }
}

function csvPropertiesHandler(outputProperties, mapper) {
    return function(name, value) {
        if (mapper.hasOwnProperty(name)) {
            mapper[name](value, outputProperties);
        } else {
            // mapping not provided
            // so we just out the value as it is
            // with the name from the csv
            outputProperties.put(name, value);
        }
    };
}

/**
 * Opens InputStream to javax.jcr.Node (assumes nt:file nodeType)
 * @param {javax.jcr.Node} node - node
 * @returns java.io.InputStream
 */
function getStreamFromNode(node) {
    if (node.hasNode("jcr:content")) {
        var contentNode = node.getNode("jcr:content");
        var data = contentNode.getProperty("jcr:data");
        return data.getBinary().getStream();
    } else {
        throw Exception("jcr:content node not found for " + node.path);
    }
}

/**
 * Opens InputStream from a file
 *
 * @param {string} path - path to file
 * @param {string} encoding - file character encoding
 * @returns java.io.InputStream
 */
function getStreamFromFile(path) {
    return new java.io.FileInputStream(path);
}

/**
 * Parses a CSV file and returns name value pairs as js object
 *
 * @param {java.io.InputStream} istream - InputStream to file
 * @param {string} separatorChar - separator character
 * @param {string} encoding - CSV file character encoding
 * @returns
 */
function parseCSV(istream, separatorChar, encoding) {
    var line;
    var reader = null;
    try {
        var result = {};
        reader = new java.io.BufferedReader(new java.io.InputStreamReader(istream, encoding));
        while ((line = reader.readLine()) != null) {
            var items = line.split(separatorChar);
            if (items.length == 2) {
                result[items[0]] = items[1].trim();
            } // skip invalid row..
        }
        return result;
    } catch (e) {
        throw (e);
    } finally {
        if (reader != null)
            reader.close();
    }
}

/**
 * Returns file names of raster world files as java string array
 * Assumes filename.tab, filename.tfw for TIFF files, filename.pgw for PNG files
 *
 * Does not check the existence of these files
 *
 * @param {javax.jcr.Node} node - node reference to raster file
 * @param {string} - file format
 * @returns {java.lang.String[]} Java String array
 */
function getWorldFile(node, format) {
    var name = node.getName();
    name = name.substring(0, name.lastIndexOf("."));
    if ("TIFF" == format) {
        var arr = getStringArray(2);
        arr[0] = name + ".tab";
        arr[1] = name + ".tfw";
        return arr;
    } else if ("PNG" == format) {
        var arr = getStringArray(1);
        arr[0] = name + ".pgw";
        return arr;
    } else {
        throw "Unknown file format: " + format;
    }
}

// DEPRECATED
// Use datasetHandler / datasetDefinitions instead
var handlerRegistry = (function() {
    var datasetHandlers = new Array();
    return {
        registerDatasetHandler: function(handler) {
            datasetHandlers.push(handler);
        },
        getDatasetHandler: function(path) {
            for each(var handler in datasetHandlers) {
                if (handler.supportsPath(path)) {
                    return handler;
                }
            }

            return null;
        },
        getDatasetHandlers: function() {
            return datasetHandlers;
        }
    };
}());

var datasetHandler = (function() {
    var datasetDefinitions = new Array();
    return {
        addDatasetDefinition: function(datasetDef) {
            datasetDefinitions.push(datasetDef);
        },
        processNode: function(node, outputProperties) {
            var path = node.getPath();
            for each(var datasetDef in datasetDefinitions) {
                    if (path.match(datasetDef.pattern)) {
                        node.addMixin("nls:datasetfile");
                        node.addMixin("gmd:metadata");
                        if (datasetDef.datasetName) {
                            outputProperties.put(MetadataProperty.NLS_DATASET, datasetDef.datasetName);
                        }

                        if (datasetDef.fileIdentifier) {
                            outputProperties.put(MetadataProperty.GMD_FILEIDENTIFIER, datasetDef.fileIdentifier);
                        }

                        if (datasetDef.mappers && datasetDef.mappers.length > 0) {
                            getPropertiesFromPath(datasetDef.pattern, path, datasetDef.mappers, outputProperties);
                        }

                        if (datasetDef.callback) {
                            datasetDef.callback(node, outputProperties);
                        }
                        return true;
                    }
                }
                // No dataset definition found for node
            return false;
        }
    };
}());

/**
 * processNode is the entry point to JavaScript from Java (JavaScriptMetadataServiceExecutor)
 * @param {javax.jcr.Node} node - JCR node for processing
 * @param {java.util.Map<String,Object} outputProperties - file properties to be added
 */
function processNode(node, outputProperties) {
    if (!datasetHandler.processNode(node, outputProperties)) {
        // fallback to legacy mode (deprecated)
        if (handlerRegistry) {
            var handler = handlerRegistry.getDatasetHandler(node.path);
            if (handler != null) {
                handler.processNode(node, outputProperties);
            }
        }
    }
}