/**
 * Peruskarttarasteri
 **/

datasetHandler.addDatasetDefinition({
	datasetName : 'peruskarttarasteri_jhs180',
	fileIdentifier : 'c6e94f34-4925-4fa6-bac9-6b25f4e7cebf',
	pattern : /\/tuotteet\/peruskarttarasteri_jhs180\/(\S*)\/\S*\/(\S*)\/(\S*)\/\S*\/\S*\/(\S*).png/i,
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
