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
 * defaultMapper maps given value directly to a string 
 * without transformations
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
	return function(value,outputProperties) {
		outputProperties.put(outputProperty, value.replace(',','.'));	
	};
}

function dateFormattingMapper(outputProperty, formatString) {
	return function(value,outputProperties) {
		var dateFormat = new Packages.java.text.SimpleDateFormat(formatString);
		var date = dateFormat.parse(value);
		outputProperties.put(outputProperty, date);	
	};
}

/**
 * lookupTableMapper uses a lookup table to replace the value
 * and maps the result to a string
 */
function lookupTableMapper(outputProperty, lookupTable) {
   return function(value, outputProperties) {
		outputProperties.put(outputProperty, lookupTable[value]);
   };
}

function getPropertiesFromCsv(node,encoding,mappers,outputProperties) {
	
	var result = parseCSV(getStreamFromNode(node), ';', encoding);
	for (prop in result) {
		if (mappers.hasOwnProperty(prop)) {
			mappers[prop](result[prop],outputProperties);
		}
		// todo: unable to handle property
	}
}


function getPropertiesFromPath(pattern,path, mappers,outputProperties) {

	var pathVariables = pattern.exec(path);
	if (pathVariables.length > 1) {
 	// pathVariables[0] is the whole path string, let's remove that
		pathVariables.splice(0,1);
		
		for (var i=0;i<pathVariables.length;i++) {
			if (typeof pathVariables[i] !== "undefined") {
				mappers[i](pathVariables[i],outputProperties);
			}
		}
	}
}

function csvPropertiesHandler(outputProperties, mapper) {
	return function(name,value) {
		if (mapper.hasOwnProperty(name)) {
			mapper[name](value,outputProperties);
		} else {
			// mapping not provided
			// so we just out the value as it is
			// with the name from the csv
			outputProperties.put(name,value);
		}
	};
}

function getStreamFromNode(node) {
	if (node.hasNode("jcr:content")) {
		var contentNode = node.getNode("jcr:content");
		var data = contentNode.getProperty("jcr:data");
		return data.getBinary().getStream();
	} else {
		throw Exception("jcr:content node not found for " + node.path);
	}
}

importPackage(java.io);

function getStreamFromFile(path, encoding) {
	return new FileInputStream(path, encoding);
}

function parseCSV(istream,separatorChar,encoding)	{
	var line;
	var reader = null;
	try {
		var result = {};
		reader = new BufferedReader(new InputStreamReader(istream,encoding));
		while((line = reader.readLine()) != null) {
			var items = line.split(separatorChar);
			if (items.length == 2) {
				result[items[0]] = items[1].trim();
			} // skip invalid row..
		}
		return result;
	} catch(e) {
		throw(e);
	} finally {
		if (reader != null)
			reader.close();
	}
}

var handlerRegistry = (function () {
	var datasetHandlers = new Array();
	return {
		registerDatasetHandler : function (handler) {
			datasetHandlers.push(handler);
		},
		getDatasetHandler : function (path) {
			for each(var handler in datasetHandlers) {
				if (handler.supportsPath(path)) {
					return handler;
				}
			}
			
			return null;
		},
		getDatasetHandlers : function() {
			return datasetHandlers;
		}
	};
}());

function processNode(node, outputProperties) {
	var handler = handlerRegistry.getDatasetHandler(node.path);
	if (handler != null) {
		handler.processNode(node,outputProperties);
	}
}
