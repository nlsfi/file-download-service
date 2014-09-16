/**
 * Korkeusvy�hykerasteri
 * Metadatan k�sittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {

	var fileIdentifier = '5c2ba253-e1b0-42c8-b9bb-3bac947e1cf1';
	var datasetName = 'korkeusvyohyke';
	var pattern = /\/tuotteet\/korkeusvyohyke\/(\w*)\/(\w*)\/(\w*)(?:\/[A-Z][0-9]{1,2}){0,2}\/(\w*)\w{4}.png$/i;
	
	var mappers = new Array();
	mappers.push(defaultMapper(MetadataProperty.NLS_DATASETVERSION));
	mappers.push(lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings));
	mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
	mappers.push(function(value, outputProperties) {
			// special case for 640m (KM10_S640.png) where whole finland is in one file
			// and thus no gridcell id exists
		if (value.indexOf("_") < 0) {
			outputProperties.put(MetadataProperty.NLS_GRIDCELL, value);
		}
	});
		
	var handler = {
			supportsPath: function (path) {
				return path.match(pattern);
			},
			fileIdentifier : fileIdentifier,
			processNode: function (node, outputProperties) {
				node.addMixin('nls:datasetfile');
				node.addMixin('gmd:metadata');
				
				var name = node.getName();
				name = name.substring(0,name.lastIndexOf("."));
				
				var arr = java.lang.reflect.Array.newInstance(java.lang.String, 1);
				arr[0] = name + ".pgw";
				outputProperties.put(MetadataProperty.NLS_RELATED, arr);
				
				getPropertiesFromPath(pattern,node.path,mappers,outputProperties);
				outputProperties.put(MetadataProperty.GMD_FILEIDENTIFIER,fileIdentifier);
				outputProperties.put(MetadataProperty.NLS_DATASET,datasetName);
			}
	};
	
	return handler;
		
}()));