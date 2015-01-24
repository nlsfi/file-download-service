/**
 * Korkeusmalli 10m
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'korkeusmalli_10m',
	fileIdentifier : 'cd640425-315f-4b12-86c5-192e98701dcb',
	pattern : /\/tuotteet\/korkeusmalli_10m\/(\S*)\/(\S*)\/(\S*)\/[A-Z][0-9]\/[A-Z][0-9][0-9]\/(\S*).(?:zip|png)/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings),
       defaultMapper(MetadataProperty.NLS_GRIDCELL)
	],
	callback : function(node,outputProperties) {
		 if (outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT).equals("PNG")) {
             var arr = getWorldFile(node,outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
             outputProperties.put(MetadataProperty.NLS_RELATED, arr);
		 }
	}
});
