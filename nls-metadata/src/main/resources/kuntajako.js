/**
 * Kuntajako
 * Metadatan k�sittely 
 **/

importClass(Packages.fi.nls.fileservice.jcr.MetadataProperty);

handlerRegistry.registerDatasetHandler((function () {

	var fileIdentifier = 'da40f862-44b5-47b9-aea8-83bb1e640ca9';
	var datasetName = 'kuntajako';
	var pattern = /\/tuotteet\/kuntajako\/(\S*)\/(\S*)\/(\S*)\/[a-z][A-Z]*_(\d*)_\S*.zip/i; 
				   
	var mappers = new Array();
	mappers.push(defaultMapper(MetadataProperty.NLS_DATASETVERSION));
	mappers.push(lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings));
	mappers.push(lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings));
	mappers.push(defaultMapper(MetadataProperty.NLS_YEAR));

	var handler = {
			supportsPath: function (path) {
				return path.match(pattern);
			},
			fileIdentifier : fileIdentifier,
			processNode: function (node, outputProperties) {
				node.addMixin('nls:datasetfile');
				node.addMixin('gmd:metadata');
				getPropertiesFromPath(pattern,node.path,mappers,outputProperties);
				outputProperties.put(MetadataProperty.GMD_FILEIDENTIFIER,fileIdentifier);
				outputProperties.put(MetadataProperty.NLS_DATASET,datasetName);
			}
	};
	
	return handler;
		
}()));