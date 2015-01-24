/**
 * Yleiskarttarasteri 1:4 500 000
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'yleiskarttarasteri_4500k_jhs180',
	fileIdentifier : 'bb491154-4f95-4b47-b0a3-cf9e1a0a78cc',
	pattern : /\/tuotteet\/yleiskarttarasteri_4500k_jhs180\/(\S*)\/(\S*)\/(\S*)\/\S*.png/i,
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
