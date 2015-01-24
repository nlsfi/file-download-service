/**
 * Yleiskarttarasteri 1:8 000 000
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'yleiskarttarasteri_8000k_jhs180',
	fileIdentifier : '72f22116-b877-4c02-8833-366134f96e06',
	pattern : /\/tuotteet\/yleiskarttarasteri_8000k_jhs180\/(\S*)\/(\S*)\/(\S*)\/\S*.png/i,
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
