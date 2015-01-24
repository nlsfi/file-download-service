/**
 * Taustakarttasarja
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'taustakarttasarja',
	fileIdentifier : 'c22da116-5095-4878-bb04-dd7db3a1a341',
	pattern : /\/tuotteet\/taustakarttasarja\/(\w*)\/\w*\/(\w*)\/(\w*)\/(?:\w*\/){0,2}(?:\d*|U(\w*)).png/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_GRIDCELL)
	],
	callback : function(node, outputProperties) {
		var arr = getWorldFile(node, outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
		outputProperties.put(MetadataProperty.NLS_RELATED, arr);
	}
});
