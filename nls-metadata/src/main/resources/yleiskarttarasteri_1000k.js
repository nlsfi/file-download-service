/**
 * Yleiskarttarasteri 1:1 000 000
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'yleiskarttarasteri_1000k',
	fileIdentifier : '980fa404-75d3-4afd-b97e-bf1a9e392cd9',
	pattern : /\/tuotteet\/yleiskarttarasteri_1000k\/(\S*)\/(\S*)\/(\S*)\/\S*.png/i,
	mappers : [
	   defaultMapper(MetadataProperty.NLS_DATASETVERSION),
       lookupTableMapper(MetadataProperty.NLS_CRS, crsMappings),
       lookupTableMapper(MetadataProperty.GMD_DISTRIBUTIONFORMAT, formatMappings)
	],
	callback : function(node, outputProperties) {
		var arr = getWorldFile(node, outputProperties.get(MetadataProperty.GMD_DISTRIBUTIONFORMAT));
		outputProperties.put(MetadataProperty.NLS_RELATED, arr);
	}
});
