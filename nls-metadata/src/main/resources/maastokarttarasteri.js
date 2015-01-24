/**
 * Maastokarttarasteri 1: 50 000
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'maastokarttarasteri',
	fileIdentifier : 'd47ac165-6abd-4357-a4f9-a6f17e2b0c58',
	pattern : /\/tuotteet\/maastokarttarasteri\/(\S*)\/\S*\/(\S*)\/(\S*)\/\S*\/\S*\/U([a-zA-Z][0-9]*)_\S*.tif/i,
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
